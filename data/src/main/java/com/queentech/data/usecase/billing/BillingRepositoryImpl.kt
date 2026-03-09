package com.queentech.data.usecase.billing

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.queentech.domain.model.billing.SubscriptionProduct
import com.queentech.domain.model.billing.SubscriptionStatus
import com.queentech.domain.usecase.billing.BillingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingRepositoryImpl @Inject constructor(
    private val billingClientWrapper: BillingClientWrapper,
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
                if (!acknowledged) {
                    Log.e(TAG, "Failed to acknowledge purchase: ${purchase.orderId}")
                }
            }
        }
        refreshSubscriptionStatus()
    }

    override suspend fun refreshSubscriptionStatus() {
        val purchases = billingClientWrapper.queryPurchases()
        val activePurchase = purchases.firstOrNull { it.purchaseState == Purchase.PurchaseState.PURCHASED }

        if (activePurchase != null) {
            _subscriptionStatus.value = SubscriptionStatus(
                isActive = true,
                productId = activePurchase.products.firstOrNull(),
                expiryTimeMillis = null,
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
    }

    companion object {
        private const val TAG = "BillingRepositoryImpl"
        val PRODUCT_IDS = listOf("fisherlotto_monthly", "fisherlotto_yearly")
    }
}
