package com.queentech.domain.usecase.payments

import com.queentech.domain.model.payments.PaymentResult

interface GetPaymentResultUseCase {
    suspend operator fun invoke(orderId: String): PaymentResult
}