package com.queentech.data.usecase.openbanking

import com.queentech.data.model.openbanking.TokenRequest
import com.queentech.data.model.service.OpenBankingService
import com.queentech.domain.model.openbanking.TokenResult
import com.queentech.domain.usecase.openbanking.GetTokenUseCase
import javax.inject.Inject

class GetTokenUseCaseImpl @Inject constructor(
    private val openBankingService: OpenBankingService,
) : GetTokenUseCase {

    override suspend fun invoke(
        code: String?,
        refreshToken: String?,
    ): TokenResult {
        val res = openBankingService.getToken(
            TokenRequest(
                code = code,
                refreshToken = refreshToken,
            )
        )
        return TokenResult(
            accessToken = res.accessToken,
            tokenType = res.tokenType,
            expiresIn = res.expiresIn,
            refreshToken = res.refreshToken,
            scope = res.scope,
            userSeqNo = res.userSeqNo,
        )
    }
}
