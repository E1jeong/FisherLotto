package com.queentech.data.model.billing

import com.google.gson.annotations.SerializedName

data class SubscriptionQueryRequest(
    @SerializedName("purchaseToken") val purchaseToken: String,
    @SerializedName("productId") val productId: String,
)
