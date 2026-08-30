package com.queentech.domain.model.login

class SignUpException(
    val status: SignUpResultStatus,
    message: String,
) : Exception(message)
