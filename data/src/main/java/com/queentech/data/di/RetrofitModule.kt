package com.queentech.data.di

import com.queentech.data.model.service.LottoService
import com.queentech.data.model.service.OpenBankingService
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


const val LOTTO_SERVER_URL = "http://www.fisherlotto.com:10907/"
const val LOTTO_SUB_BACKEND_URL = "https://lotto-sub-backend.vercel.app/"

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
            .baseUrl(LOTTO_SERVER_URL)
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
    @Named("lotto-sub")
    fun providePaymentsRetrofit(client: OkHttpClient): Retrofit {
        val gsonConverterFactory = GsonConverterFactory.create()

        return Retrofit.Builder()
            .baseUrl(LOTTO_SUB_BACKEND_URL)
            .addConverterFactory(gsonConverterFactory)
            .client(client)
            .build()
    }

    @Provides
    fun providePaymentsService(@Named("lotto-sub") retrofit: Retrofit): PaymentsService {
        return retrofit.create(PaymentsService::class.java)
    }

    // --- 오픈뱅킹 서비스 (lotto-sub 서버 사용) ---
    @Provides
    fun provideOpenBankingService(@Named("lotto-sub") retrofit: Retrofit): OpenBankingService {
        return retrofit.create(OpenBankingService::class.java)
    }
}
