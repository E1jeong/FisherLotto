package com.queentech.domain.usecase.lotto

import com.queentech.domain.model.lotto.GetLottoStats

interface GetLottoStatsUseCase {

    suspend operator fun invoke(
        round: Int,
    ): Result<GetLottoStats>
}
