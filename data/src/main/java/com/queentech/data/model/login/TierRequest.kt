package com.queentech.data.model.login

data class TierRequest(
    val email: String,
    val phone: String,
    val isPremium: Boolean,
)
