package com.queentech.domain.model.lotto

import org.junit.Assert.assertEquals
import org.junit.Test

class GetLottoNumberTest {

    private fun createLottoNumber(
        round: String = "1210",
        num1: String = "3",
        num2: String = "12",
        num3: String = "18",
        num4: String = "27",
        num5: String = "33",
        num6: String = "45",
        bonus: String = "7",
        firstMoney: String = "2000000000",
    ) = GetLottoNumber(
        firstCount = "10", firstMoney = firstMoney,
        secondCount = "50", secondMoney = "60000000",
        thirdCount = "2000", thirdMoney = "1500000",
        fourthCount = "100000", fourthMoney = "50000",
        fifthCount = "2000000", fifthMoney = "5000",
        bonus = bonus,
        num1 = num1, num2 = num2, num3 = num3,
        num4 = num4, num5 = num5, num6 = num6,
        pdate = "2026-02-07", round = round,
    )

    @Test
    fun `roundInt parses string round to Int`() {
        val lotto = createLottoNumber(round = "1210")
        assertEquals(1210, lotto.roundInt)
    }

    @Test
    fun `num1Int through num6Int parse correctly`() {
        val lotto = createLottoNumber(
            num1 = "3", num2 = "12", num3 = "18",
            num4 = "27", num5 = "33", num6 = "45",
        )
        assertEquals(3, lotto.num1Int)
        assertEquals(12, lotto.num2Int)
        assertEquals(18, lotto.num3Int)
        assertEquals(27, lotto.num4Int)
        assertEquals(33, lotto.num5Int)
        assertEquals(45, lotto.num6Int)
    }

    @Test
    fun `bonusInt parses correctly`() {
        val lotto = createLottoNumber(bonus = "7")
        assertEquals(7, lotto.bonusInt)
    }

    @Test
    fun `firstMoneyLong parses large number correctly`() {
        val lotto = createLottoNumber(firstMoney = "2000000000")
        assertEquals(2_000_000_000L, lotto.firstMoneyLong)
    }
}
