package com.queentech.domain.usecase.openbanking

import com.queentech.domain.model.openbanking.Account

interface GetAccountsUseCase {
    suspend operator fun invoke(
        accessToken: String,
        userSeqNo: String,
    ): List<Account>
}
