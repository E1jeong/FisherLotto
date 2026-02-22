package com.queentech.domain.usecase.login

import com.queentech.domain.model.login.User
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val currentUser: StateFlow<User?>
    suspend fun signUp(name: String, email: String, birth: String, phone: String): Result<User>
    suspend fun login(name: String, birth: String, phone: String, email: String): Result<User>
    suspend fun loadCachedUser()
    suspend fun logout()
}
