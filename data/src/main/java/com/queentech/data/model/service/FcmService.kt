package com.queentech.data.model.service

import com.queentech.data.model.fcm.DeleteUserRequest
import com.queentech.data.model.fcm.FcmTokenRequest
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.POST

interface FcmService {
    @POST("api/fcm/token")
    suspend fun registerToken(
        @Body request: FcmTokenRequest,
    )

    @HTTP(method = "DELETE", path = "api/fcm/user", hasBody = true)
    suspend fun deleteUser(
        @Body request: DeleteUserRequest,
    )
}
