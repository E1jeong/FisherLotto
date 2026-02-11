package com.queentech.domain.model.lotto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetLottoNumber(
    @SerialName("1_count") val firstCount: String,
    @SerialName("1_money") val firstMoney: String,
    @SerialName("2_count") val secondCount: String,
    @SerialName("2_money") val secondMoney: String,
    @SerialName("3_count") val thirdCount: String,
    @SerialName("3_money") val thirdMoney: String,
    @SerialName("4_count") val fourthCount: String,
    @SerialName("4_money") val fourthMoney: String,
    @SerialName("5_count") val fifthCount: String,
    @SerialName("5_money") val fifthMoney: String,

    @SerialName("bonus") val bonus: String,
    @SerialName("num1") val num1: String,
    @SerialName("num2") val num2: String,
    @SerialName("num3") val num3: String,
    @SerialName("num4") val num4: String,
    @SerialName("num5") val num5: String,
    @SerialName("num6") val num6: String,

    @SerialName("pdate") val pdate: String,   // "2026-02-07"
    @SerialName("round") val round: String    // "1210"
) {
    val firstCountInt get() = firstCount.toInt()
    val firstMoneyLong get() = firstMoney.toLong()
    val secondCountInt get() = secondCount.toInt()
    val secondMoneyLong get() = secondMoney.toLong()
    val thirdCountInt get() = thirdCount.toInt()
    val thirdMoneyLong get() = thirdMoney.toLong()
    val fourthCountInt get() = fourthCount.toInt()
    val fourthMoneyLong get() = fourthMoney.toLong()
    val fifthCountInt get() = fifthCount.toInt()
    val fifthMoneyLong get() = fifthMoney.toLong()

    val bonusInt get() = bonus.toInt()
    val num1Int get() = num1.toInt()
    val num2Int get() = num2.toInt()
    val num3Int get() = num3.toInt()
    val num4Int get() = num4.toInt()
    val num5Int get() = num5.toInt()
    val num6Int get() = num6.toInt()

    val roundInt get() = round.toInt()
}