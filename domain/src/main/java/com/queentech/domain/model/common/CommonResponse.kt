package com.queentech.domain.model.common

data class CommonResponse(
    val status: String,
    val verificationToken: String? = null,
) {
    val statusInt get() = status.toIntOrNull() ?: -1
}
