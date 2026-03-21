package com.queentech.domain.model.lotto

import org.junit.Assert.assertEquals
import org.junit.Test

class GetExpectNumberTest {

    @Test
    fun `toLottoNumbers splits comma-separated string into int list`() {
        val expectNumber = GetExpectNumber(count = 1, lotto = listOf("1, 5, 12, 23, 34, 45"))

        with(expectNumber) {
            val result = expectNumber.lotto[0].toLottoNumbers()
            assertEquals(listOf(1, 5, 12, 23, 34, 45), result)
        }
    }

    @Test
    fun `toLottoNumbers works with single number`() {
        val expectNumber = GetExpectNumber(count = 1, lotto = listOf("42"))

        with(expectNumber) {
            val result = expectNumber.lotto[0].toLottoNumbers()
            assertEquals(listOf(42), result)
        }
    }
}
