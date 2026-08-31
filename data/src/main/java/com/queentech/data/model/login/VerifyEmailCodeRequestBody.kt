package com.queentech.data.model.login

data class VerifyEmailCodeRequestBody(
    val email: String,
    val code: String,
    val purpose: String,
)
