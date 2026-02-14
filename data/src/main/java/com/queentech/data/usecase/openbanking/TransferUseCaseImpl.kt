package com.queentech.data.usecase.openbanking

import com.queentech.data.model.openbanking.TransferRequest
import com.queentech.data.model.service.OpenBankingService
import com.queentech.domain.model.openbanking.TransferResult
import com.queentech.domain.usecase.openbanking.TransferUseCase
import javax.inject.Inject

class TransferUseCaseImpl @Inject constructor(
    private val openBankingService: OpenBankingService,
) : TransferUseCase {

    override suspend fun invoke(
        accessToken: String,
        fintechUseNum: String,
        tranAmt: Long,
        reqClientName: String,
        reqClientNum: String,
        recvClientName: String,
        recvClientAccountNum: String,
    ): TransferResult {
        val res = openBankingService.transfer(
            authorization = "Bearer $accessToken",
            request = TransferRequest(
                fintechUseNum = fintechUseNum,
                tranAmt = tranAmt.toString(),
                reqClientName = reqClientName,
                reqClientNum = reqClientNum,
                recvClientName = recvClientName,
                recvClientAccountNum = recvClientAccountNum,
            ),
        )
        return TransferResult(
            apiTranId = res.apiTranId,
            rspCode = res.rspCode,
            rspMessage = res.rspMessage,
            dpsBankName = res.dpsBankName,
            dpsAccountNumMasked = res.dpsAccountNumMasked,
            tranAmt = res.tranAmt.toLongOrNull() ?: 0L,
            recvClientName = res.recvClientName,
        )
    }
}
