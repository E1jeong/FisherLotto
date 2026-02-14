package com.queentech.domain.usecase.openbanking

import com.queentech.domain.model.openbanking.AuthorizeResult

interface GetAuthorizeUrlUseCase {
    suspend operator fun invoke(): AuthorizeResult
}
