package com.queentech.data.usecase.billing

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.queentech.data.database.datastore.UserLocalDataSource
import com.queentech.data.model.billing.ReceiptRequest
import com.queentech.data.model.billing.SubscriptionQueryRequest
import com.queentech.data.model.service.BillingService
import com.queentech.domain.model.billing.SubscriptionProduct
import com.queentech.domain.model.billing.SubscriptionStatus
import com.queentech.domain.model.billing.SubscriptionVerificationState
import com.queentech.domain.model.login.User
import com.queentech.domain.usecase.billing.BillingRepository
import com.queentech.domain.usecase.login.UserRepository
import com.queentech.domain.usecase.lotto.LottoIssueRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingRepositoryImpl @Inject constructor(
    private val billingClientWrapper: BillingClientWrapper,
    private val billingService: BillingService,
    private val userLocalDataSource: UserLocalDataSource,
    private val userRepository: UserRepository,
    private val lottoIssueRepository: LottoIssueRepository,
) : BillingRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _subscriptionStatus = MutableStateFlow(
        SubscriptionStatus(isActive = false, productId = null, expiryTimeMillis = null, autoRenewing = false)
    )
    override val subscriptionStatus: Flow<SubscriptionStatus> = _subscriptionStatus.asStateFlow()

    private val _expectedNumberResetEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val expectedNumberResetEvents: Flow<Unit> = _expectedNumberResetEvents.asSharedFlow()

    private var cachedProductDetails = mutableMapOf<String, com.android.billingclient.api.ProductDetails>()

    init {
        scope.launch {
            billingClientWrapper.purchasesUpdated.collect { (billingResult, purchases) ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    handlePurchases(purchases)
                }
            }
        }
        scope.launch {
            refreshSubscriptionStatus()
        }
    }

    override suspend fun querySubscriptionProducts(): Result<List<SubscriptionProduct>> = runCatching {
        val productDetailsList = billingClientWrapper.queryProductDetails(PRODUCT_IDS)
        productDetailsList.also { list ->
            list.forEach { cachedProductDetails[it.productId] = it }
        }.mapNotNull { details ->
            val offerDetails = details.subscriptionOfferDetails?.firstOrNull() ?: return@mapNotNull null
            val pricingPhase = offerDetails.pricingPhases.pricingPhaseList.firstOrNull() ?: return@mapNotNull null

            SubscriptionProduct(
                productId = details.productId,
                name = details.name,
                description = details.description,
                formattedPrice = pricingPhase.formattedPrice,
                billingPeriod = pricingPhase.billingPeriod,
                priceAmountMicros = pricingPhase.priceAmountMicros,
            )
        }
    }

    override suspend fun launchSubscriptionFlow(activityContext: Any, productId: String): Result<Unit> = runCatching {
        val activity = activityContext as Activity
        val productDetails = cachedProductDetails[productId]
            ?: billingClientWrapper.queryProductDetails(listOf(productId)).firstOrNull()
            ?: throw IllegalStateException("Product not found: $productId")

        cachedProductDetails[productId] = productDetails

        val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken
            ?: throw IllegalStateException("No offer available for: $productId")

        val billingResult = billingClientWrapper.launchBillingFlow(activity, productDetails, offerToken)
        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            throw IllegalStateException("Billing flow failed: ${billingResult.debugMessage}")
        }
    }

    private suspend fun handlePurchases(purchases: List<Purchase>) {
        for (purchase in purchases) {
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
                val receiptSent = sendReceiptToServer(purchase, resetExpectedNumbersOnSuccess = true)
                if (receiptSent) {
                    val acknowledged = billingClientWrapper.acknowledgePurchase(purchase.purchaseToken)
                    if (!acknowledged) {
                        Log.e(TAG, "Failed to acknowledge purchase: ${purchase.orderId}")
                    }
                }
            }
        }
    }

    private suspend fun sendReceiptToServer(
        purchase: Purchase,
        resetExpectedNumbersOnSuccess: Boolean,
    ): Boolean {
        return try {
            val email = userLocalDataSource.userFlow.firstOrNull()?.email
            val productId = purchase.products.firstOrNull() ?: ""
            val request = ReceiptRequest(
                orderId = purchase.orderId ?: "",
                productId = productId,
                purchaseToken = purchase.purchaseToken,
                purchaseTime = purchase.purchaseTime,
                autoRenewing = purchase.isAutoRenewing,
                email = email,
            )
            val response = billingService.sendReceipt(request)
            val expiryMillis = response.expiryTimeMillis
            if (response.success && response.isEntitled == true && expiryMillis != null && expiryMillis > System.currentTimeMillis()) {
                Log.d(TAG, "Receipt sent successfully: ${purchase.orderId}")
                _subscriptionStatus.value = SubscriptionStatus(
                    isActive = true,
                    productId = productId,
                    expiryTimeMillis = expiryMillis,
                    autoRenewing = purchase.isAutoRenewing,
                    verificationState = SubscriptionVerificationState.VERIFIED,
                )
                userRepository.updateTier(User.TIER_PREMIUM)

                if (resetExpectedNumbersOnSuccess) {
                    resetCurrentWeekExpectedNumbers()
                }
                true
            } else {
                Log.e(TAG, "Receipt send failed: ${response.message}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send receipt to server", e)
            false
        }
    }

    private suspend fun resetCurrentWeekExpectedNumbers() {
        try {
            lottoIssueRepository.deleteWeek(getCurrentWeekStartMillis())
            _expectedNumberResetEvents.emit(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to reset current-week expected numbers", e)
        }
    }

    private fun getCurrentWeekStartMillis(): Long {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
        calendar.add(Calendar.DAY_OF_YEAR, -(calendar.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY))
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    override suspend fun refreshSubscriptionStatus(): Result<SubscriptionStatus> {
        val purchases = billingClientWrapper.queryPurchases().getOrElse {
            setVerificationFailed()
            return Result.failure(it)
        }
        val activePurchase = purchases.firstOrNull {
            it.purchaseState == Purchase.PurchaseState.PURCHASED &&
                it.products.any(PRODUCT_IDS::contains)
        }

        if (activePurchase != null) {
            val productId = activePurchase.products.firstOrNull(PRODUCT_IDS::contains)
            val serverResponse = try {
                val response = billingService.querySubscription(
                    SubscriptionQueryRequest(
                        purchaseToken = activePurchase.purchaseToken,
                        productId = productId ?: "",
                    )
                )
                response
            } catch (e: Exception) {
                Log.w(TAG, "Failed to query subscription from server", e)
                setVerificationFailed()
                return Result.failure(e)
            }

            val expiryMillis = serverResponse.expiryTimeMillis
            val isEntitled = serverResponse.success &&
                serverResponse.isEntitled == true &&
                serverResponse.productId == productId &&
                expiryMillis != null &&
                expiryMillis > System.currentTimeMillis()

            if (!isEntitled) {
                _subscriptionStatus.value = SubscriptionStatus(
                    isActive = false,
                    productId = null,
                    expiryTimeMillis = null,
                    autoRenewing = false,
                    verificationState = if (serverResponse.success) {
                        SubscriptionVerificationState.VERIFIED
                    } else {
                        SubscriptionVerificationState.FAILED
                    },
                )
                if (serverResponse.success) userRepository.updateTier(User.TIER_FREE)
                return if (serverResponse.success) {
                    Result.success(_subscriptionStatus.value)
                } else {
                    Result.failure(IllegalStateException("Failed to verify subscription"))
                }
            }

            _subscriptionStatus.value = SubscriptionStatus(
                isActive = true,
                productId = productId,
                expiryTimeMillis = expiryMillis,
                autoRenewing = activePurchase.isAutoRenewing,
                cancelAtPeriodEnd = serverResponse.cancelAtPeriodEnd ?: false,
                isOnHold = serverResponse.isOnHold ?: false,
                verificationState = SubscriptionVerificationState.VERIFIED,
            )
            userRepository.updateTier(User.TIER_PREMIUM)
        } else {
            _subscriptionStatus.value = SubscriptionStatus(
                isActive = false,
                productId = null,
                expiryTimeMillis = null,
                autoRenewing = false,
                verificationState = SubscriptionVerificationState.VERIFIED,
            )
            userRepository.updateTier(User.TIER_FREE)
        }
        return Result.success(_subscriptionStatus.value)
    }

    private fun setVerificationFailed() {
        _subscriptionStatus.value = SubscriptionStatus(
            isActive = false,
            productId = null,
            expiryTimeMillis = null,
            autoRenewing = false,
            verificationState = SubscriptionVerificationState.FAILED,
        )
    }

    companion object {
        private const val TAG = "BillingRepositoryImpl"
        val PRODUCT_IDS = listOf("fisherlotto_monthly")
    }
}
