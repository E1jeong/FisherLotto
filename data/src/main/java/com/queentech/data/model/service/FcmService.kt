package com.queentech.data.model.service

import com.queentech.data.model.fcm.FcmTokenRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface FcmService {
    @POST("api/fcm/token")
    suspend fun registerToken(
        @Body request: FcmTokenRequest,
    )
}
