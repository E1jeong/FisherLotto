package com.queentech.data.model.billing

import com.google.gson.annotations.SerializedName

data class ReceiptRequest(
    @SerializedName("orderId") val orderId: String,
    @SerializedName("productId") val productId: String,
    @SerializedName("purchaseToken") val purchaseToken: String,
    @SerializedName("purchaseTime") val purchaseTime: Long,
    @SerializedName("autoRenewing") val autoRenewing: Boolean,
    @SerializedName("email") val email: String?,
)
