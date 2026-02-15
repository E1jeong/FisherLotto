package com.queentech.data.model.login

data class SignUpUserRequestBody(
    val name: String,
    val email: String,
    val birth: String,
    val phone: String,
)
