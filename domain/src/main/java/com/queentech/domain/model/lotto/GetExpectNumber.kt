package com.queentech.domain.model.lotto

data class GetExpectNumber(
    val count: Int,
    val lotto: List<String>
) {
    fun String.toLottoNumbers(): List<Int> = split(",").map { it.trim().toInt() }
}