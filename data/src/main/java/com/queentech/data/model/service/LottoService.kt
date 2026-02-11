package com.queentech.data.model.service

import com.queentech.data.model.lotto.GetLottoNumberResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface LottoService {
    companion object {
        const val RESOURCE_LOTTO = "lotto"
        const val GET_LOTTO_NUMBER = "1044"
        const val DRW_NO = "drwNo" // 로또 당첨 회차 번호
    }

    @GET("$RESOURCE_LOTTO/$GET_LOTTO_NUMBER")
    suspend fun getNumber(
        @Query(DRW_NO) drwNo: Int,
    ): GetLottoNumberResponse
}
