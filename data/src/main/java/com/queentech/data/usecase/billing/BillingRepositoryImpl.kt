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
import com.queentech.domain.model.login.User
import com.queentech.domain.usecase.billing.BillingRepository
import com.queentech.domain.usecase.login.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingRepositoryImpl @Inject constructor(
    private val billingClientWrapper: BillingClientWrapper,
    private val billingService: BillingService,
    private val userLocalDataSource: UserLocalDataSource,
    private val userRepository: UserRepository,
) : BillingRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _subscriptionStatus = MutableStateFlow(
        SubscriptionStatus(isActive = false, productId = null, expiryTimeMillis = null, autoRenewing = false)
    )
    override val subscriptionStatus: Flow<SubscriptionStatus> = _subscriptionStatus.asStateFlow()

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

    override suspend fun restorePurchases(): Result<SubscriptionStatus> = runCatching {
        refreshSubscriptionStatus()
        _subscriptionStatus.value
    }

    private suspend fun handlePurchases(purchases: List<Purchase>) {
        for (purchase in purchases) {
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
                val acknowledged = billingClientWrapper.acknowledgePurchase(purchase.purchaseToken)
                if (acknowledged) {
                    sendReceiptToServer(purchase)
                } else {
                    Log.e(TAG, "Failed to acknowledge purchase: ${purchase.orderId}")
                }
            }
        }
        refreshSubscriptionStatus()
    }

    private suspend fun sendReceiptToServer(purchase: Purchase) {
        try {
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
            if (response.success) {
                Log.d(TAG, "Receipt sent successfully: ${purchase.orderId}")
                // 서버에서 반환한 정확한 만료일로 구독 상태 즉시 업데이트
                val expiryMillis = response.expiryTimeMillis
                    ?: estimateExpiryTime(purchase.purchaseTime, productId)
                _subscriptionStatus.value = SubscriptionStatus(
                    isActive = true,
                    productId = productId,
                    expiryTimeMillis = expiryMillis,
                    autoRenewing = purchase.isAutoRenewing,
                )
            } else {
                Log.e(TAG, "Receipt send failed: ${response.message}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send receipt to server", e)
        }
    }

    override suspend fun refreshSubscriptionStatus() {
        val purchases = billingClientWrapper.queryPurchases() ?: run {
            Log.w(TAG, "Failed to query purchases, keeping current status")
            return
        }
        val activePurchase = purchases.firstOrNull { it.purchaseState == Purchase.PurchaseState.PURCHASED }

        val isActive = activePurchase != null

        if (activePurchase != null) {
            val productId = activePurchase.products.firstOrNull()

            // 서버를 통해 Google Play Developer API에서 정확한 만료일 조회
            val serverExpiry = try {
                val response = billingService.querySubscription(
                    SubscriptionQueryRequest(
                        purchaseToken = activePurchase.purchaseToken,
                        productId = productId ?: "",
                    )
                )
                if (response.success) response.expiryTimeMillis else null
            } catch (e: Exception) {
                Log.w(TAG, "Failed to query subscription from server, using estimate", e)
                null
            }

            val expiryMillis = serverExpiry ?: estimateExpiryTime(activePurchase.purchaseTime, productId)
            _subscriptionStatus.value = SubscriptionStatus(
                isActive = true,
                productId = productId,
                expiryTimeMillis = expiryMillis,
                autoRenewing = activePurchase.isAutoRenewing,
            )
        } else {
            _subscriptionStatus.value = SubscriptionStatus(
                isActive = false,
                productId = null,
                expiryTimeMillis = null,
                autoRenewing = false,
            )
        }

        val tier = if (isActive) User.TIER_PREMIUM else User.TIER_FREE
        userRepository.updateTier(tier)
    }

    private fun estimateExpiryTime(purchaseTimeMillis: Long, productId: String?): Long {
        val calendar = Calendar.getInstance().apply { timeInMillis = purchaseTimeMillis }
        when (productId) {
            "fisherlotto_monthly" -> calendar.add(Calendar.MONTH, 1)
            "fisherlotto_yearly" -> calendar.add(Calendar.YEAR, 1)
        }
        return calendar.timeInMillis
    }

    companion object {
        private const val TAG = "BillingRepositoryImpl"
        val PRODUCT_IDS = listOf("fisherlotto_monthly", "fisherlotto_yearly")
    }
}
