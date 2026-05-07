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
    private fun String.toCleanLong() = replace(Regex("[^0-9]"), "").toLong()
    private fun String.toCleanInt() = replace(Regex("[^0-9]"), "").toInt()

    val firstCountInt get() = firstCount.toCleanInt()
    val firstMoneyLong get() = firstMoney.toCleanLong()
    val secondCountInt get() = secondCount.toCleanInt()
    val secondMoneyLong get() = secondMoney.toCleanLong()
    val thirdCountInt get() = thirdCount.toCleanInt()
    val thirdMoneyLong get() = thirdMoney.toCleanLong()
    val fourthCountInt get() = fourthCount.toCleanInt()
    val fourthMoneyLong get() = fourthMoney.toCleanLong()
    val fifthCountInt get() = fifthCount.toCleanInt()
    val fifthMoneyLong get() = fifthMoney.toCleanLong()

    val bonusInt get() = bonus.toCleanInt()
    val num1Int get() = num1.toCleanInt()
    val num2Int get() = num2.toCleanInt()
    val num3Int get() = num3.toCleanInt()
    val num4Int get() = num4.toCleanInt()
    val num5Int get() = num5.toCleanInt()
    val num6Int get() = num6.toCleanInt()

    val roundInt get() = round.toCleanInt()
}