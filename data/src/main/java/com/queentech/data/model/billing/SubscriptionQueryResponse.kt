package com.queentech.data.model.billing

import com.google.gson.annotations.SerializedName

data class SubscriptionQueryResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("expiryTimeMillis") val expiryTimeMillis: Long?,
    @SerializedName("autoRenewing") val autoRenewing: Boolean?,
    @SerializedName("cancelAtPeriodEnd") val cancelAtPeriodEnd: Boolean?,
    @SerializedName("isOnHold") val isOnHold: Boolean?,
    @SerializedName("message") val message: String?,
)
