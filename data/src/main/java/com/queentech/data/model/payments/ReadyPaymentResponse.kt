package com.queentech.data.model.payments

data class ReadyPaymentResponse(
    val orderId: String,
    val amount: Long,
    val payUrl: String,
)