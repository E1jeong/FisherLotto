package com.queentech.data.usecase.lotto

import com.queentech.data.model.lotto.toDomainModel
import com.queentech.data.model.service.SubLottoService
import com.queentech.domain.model.lotto.GetLottoNumber
import com.queentech.domain.usecase.lotto.GetLottoNumberUseCase
import javax.inject.Inject

class GetLottoNumberUseCaseImpl @Inject constructor(
    private val subLottoService: SubLottoService,
) : GetLottoNumberUseCase {

    override suspend fun invoke(round: Int): Result<GetLottoNumber> = kotlin.runCatching {
        val response = subLottoService.getNumber(round = round)

        response.toDomainModel()
    }
}