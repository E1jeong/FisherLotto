package com.queentech.domain.model.lotto

data class GetLottoNumber(
    val firstCount: String,
    val firstMoney: String,
    val secondCount: String,
    val secondMoney: String,
    val thirdCount: String,
    val thirdMoney: String,
    val fourthCount: String,
    val fourthMoney: String,
    val fifthCount: String,
    val fifthMoney: String,

    val bonus: String,
    val num1: String,
    val num2: String,
    val num3: String,
    val num4: String,
    val num5: String,
    val num6: String,

    val pdate: String,   // "2026-02-07"
    val round: String    // "1210"
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