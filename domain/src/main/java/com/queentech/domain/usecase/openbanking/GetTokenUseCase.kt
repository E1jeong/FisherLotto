package com.queentech.domain.usecase.openbanking

import com.queentech.domain.model.openbanking.TokenResult

interface GetTokenUseCase {
    suspend operator fun invoke(
        code: String? = null,
        refreshToken: String? = null,
    ): TokenResult
}
