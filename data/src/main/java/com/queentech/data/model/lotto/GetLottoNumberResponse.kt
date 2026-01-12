package com.queentech.data.model.lotto

import com.queentech.domain.model.lotto.GetLottoNumber

data class GetLottoNumberResponse(
    val totSellamnt: Long, // 해당 회차 복권 판매 금액
    val returnValue: String,
    val firstWinamnt: Long, // 1게임당 1등 당첨금
    val firstPrzwnerCo: Int, // 1등 당첨 수
    val firstAccumamnt: Long, // 1등 당첨금 총액
    val drwNo: Int, // 해당 회차 번호
    val drwNoDate: String,
    val drwtNo1: Int,
    val drwtNo2: Int,
    val drwtNo3: Int,
    val drwtNo4: Int,
    val drwtNo5: Int,
    val drwtNo6: Int,
    val bnusNo: Int,
)

fun GetLottoNumberResponse.toDomainModel(): GetLottoNumber = GetLottoNumber(
    eachWinnings = firstWinamnt,
    winnerCount = firstPrzwnerCo,
    totalWinnings = firstAccumamnt,
    drwNo = drwNo,
    drwNoDate = drwNoDate,
    drwtNo1 = drwtNo1,
    drwtNo2 = drwtNo2,
    drwtNo3 = drwtNo3,
    drwtNo4 = drwtNo4,
    drwtNo5 = drwtNo5,
    drwtNo6 = drwtNo6,
    bnusNo = bnusNo,
)