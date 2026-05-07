package com.queentech.data.model.service

import com.queentech.data.model.login.GetUserRequestBody
import com.queentech.data.model.login.SignUpUserRequestBody
import com.queentech.data.model.login.TierRequest
import com.queentech.domain.model.common.CommonResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("api/users/register")
    suspend fun signUpUser(
        @Body request: SignUpUserRequestBody,
    ): CommonResponse

    @POST("api/users/login")
    suspend fun getUser(
        @Body request: GetUserRequestBody,
    ): CommonResponse

    @POST("api/users/tier")
    suspend fun updateTier(
        @Body request: TierRequest,
    ): CommonResponse

    @POST("api/users/withdraw")
    suspend fun withdraw(
        @Body request: GetUserRequestBody,
    ): CommonResponse
}
