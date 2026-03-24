package com.queentech.domain.model.lotto

data class ScanHistory(
    val id: Long,
    val drawNo: Int,
    val games: List<List<Int>>,
    val bestRank: Int,   // 1~5 = 등수, 0 = 낙첨
    val scannedAt: Long,
)
