package com.queentech.domain.model.lotto

data class GetLottoStats(
    val round: String,
    val pdate: String,
    val grade1: Int,
    val grade2: Int,
    val grade3: Int,
    val grade4: Int,
    val grade5: Int,
    val combiCount: Int,
)
