package com.queentech.domain.usecase.billing

import com.queentech.domain.model.billing.SubscriptionProduct
import com.queentech.domain.model.billing.SubscriptionStatus
import kotlinx.coroutines.flow.Flow

interface BillingRepository {
    val subscriptionStatus: Flow<SubscriptionStatus>

    // 구독 결제로 서버가 이번주 예상번호를 재발급했으나 앱이 아직 반영하지 못한 상태.
    // 반영한 쪽이 clearReissuePending()으로 해제한다.
    val reissuePending: Flow<Boolean>

    suspend fun clearReissuePending()

    suspend fun querySubscriptionProducts(): Result<List<SubscriptionProduct>>

    suspend fun launchSubscriptionFlow(activityContext: Any, productId: String): Result<Unit>

    suspend fun restorePurchases(): Result<SubscriptionStatus>

    suspend fun refreshSubscriptionStatus()
}
