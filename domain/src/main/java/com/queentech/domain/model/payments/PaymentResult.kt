package com.queentech.domain.model.payments

data class PaymentResult(
    val orderId: String,
    val status: PaymentStatus,
    val amount: Long,
    val approvalNo: String?,
    val approvedAt: String?,
    val message: String?,
)
