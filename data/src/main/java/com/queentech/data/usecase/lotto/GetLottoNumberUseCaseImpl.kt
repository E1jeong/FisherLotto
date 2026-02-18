package com.queentech.data.usecase.lotto

import com.queentech.data.model.lotto.toDomainModel
import com.queentech.data.model.service.LottoService
import com.queentech.domain.model.lotto.GetLottoNumber
import com.queentech.domain.usecase.lotto.GetLottoNumberUseCase
import javax.inject.Inject

class GetLottoNumberUseCaseImpl @Inject constructor(
    private val lottoService: LottoService,
) : GetLottoNumberUseCase {

    override suspend fun invoke(round: Int): Result<GetLottoNumber> = kotlin.runCatching {
        val response = lottoService.getNumber(round = round)

        response.toDomainModel()
    }
}