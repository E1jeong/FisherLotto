package com.queentech.data.model.service

import com.queentech.data.model.login.GetUserRequestBody
import com.queentech.data.model.login.SignUpUserRequestBody
import com.queentech.domain.model.common.CommonResponse
import com.queentech.domain.model.lotto.GetExpectNumber
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
    ): GetExpectNumber

    @POST("$RESOURCE_LOTTO/$REGISTER_USER")
    suspend fun registerUser(
        @Body request: SignUpUserRequestBody,
    ): CommonResponse
}
