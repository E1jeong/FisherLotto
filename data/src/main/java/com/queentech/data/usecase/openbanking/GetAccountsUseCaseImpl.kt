package com.queentech.data.usecase.openbanking

import com.queentech.data.model.service.OpenBankingService
import com.queentech.domain.model.openbanking.Account
import com.queentech.domain.usecase.openbanking.GetAccountsUseCase
import javax.inject.Inject

class GetAccountsUseCaseImpl @Inject constructor(
    private val openBankingService: OpenBankingService,
) : GetAccountsUseCase {

    override suspend fun invoke(
        accessToken: String,
        userSeqNo: String,
    ): List<Account> {
        val res = openBankingService.getAccounts(
            authorization = "Bearer $accessToken",
            userSeqNo = userSeqNo,
        )
        return res.accountList.map { item ->
            Account(
                fintechUseNum = item.fintechUseNum,
                accountAlias = item.accountAlias,
                bankName = item.bankName,
                accountNumMasked = item.accountNumMasked,
                accountHolderName = item.accountHolderName,
                transferAgreeYn = item.transferAgreeYn,
            )
        }
    }
}
