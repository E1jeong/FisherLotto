package com.queentech.data.usecase.fcm

import com.google.firebase.messaging.FirebaseMessaging
import com.queentech.data.database.datastore.FcmLocalDataSource
import com.queentech.data.model.fcm.DeleteUserRequest
import com.queentech.data.model.fcm.FcmTokenRequest
import com.queentech.data.model.service.FcmService
import com.queentech.domain.usecase.fcm.FcmRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class FcmRepositoryImpl @Inject constructor(
    private val fcmLocalDataSource: FcmLocalDataSource,
    private val fcmService: FcmService,
) : FcmRepository {

    override suspend fun getFreshToken(): String? = suspendCancellableCoroutine { continuation ->
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token -> continuation.resume(token) }
            .addOnFailureListener { continuation.resume(null) }
    }

    override suspend fun getCachedToken(): String? {
        return fcmLocalDataSource.getToken()
    }

    override suspend fun saveTokenToCache(token: String) {
        fcmLocalDataSource.saveToken(token)
    }

    override suspend fun getCachedEmail(): String? {
        return fcmLocalDataSource.getEmail()
    }

    override suspend fun saveEmailToCache(email: String) {
        fcmLocalDataSource.saveEmail(email)
    }

    override suspend fun hasShownNotificationPermissionPrompt(): Boolean {
        return fcmLocalDataSource.hasShownNotificationPermissionPrompt()
    }

    override suspend fun markNotificationPermissionPromptShown() {
        fcmLocalDataSource.markNotificationPermissionPromptShown()
    }

    override suspend fun sendTokenToServer(email: String, fcmToken: String): Result<Unit> {
        return try {
            fcmService.registerToken(FcmTokenRequest(email = email, fcmToken = fcmToken))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteUser(email: String): Result<Unit> {
        return try {
            fcmService.deleteUser(DeleteUserRequest(email = email))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
