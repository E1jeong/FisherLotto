package com.queentech.domain.usecase

import com.queentech.domain.model.GetLottoNumber

interface GetLottoNumberUseCase {

    suspend operator fun invoke(
        drwNo: Int,
    ): Result<GetLottoNumber>
}