package com.queentech.data.model.payments

data class CreateOrderRequest(
    val amount: Long,
    val goodName: String,
    val orderName: String,
)
