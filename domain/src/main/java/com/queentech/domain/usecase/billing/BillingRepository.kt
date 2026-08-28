package com.queentech.domain.usecase.billing

import com.queentech.domain.model.billing.SubscriptionProduct
import com.queentech.domain.model.billing.SubscriptionStatus
import kotlinx.coroutines.flow.Flow

interface BillingRepository {
    val subscriptionStatus: Flow<SubscriptionStatus>

    // 신규 구독 결제로 현재 주차의 로컬 추천 번호를 비운 뒤, 열린 화면을 갱신한다.
    val expectedNumberResetEvents: Flow<Unit>

    suspend fun querySubscriptionProducts(): Result<List<SubscriptionProduct>>

    suspend fun launchSubscriptionFlow(activityContext: Any, productId: String): Result<Unit>

    suspend fun refreshSubscriptionStatus(): Result<SubscriptionStatus>
}
