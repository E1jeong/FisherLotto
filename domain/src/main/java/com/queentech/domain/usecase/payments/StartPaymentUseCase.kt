package com.queentech.domain.usecase.payments

import com.queentech.domain.model.payments.ReadyPaymentResult

interface StartPaymentUseCase {
    suspend operator fun invoke(
        amount: Long,
        goodName: String,
        orderName: String,
    ): ReadyPaymentResult
}