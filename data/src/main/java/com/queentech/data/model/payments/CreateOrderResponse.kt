package com.queentech.data.model.payments

data class CreateOrderResponse(
    val orderId: String,
    val amount: Long,
    val goodName: String,
    val orderName: String,
)