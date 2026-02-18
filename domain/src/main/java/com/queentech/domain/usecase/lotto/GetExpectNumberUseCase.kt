package com.queentech.domain.usecase.lotto

import com.queentech.domain.model.lotto.GetExpectNumber

interface GetExpectNumberUseCase {
    suspend operator fun invoke(
        email: String,
        phone: String,
    ): Result<GetExpectNumber>
}