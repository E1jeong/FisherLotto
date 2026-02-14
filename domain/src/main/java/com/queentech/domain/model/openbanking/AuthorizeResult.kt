package com.queentech.domain.model.openbanking

data class AuthorizeResult(
    val authorizeUrl: String,
    val state: String,
)
