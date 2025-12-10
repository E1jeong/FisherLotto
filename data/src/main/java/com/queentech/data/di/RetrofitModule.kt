package com.queentech.data.di

import com.queentech.data.model.service.LottoService
import com.queentech.data.model.service.PaymentsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named


const val DH_LOTTERY_URL = "https://www.dhlottery.co.kr/"
const val PAYMENTS_BASE_URL = "https://kspay-backend.vercel.app/"

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // 로그 레벨 설정 (BODY는 요청과 응답의 모든 정보를 로그로 출력)
    }

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient
            .Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS) // 연결 타임아웃을 30초로 설정
            .writeTimeout(30, TimeUnit.SECONDS) // 쓰기 타임아웃을 30초로 설정
            .readTimeout(30, TimeUnit.SECONDS) // 읽기 타임아웃을 30초로 설정
            .build()
    }

    // --- 로또용 Retrofit ---
    @Provides
    @Named("lotto")
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        val gsonConverterFactory = GsonConverterFactory.create()

        return Retrofit.Builder()
            .baseUrl(DH_LOTTERY_URL)
            .addConverterFactory(gsonConverterFactory)
            .client(client)
            .build()
    }

    @Provides
    fun provideLottoService(@Named("lotto") retrofit: Retrofit): LottoService {
        return retrofit.create(LottoService::class.java)
    }

    // --- 결제 서버용 Retrofit ---
    @Provides
    @Named("payments")
    fun providePaymentsRetrofit(client: OkHttpClient): Retrofit {
        val gsonConverterFactory = GsonConverterFactory.create()

        return Retrofit.Builder()
            .baseUrl(PAYMENTS_BASE_URL)
            .addConverterFactory(gsonConverterFactory)
            .client(client)
            .build()
    }

    @Provides
    fun providePaymentsService(@Named("payments") retrofit: Retrofit): PaymentsService {
        return retrofit.create(PaymentsService::class.java)
    }
}
