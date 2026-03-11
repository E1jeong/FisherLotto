package com.queentech.data.model.billing

import com.google.gson.annotations.SerializedName

data class ReceiptResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
)
