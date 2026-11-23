package com.queentech.presentation.main.camera

import android.net.Uri
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class LottoQrResultTest {

    @Before
    fun setUp() {
        mockkStatic(Uri::class)
    }

    @After
    fun tearDown() {
        unmockkStatic(Uri::class)
    }

    private fun parseValue(value: String): LottoQrResult? {
        val raw = "http://qr.dhlottery.co.kr/?v=$value"
        val uri: Uri = mockk()
        every { Uri.parse(raw) } returns uri
        every { uri.getQueryParameter("v") } returns value
        return LottoQrResult.parse(raw)
    }

    @Test
    fun `구분 문자 뒤 게임만 읽고 꼬리 숫자는 게임으로 만들지 않는다`() {
        // 실제 5게임 용지 형식: 회차 + (q + 12자리) x 5 + 18자리 꼬리 숫자
        val value = "1251" +
            "q101935374142q222734364144q031314344042q061213293239q010213243237" +
            "010203040506070809"

        val result = parseValue(value)

        assertEquals(1251, result?.drawNo)
        assertEquals(
            listOf(
                listOf(10, 19, 35, 37, 41, 42),
                listOf(22, 27, 34, 36, 41, 44),
                listOf(3, 13, 14, 34, 40, 42),
                listOf(6, 12, 13, 29, 32, 39),
                listOf(1, 2, 13, 24, 32, 37),
            ),
            result?.games,
        )
    }

    @Test
    fun `수동 구분 문자도 게임으로 읽는다`() {
        val result = parseValue("1251m010203040506q071122334445")

        assertEquals(
            listOf(listOf(1, 2, 3, 4, 5, 6), listOf(7, 11, 22, 33, 44, 45)),
            result?.games,
        )
    }

    @Test
    fun `구분 문자가 없는 값은 12자리 단위로 읽고 남는 숫자는 무시한다`() {
        val result = parseValue("12100218253034442233")

        assertEquals(1210, result?.drawNo)
        assertEquals(listOf(listOf(2, 18, 25, 30, 34, 44)), result?.games)
    }

    @Test
    fun `45를 넘는 번호가 있는 게임은 버린다`() {
        assertNull(parseValue("1251q152840877811"))
    }
}
