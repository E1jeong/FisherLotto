package com.queentech.data.usecase.lotto

import com.queentech.data.model.login.GetUserRequestBody
import com.queentech.data.model.service.SubLottoService
import com.queentech.domain.model.lotto.GetExpectNumber
import com.queentech.domain.usecase.lotto.GetExpectNumberUseCase
import javax.inject.Inject

class GetExpectNumberUseCaseImpl @Inject constructor(
    private val subLottoService: SubLottoService,
) : GetExpectNumberUseCase {
    override suspend fun invoke(
        email: String,
        phone: String,
    ): Result<GetExpectNumber> = kotlin.runCatching {

        val requestBody = GetUserRequestBody(
            email = email,
            phone = phone
        )

        subLottoService.getExpectNumber(requestBody)
    }
}