package com.queentech.data.model.openbanking

data class TokenRequest(
    val code: String? = null,
    val refreshToken: String? = null,
)
