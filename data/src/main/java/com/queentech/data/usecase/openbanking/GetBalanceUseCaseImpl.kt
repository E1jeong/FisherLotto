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
            bankName = res.bankName ?: "알 수 없음",
            accountNumMasked = res.accountNumMasked ?: "계좌번호 없음",
            balanceAmt = res.balanceAmt?.toLongOrNull() ?: 0L,
            availableAmt = res.availableAmt?.toLongOrNull() ?: 0L,
            productName = res.productName ?: "상품명 없음",
        )
    }
}
