package com.queentech.domain.usecase.billing

import com.queentech.domain.model.billing.SubscriptionProduct
import com.queentech.domain.model.billing.SubscriptionStatus
import kotlinx.coroutines.flow.Flow

interface BillingRepository {
    val subscriptionStatus: Flow<SubscriptionStatus>

    suspend fun querySubscriptionProducts(): Result<List<SubscriptionProduct>>

    suspend fun launchSubscriptionFlow(activityContext: Any, productId: String): Result<Unit>

    suspend fun restorePurchases(): Result<SubscriptionStatus>

    suspend fun refreshSubscriptionStatus()
}
