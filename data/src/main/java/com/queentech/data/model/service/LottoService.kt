package com.queentech.data.model.service

import com.queentech.data.model.login.SignUpUserRequestBody
import com.queentech.data.model.lotto.GetLottoNumberResponse
import com.queentech.domain.model.common.CommonResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LottoService {
    companion object {
        const val RESOURCE_LOTTO = "lotto"

        const val SIGNUP_USER = "1022"
        const val GET_LOTTO_NUMBER = "1044"
        const val DRW_NO = "drwNo" // 로또 당첨 회차 번호
    }

    @POST("$RESOURCE_LOTTO/$SIGNUP_USER")
    suspend fun signUpUser(
        @Body request: SignUpUserRequestBody,
    ): CommonResponse

    @GET("$RESOURCE_LOTTO/$GET_LOTTO_NUMBER")
    suspend fun getNumber(
        @Query(DRW_NO) drwNo: Int,
    ): GetLottoNumberResponse
}
