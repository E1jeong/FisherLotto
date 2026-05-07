package com.queentech.data.di

import com.queentech.data.model.service.BillingService
import com.queentech.data.model.service.FcmService
import com.queentech.data.model.service.LottoService
import com.queentech.data.model.service.SubLottoService
import com.queentech.data.model.service.UserService
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
const val LOTTO_SUB_BACKEND_URL = "http://www.fisherlotto.com:3001/"

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

    // --- 메인서버 Retrofit ---
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

    // --- 서브서버 Retrofit ---
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

    // --- FCM 서비스 (lotto-sub 서버 사용) ---
    @Provides
    fun provideFcmService(@Named("lotto-sub") retrofit: Retrofit): FcmService {
        return retrofit.create(FcmService::class.java)
    }

    // --- Billing 서비스 (lotto-sub 서버 사용) ---
    @Provides
    fun provideBillingService(@Named("lotto-sub") retrofit: Retrofit): BillingService {
        return retrofit.create(BillingService::class.java)
    }

    // --- User 서비스 (lotto-sub 서버 사용) ---
    @Provides
    fun provideUserService(@Named("lotto-sub") retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }

    // --- SubLotto 서비스 (lotto-sub 서버 사용) ---
    @Provides
    fun provideSubLottoService(@Named("lotto-sub") retrofit: Retrofit): SubLottoService {
        return retrofit.create(SubLottoService::class.java)
    }
}
