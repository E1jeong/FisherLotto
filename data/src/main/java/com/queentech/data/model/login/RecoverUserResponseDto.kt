package com.queentech.data.model.login

data class RecoverUserResponseDto(
    val status: String,
    val name: String? = null,
    val email: String? = null,
    val birth: String? = null,
    val phone: String? = null,
    val tier: String? = null,
)
