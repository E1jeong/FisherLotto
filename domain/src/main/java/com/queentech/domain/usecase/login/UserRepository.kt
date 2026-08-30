package com.queentech.domain.usecase.login

import com.queentech.domain.model.login.User
import com.queentech.domain.model.common.CommonResponse
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val currentUser: StateFlow<User?>
    suspend fun sendVerificationCode(email: String): Result<CommonResponse>
    suspend fun verifyEmailCode(email: String, code: String): Result<CommonResponse>
    suspend fun signUp(
        name: String,
        email: String,
        birth: String,
        phone: String,
        verificationToken: String,
    ): Result<User>
    suspend fun login(name: String, birth: String, phone: String, email: String): Result<User>
    suspend fun loadCachedUser()
    suspend fun logout()
    suspend fun deleteAccount(): Result<Unit>
    suspend fun updateTier(tier: String)
}
