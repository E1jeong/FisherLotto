package com.queentech.domain.model.payments

data class ReadyPaymentResult(
    val orderId: String,
    val amount: Long,
    val payUrl: String,
)
