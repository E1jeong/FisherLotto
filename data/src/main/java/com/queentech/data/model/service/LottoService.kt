package com.queentech.data.model.service

import com.queentech.data.model.response.GetLottoNumberResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface LottoService {
    companion object {
        const val DHLOTTERY_SERVER_COMMON = "common.do?"
        const val METHOD = "method"
        const val DRW_NO = "drwNo" // 로또 당첨 회차 번호

        const val GET_LOTTO_NUMBER = "getLottoNumber"
    }

    @GET(DHLOTTERY_SERVER_COMMON)
    suspend fun getNumber(
        @Query(METHOD) method: String = GET_LOTTO_NUMBER,
        @Query(DRW_NO) drwNo: Int,
    ): GetLottoNumberResponse
}
