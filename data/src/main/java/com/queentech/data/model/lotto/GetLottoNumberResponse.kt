package com.queentech.data.model.lotto

import com.google.gson.annotations.SerializedName
import com.queentech.domain.model.lotto.GetLottoNumber

data class GetLottoNumberResponse(
    @SerializedName("1_count") val firstCount: String,
    @SerializedName("1_money") val firstMoney: String,
    @SerializedName("2_count") val secondCount: String,
    @SerializedName("2_money") val secondMoney: String,
    @SerializedName("3_count") val thirdCount: String,
    @SerializedName("3_money") val thirdMoney: String,
    @SerializedName("4_count") val fourthCount: String,
    @SerializedName("4_money") val fourthMoney: String,
    @SerializedName("5_count") val fifthCount: String,
    @SerializedName("5_money") val fifthMoney: String,

    @SerializedName("bonus") val bonus: String,
    @SerializedName("num1") val num1: String,
    @SerializedName("num2") val num2: String,
    @SerializedName("num3") val num3: String,
    @SerializedName("num4") val num4: String,
    @SerializedName("num5") val num5: String,
    @SerializedName("num6") val num6: String,

    @SerializedName("pdate") val pdate: String,
    @SerializedName("round") val round: String
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
    round = round,
)