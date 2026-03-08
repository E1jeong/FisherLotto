package com.queentech.data.model.fcm

import com.google.gson.annotations.SerializedName

data class DeleteUserRequest(
    @SerializedName("email") val email: String,
)
