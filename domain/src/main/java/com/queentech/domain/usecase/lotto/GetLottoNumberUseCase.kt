package com.queentech.domain.usecase.lotto

import com.queentech.domain.model.lotto.GetLottoNumber

interface GetLottoNumberUseCase {

    suspend operator fun invoke(
        drwNo: Int,
    ): Result<GetLottoNumber>
}