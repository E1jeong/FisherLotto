package com.queentech.domain.usecase.openbanking

import com.queentech.domain.model.openbanking.AccountBalance

interface GetBalanceUseCase {
    suspend operator fun invoke(
        accessToken: String,
        fintechUseNum: String,
    ): AccountBalance
}
