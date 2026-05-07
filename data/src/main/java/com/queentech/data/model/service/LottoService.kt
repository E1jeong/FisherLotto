package com.queentech.data.model.service

import com.queentech.data.model.login.GetUserRequestBody
import com.queentech.domain.model.lotto.GetExpectNumber
import retrofit2.http.Body
import retrofit2.http.POST

interface LottoService {
    companion object {
        const val RESOURCE_LOTTO = "lotto"
        const val GET_EXPECT_NUMBER = "1000"
    }

    @POST("$RESOURCE_LOTTO/$GET_EXPECT_NUMBER")
    suspend fun getExpectNumber(
        @Body request: GetUserRequestBody,
    ): GetExpectNumber
}
