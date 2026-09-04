package com.queentech.domain.usecase.fcm

interface FcmRepository {
    // 푸시 제공자에서 현재 토큰 조회. 실패 시 null
    suspend fun getFreshToken(): String?

    // 내부 저장소(DataStore)에서 토큰 읽기
    suspend fun getCachedToken(): String?

    // 내부 저장소에 토큰 저장
    suspend fun saveTokenToCache(token: String)

    // 내부 저장소에서 이메일 읽기
    suspend fun getCachedEmail(): String?

    // 내부 저장소에 이메일 저장
    suspend fun saveEmailToCache(email: String)

    // 알림 권한 사전 안내를 이미 표시했는지 확인
    suspend fun hasShownNotificationPermissionPrompt(): Boolean

    // 알림 권한 사전 안내 표시 이력 저장
    suspend fun markNotificationPermissionPromptShown()

    // Next.js 서브 서버로 토큰 전송
    suspend fun sendTokenToServer(email: String, fcmToken: String): Result<Unit>

    // 서브 서버에서 유저 데이터 삭제
    suspend fun deleteUser(email: String): Result<Unit>
}
