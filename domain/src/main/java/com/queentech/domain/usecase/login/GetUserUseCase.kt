package com.queentech.domain.usecase.login

import com.queentech.domain.model.common.CommonResponse

interface GetUserUseCase {
    suspend operator fun invoke(
        email: String,
        phone: String,
    ): Result<CommonResponse>
}