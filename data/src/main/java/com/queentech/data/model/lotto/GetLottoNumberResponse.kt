package com.queentech.data.model.lotto

import com.google.gson.annotations.SerializedName
import com.queentech.domain.model.lotto.GetLottoNumber

data class GetLottoNumberResponse(
    @SerializedName("firstCount") val firstCount: String,
    @SerializedName("firstMoney") val firstMoney: String,
    @SerializedName("secondCount") val secondCount: String,
    @SerializedName("secondMoney") val secondMoney: String,
    @SerializedName("thirdCount") val thirdCount: String,
    @SerializedName("thirdMoney") val thirdMoney: String,
    @SerializedName("fourthCount") val fourthCount: String,
    @SerializedName("fourthMoney") val fourthMoney: String,
    @SerializedName("fifthCount") val fifthCount: String,
    @SerializedName("fifthMoney") val fifthMoney: String,

    @SerializedName("bonus") val bonus: String,
    @SerializedName("no1") val num1: String,
    @SerializedName("no2") val num2: String,
    @SerializedName("no3") val num3: String,
    @SerializedName("no4") val num4: String,
    @SerializedName("no5") val num5: String,
    @SerializedName("no6") val num6: String,

    @SerializedName("pickDate") val pdate: String,
    @SerializedName("lottoRound") val round: Int
)

fun GetLottoNumberResponse.toDomainModel(): GetLottoNumber = GetLottoNumber(
    firstCount = firstCount,
    firstMoney = firstMoney,
    secondCount = secondCount,
    secondMoney = secondMoney,
    thirdCount = thirdCount,
    thirdMoney = thirdMoney,
    fourthCount = fourthCount,
    fourthMoney = fourthMoney,
    fifthCount = fifthCount,
    fifthMoney = fifthMoney,
    bonus = bonus,
    num1 = num1,
    num2 = num2,
    num3 = num3,
    num4 = num4,
    num5 = num5,
    num6 = num6,
    pdate = pdate,
    round = round.toString(),
)