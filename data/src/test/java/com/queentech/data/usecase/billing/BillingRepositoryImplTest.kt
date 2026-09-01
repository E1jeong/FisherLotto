package com.queentech.data.usecase.billing

import com.queentech.domain.model.billing.SubscriptionStatus
import com.queentech.domain.model.billing.SubscriptionVerificationState
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BillingRepositoryImplTest {

    @Test
    fun `검증된 프리미엄이 아직 만료되지 않았으면 빈 구매 목록으로 유지한다`() {
        val status = SubscriptionStatus(
            isActive = true,
            productId = "fisherlotto_monthly",
            expiryTimeMillis = 2_000L,
            autoRenewing = true,
            verificationState = SubscriptionVerificationState.VERIFIED,
        )

        assertTrue(BillingRepositoryImpl.shouldPreserveValidEntitlement(status, nowMillis = 1_000L))
    }

    @Test
    fun `만료됐거나 검증되지 않은 구독은 유지하지 않는다`() {
        val expired = SubscriptionStatus(
            isActive = true,
            productId = "fisherlotto_monthly",
            expiryTimeMillis = 1_000L,
            autoRenewing = false,
            verificationState = SubscriptionVerificationState.VERIFIED,
        )
        val unverified = expired.copy(
            expiryTimeMillis = 2_000L,
            verificationState = SubscriptionVerificationState.UNKNOWN,
        )

        assertFalse(BillingRepositoryImpl.shouldPreserveValidEntitlement(expired, nowMillis = 1_000L))
        assertFalse(BillingRepositoryImpl.shouldPreserveValidEntitlement(unverified, nowMillis = 1_000L))
    }
}
