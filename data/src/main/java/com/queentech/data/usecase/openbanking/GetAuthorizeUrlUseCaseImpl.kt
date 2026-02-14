package com.queentech.data.usecase.openbanking

import com.queentech.data.model.service.OpenBankingService
import com.queentech.domain.model.openbanking.AuthorizeResult
import com.queentech.domain.usecase.openbanking.GetAuthorizeUrlUseCase
import javax.inject.Inject

class GetAuthorizeUrlUseCaseImpl @Inject constructor(
    private val openBankingService: OpenBankingService,
) : GetAuthorizeUrlUseCase {

    override suspend fun invoke(): AuthorizeResult {
        val res = openBankingService.getAuthorizeUrl()
        return AuthorizeResult(
            authorizeUrl = res.authorizeUrl,
            state = res.state,
        )
    }
}
