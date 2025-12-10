package com.queentech.data.model.payments

data class ReadyPaymentRequest(
    val orderId: String,
    val amount: Long,
)