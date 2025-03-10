package com.queentech.data.usecase

import com.queentech.data.model.response.toDomainModel
import com.queentech.data.model.service.LottoService
import com.queentech.domain.model.GetLottoNumber
import com.queentech.domain.usecase.GetLottoNumberUseCase
import javax.inject.Inject

class GetLottoNumberUseCaseImpl @Inject constructor(
    private val lottoService: LottoService,
) : GetLottoNumberUseCase {

    override suspend fun invoke(drwNo: Int): Result<GetLottoNumber> = kotlin.runCatching {
        val response = lottoService.getNumber(drwNo = drwNo)

        response.toDomainModel()
    }
}