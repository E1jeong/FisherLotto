package com.queentech.data.model.service

import com.queentech.data.model.login.GetUserRequestBody
import com.queentech.data.model.login.SignUpUserRequestBody
import com.queentech.data.model.common.CommonResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("api/users/register")
    suspend fun signUpUser(
        @Body request: SignUpUserRequestBody,
    ): CommonResponseDto

    @POST("api/users/login")
    suspend fun getUser(
        @Body request: GetUserRequestBody,
    ): CommonResponseDto

    @POST("api/users/withdraw")
    suspend fun withdraw(
        @Body request: GetUserRequestBody,
    ): CommonResponseDto
}
