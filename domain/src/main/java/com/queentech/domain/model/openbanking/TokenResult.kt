package com.queentech.domain.model.openbanking

data class TokenResult(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Long,
    val refreshToken: String,
    val scope: String,
    val userSeqNo: String,
)
