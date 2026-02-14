package com.queentech.domain.usecase.openbanking

import com.queentech.domain.model.openbanking.TransferResult

interface TransferUseCase {
    suspend operator fun invoke(
        accessToken: String,
        fintechUseNum: String,
        tranAmt: Long,
        reqClientName: String,
        reqClientNum: String,
        recvClientName: String,
        recvClientAccountNum: String,
    ): TransferResult
}
