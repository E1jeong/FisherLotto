package com.queentech.data.model.fcm

import com.google.gson.annotations.SerializedName

data class FcmTokenRequest(
    @SerializedName("email") val email: String,
    @SerializedName("fcmToken") val fcmToken: String,
)
