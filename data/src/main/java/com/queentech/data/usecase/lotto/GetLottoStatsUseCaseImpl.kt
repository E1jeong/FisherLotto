package com.queentech.data.usecase.lotto

import com.queentech.data.model.lotto.toDomainModel
import com.queentech.data.model.service.SubLottoService
import com.queentech.domain.model.lotto.GetLottoStats
import com.queentech.domain.usecase.lotto.GetLottoStatsUseCase
import javax.inject.Inject

class GetLottoStatsUseCaseImpl @Inject constructor(
    private val subLottoService: SubLottoService,
) : GetLottoStatsUseCase {

    override suspend fun invoke(round: Int): Result<GetLottoStats> = kotlin.runCatching {
        val response = subLottoService.getStats(round = round)

        response.toDomainModel()
    }
}
