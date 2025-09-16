package com.queentech.data

import com.queentech.data.di.DH_LOTTERY_URL
import com.queentech.data.model.service.LottoService
import com.queentech.data.model.service.LottoService.Companion.GET_LOTTO_NUMBER
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.fail
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class LottoServiceTest {
    private lateinit var server: MockWebServer
    private lateinit var api: LottoService

    @Before
    fun setup() {
        server = MockWebServer().apply { start() }
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())   // ✅ 필수
            .build()
        api = Retrofit.Builder()
            .baseUrl(server.url(DH_LOTTERY_URL))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(LottoService::class.java)
    }

    @After
    fun tearDown() { server.shutdown() }

    @Test
    fun successParse200() = runTest {
        server.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """
                {
                  "totSellamnt": 1,
                  "returnValue": "ok",
                  "firstWinamnt": 1,
                  "firstPrzwnerCo": 1,
                  "firstAccumamnt": 1,
                  "drwNo": 1128,
                  "drwNoDate": "2020-01-01",
                  "drwtNo1": 1,
                  "drwtNo2": 2,
                  "drwtNo3": 3,
                  "drwtNo4": 4,
                  "drwtNo5": 5,
                  "drwtNo6": 6,
                  "bnusNo": 7
                }
                """.trimIndent()
            )
        )

        val res = api.getNumber(drwNo = 1128)
        assertNotNull(res)

        val req = server.takeRequest()
        val url = req.requestUrl!!
        assertEquals("/common.do", url.encodedPath) // DHLOTTERY_SERVER_COMMON = "common.do?"
        assertEquals(LottoService.GET_LOTTO_NUMBER, url.queryParameter(LottoService.METHOD))
        assertEquals("1128", url.queryParameter(LottoService.DRW_NO))
    }

    @Test
    fun `500 에러 변환`() = runTest {
        server.enqueue(MockResponse().setResponseCode(500))
        try { api.getNumber(GET_LOTTO_NUMBER, 1128); fail("should throw") }
        catch (e: HttpException) { assertEquals(500, e.code()) }
    }
}