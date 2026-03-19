package com.queentech.domain.model.lotto

data class ScanHistory(
    val id: Long,
    val drawNo: Int,
    val games: List<List<Int>>,
    val matchCount: Int,
    val scannedAt: Long,
)
