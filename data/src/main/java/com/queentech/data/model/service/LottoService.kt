package com.queentech.data.model.service

import com.queentech.data.model.login.GetUserRequestBody
import com.queentech.data.model.login.SignUpUserRequestBody
import com.queentech.data.model.lotto.GetLottoNumberResponse
import com.queentech.domain.model.common.CommonResponse
import com.queentech.domain.model.lotto.GetExpectNumber
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LottoService {
    companion object {
        const val RESOURCE_LOTTO = "lotto"

        const val GET_EXPECT_NUMBER = "1000"
        const val SIGNUP_USER = "1022"
        const val GET_USER = "1033"
        const val GET_LOTTO_NUMBER = "1044"
        const val ROUND = "round" // 로또 당첨 회차
    }

    @POST("$RESOURCE_LOTTO/$GET_EXPECT_NUMBER")
    suspend fun getExpectNumber(
        @Body request: GetUserRequestBody,
    ): GetExpectNumber

    @POST("$RESOURCE_LOTTO/$SIGNUP_USER")
    suspend fun signUpUser(
        @Body request: SignUpUserRequestBody,
    ): CommonResponse

    @POST("$RESOURCE_LOTTO/$GET_USER")
    suspend fun getUser(
        @Body request: GetUserRequestBody,
    ): CommonResponse

    @GET("$RESOURCE_LOTTO/$GET_LOTTO_NUMBER")
    suspend fun getNumber(
        @Query(ROUND) round: Int,
    ): GetLottoNumberResponse
}
