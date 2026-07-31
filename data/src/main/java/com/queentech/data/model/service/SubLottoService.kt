package com.queentech.data.model.service

import com.queentech.data.model.login.GetUserRequestBody
import com.queentech.data.model.lotto.GetLottoNumberResponse
import com.queentech.data.model.lotto.GetExpectNumberResponse
import com.queentech.data.model.lotto.GetLottoStatsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SubLottoService {
    @GET("api/lotto/winning")
    suspend fun getNumber(
        @Query("round") round: Int,
    ): GetLottoNumberResponse

    @GET("api/lotto/stats")
    suspend fun getStats(
        @Query("round") round: Int,
    ): GetLottoStatsResponse

    @POST("api/lotto/expect")
    suspend fun getExpectNumber(
        @Body request: GetUserRequestBody,
    ): GetExpectNumberResponse
}
