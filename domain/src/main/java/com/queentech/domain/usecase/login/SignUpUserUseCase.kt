package com.queentech.domain.usecase.login

import com.queentech.domain.model.common.CommonResponse

interface SignUpUserUseCase {
    suspend operator fun invoke(
        name: String,
        email: String,
        birth: String,
        phone: String,
    ): Result<CommonResponse>
}