package com.queentech.data.model.login

data class RecoverUserRequestBody(
    val email: String,
    val phone: String,
    val verificationToken: String,
)
