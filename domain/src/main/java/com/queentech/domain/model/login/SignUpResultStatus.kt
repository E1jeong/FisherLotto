package com.queentech.domain.model.login

enum class SignUpResultStatus(
    val status: Int
) {
    OK(8200),
    NOT_FOUND(8404),
    DUPLICATED_EMAIL(8611),
    DUPLICATED_PHONE_NUMBER(8633),
    ERROR_REGISTER(8655),
    ERROR_REQUEST(8677),
    ERROR(8699),
}