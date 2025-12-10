package com.queentech.data.usecase.payments

import com.queentech.data.model.service.PaymentsService
import com.queentech.domain.model.payments.PaymentResult
import com.queentech.domain.model.payments.PaymentStatus
import com.queentech.domain.usecase.payments.GetPaymentResultUseCase
import javax.inject.Inject

class GetPaymentResultUseCaseImpl @Inject constructor(
    private val paymentsService: PaymentsService,
) : GetPaymentResultUseCase {

    override suspend fun invoke(orderId: String): PaymentResult {
        val res = paymentsService.getPaymentResult(orderId)

        val status = when (res.status.uppercase()) {
            "SUCCESS" -> PaymentStatus.SUCCESS
            "FAILED"  -> PaymentStatus.FAILED
            else      -> PaymentStatus.PENDING
        }

        return PaymentResult(
            orderId = res.orderId,
            status = status,
            amount = res.amount,
            approvalNo = res.approvalNo,
            approvedAt = res.approvedAt,
            message = res.message,
        )
    }
}