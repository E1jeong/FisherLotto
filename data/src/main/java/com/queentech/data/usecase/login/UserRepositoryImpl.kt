package com.queentech.data.usecase.login

import com.queentech.data.database.local.UserLocalDataSource
import com.queentech.data.model.login.GetUserRequestBody
import com.queentech.data.model.login.SignUpUserRequestBody
import com.queentech.data.model.service.LottoService
import com.queentech.domain.model.login.SignUpResultStatus
import com.queentech.domain.model.login.User
import com.queentech.domain.usecase.login.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val lottoService: LottoService,
    private val localDataSource: UserLocalDataSource
) : UserRepository {

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    override suspend fun signUp(
        name: String,
        email: String,
        birth: String,
        phone: String
    ): Result<User> {
        return try {
            val requestBody = SignUpUserRequestBody(
                name = name,
                email = email,
                birth = birth,
                phone = phone
            )

            val response = lottoService.signUpUser(requestBody)

            if (response.statusInt == SignUpResultStatus.OK.status) {
                val user = User(name, email, birth, phone)
                localDataSource.saveUser(user) // DataStore에 영속 저장
                Result.success(user)
            } else {
                val errorMessage = when (response.statusInt) {
                    SignUpResultStatus.DUPLICATED_EMAIL.status -> "이미 등록된 이메일입니다."
                    SignUpResultStatus.DUPLICATED_PHONE_NUMBER.status -> "이미 등록된 전화번호입니다."
                    SignUpResultStatus.ERROR_REGISTER.status -> "등록 중 오류가 발생했습니다."
                    SignUpResultStatus.ERROR_REQUEST.status -> "요청 오류가 발생했습니다."
                    else -> "회원가입에 실패했습니다. (${response.status})"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(
        name: String,
        birth: String,
        phone: String,
        email: String
    ): Result<User> {
        return try {
            val body = GetUserRequestBody(
                email = email,
                phone = phone
            )

            // 서버에 유저 존재 여부 조회
            val response = lottoService.getUser(body)

            if (response.statusInt == 8200) {
                val user = User(name, email, birth, phone)
                _currentUser.value = user
                localDataSource.saveUser(user) // DataStore에 영속 저장
                Result.success(user)
            } else {
                Result.failure(Exception("사용자를 찾을 수 없습니다"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 앱 재시작 시 DataStore에서 복원
    override suspend fun loadCachedUser() {
        localDataSource.userFlow.first()?.let { cached ->
            _currentUser.value = cached
        }
    }

    override suspend fun logout() {
        _currentUser.value = null
        localDataSource.clear()
    }
}
