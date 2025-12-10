package com.queentech.data.model.payments

data class PaymentResultResponse(
    val orderId: String,
    val status: String,       // "SUCCESS" | "FAILED" | "PENDING" (지금은 mock SUCCESS)
    val amount: Long,
    val approvalNo: String?,
    val approvedAt: String?,
    val message: String?,
)