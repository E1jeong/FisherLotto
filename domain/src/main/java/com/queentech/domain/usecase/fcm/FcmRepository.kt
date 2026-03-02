package com.queentech.domain.usecase.fcm

interface FcmRepository {
    // 내부 저장소(DataStore)에서 토큰 읽기
    suspend fun getCachedToken(): String?

    // 내부 저장소에 토큰 저장
    suspend fun saveTokenToCache(token: String)

    // Next.js 서브 서버로 토큰 전송
    suspend fun sendTokenToServer(email: String, fcmToken: String): Result<Unit>
}