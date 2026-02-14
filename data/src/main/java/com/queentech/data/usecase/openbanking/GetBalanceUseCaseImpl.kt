package com.queentech.data.usecase.openbanking

import com.queentech.data.model.service.OpenBankingService
import com.queentech.domain.model.openbanking.AccountBalance
import com.queentech.domain.usecase.openbanking.GetBalanceUseCase
import javax.inject.Inject

class GetBalanceUseCaseImpl @Inject constructor(
    private val openBankingService: OpenBankingService,
) : GetBalanceUseCase {

    override suspend fun invoke(
        accessToken: String,
        fintechUseNum: String,
    ): AccountBalance {
        val res = openBankingService.getBalance(
            authorization = "Bearer $accessToken",
            fintechUseNum = fintechUseNum,
        )
        return AccountBalance(
            bankName = res.bankName,
            accountNumMasked = res.accountNumMasked,
            balanceAmt = res.balanceAmt.toLongOrNull() ?: 0L,
            availableAmt = res.availableAmt.toLongOrNull() ?: 0L,
            productName = res.productName,
        )
    }
}
