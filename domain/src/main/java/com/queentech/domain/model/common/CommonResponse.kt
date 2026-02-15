package com.queentech.domain.model.common

data class CommonResponse(
    val status: String
) {
    val statusInt get() = status.toInt()
}