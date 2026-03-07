package com.queentech.domain.model.billing

data class SubscriptionProduct(
    val productId: String,
    val name: String,
    val description: String,
    val formattedPrice: String,
    val billingPeriod: String,
)
