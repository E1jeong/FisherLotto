package com.queentech.data.usecase.login

import com.queentech.data.model.login.GetUserRequestBody
import com.queentech.data.model.service.LottoService
import com.queentech.domain.model.common.CommonResponse
import com.queentech.domain.usecase.login.GetUserUseCase
import javax.inject.Inject

class GetUserUseCaseImpl @Inject constructor(
    private val lottoService: LottoService
) : GetUserUseCase {
    override suspend fun invoke(
        email: String,
        phone: String
    ): Result<CommonResponse> = kotlin.runCatching {

        val requestBody = GetUserRequestBody(
            email = email,
            phone = phone
        )

        lottoService.getUser(requestBody)
    }
}