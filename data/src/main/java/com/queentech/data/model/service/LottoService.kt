package com.queentech.data.model.service

import com.queentech.data.model.login.GetUserRequestBody
import com.queentech.data.model.login.SignUpUserRequestBody
import com.queentech.data.model.common.CommonResponseDto
import com.queentech.data.model.lotto.GetExpectNumberResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LottoService {
    companion object {
        const val RESOURCE_LOTTO = "lotto"
        const val GET_EXPECT_NUMBER = "1000"
        const val REGISTER_USER = "1022"
    }

    @POST("$RESOURCE_LOTTO/$GET_EXPECT_NUMBER")
    suspend fun getExpectNumber(
        @Body request: GetUserRequestBody,
    ): GetExpectNumberResponse

    @POST("$RESOURCE_LOTTO/$REGISTER_USER")
    suspend fun registerUser(
        @Body request: SignUpUserRequestBody,
    ): CommonResponseDto
}
