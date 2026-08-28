package com.queentech.data.model.billing

import com.google.gson.annotations.SerializedName

data class SubscriptionQueryResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("expiryTimeMillis") val expiryTimeMillis: Long?,
    @SerializedName("autoRenewing") val autoRenewing: Boolean?,
    @SerializedName("cancelAtPeriodEnd") val cancelAtPeriodEnd: Boolean?,
    @SerializedName("isOnHold") val isOnHold: Boolean?,
    @SerializedName("message") val message: String?,
    @SerializedName("isEntitled") val isEntitled: Boolean? = null,
    @SerializedName("productId") val productId: String? = null,
    @SerializedName("subscriptionState") val subscriptionState: String? = null,
)
