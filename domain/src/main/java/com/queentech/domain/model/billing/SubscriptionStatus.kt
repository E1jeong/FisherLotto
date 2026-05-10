package com.queentech.domain.model.billing

data class SubscriptionStatus(
    val isActive: Boolean,
    val productId: String?,
    val expiryTimeMillis: Long?,
    val autoRenewing: Boolean,
    val cancelAtPeriodEnd: Boolean = false,
    val isOnHold: Boolean = false,
)
