package com.queentech.data

import com.queentech.data.di.LOTTO_SERVER_URL
import com.queentech.data.model.service.LottoService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
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
            .baseUrl(server.url(LOTTO_SERVER_URL))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(LottoService::class.java)
    }

    @After
    fun tearDown() { server.shutdown() }

    @Test
    fun successParse200() = runTest {
    }

    @Test
    fun `500 에러 변환`() = runTest {
    }
}