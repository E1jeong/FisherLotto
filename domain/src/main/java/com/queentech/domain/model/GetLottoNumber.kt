package com.queentech.domain.model

data class GetLottoNumber(
    val eachWinnings: Long, // 1게임당 1등 당첨금
    val winnerCount: Int, // 1등 당첨 수
    val totalWinnings: Long, // 1등 당첨금 총액
    val drwNo: Int,
    val drwNoDate: String,
    val drwtNo1: Int,
    val drwtNo2: Int,
    val drwtNo3: Int,
    val drwtNo4: Int,
    val drwtNo5: Int,
    val drwtNo6: Int,
    val bnusNo: Int,
)