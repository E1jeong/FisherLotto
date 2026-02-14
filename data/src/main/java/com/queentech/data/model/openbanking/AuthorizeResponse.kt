package com.queentech.data.model.openbanking

data class AuthorizeResponse(
    val authorizeUrl: String,
    val state: String,
)
