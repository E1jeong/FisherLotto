package com.queentech.data.model.service

import com.queentech.data.model.lotto.GetLottoNumberResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SubLottoService {
    @GET("api/lotto/winning")
    suspend fun getNumber(
        @Query("round") round: Int,
    ): GetLottoNumberResponse
}
