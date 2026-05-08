package com.queentech.data.usecase.login

import android.util.Log
import com.queentech.data.database.datastore.UserLocalDataSource
import com.queentech.data.database.room.dao.LottoIssueDao
import com.queentech.data.database.room.dao.ScanHistoryDao
import com.queentech.data.model.login.GetUserRequestBody
import com.queentech.data.model.login.SignUpUserRequestBody
import com.queentech.data.model.login.TierRequest
import com.queentech.data.model.service.LottoService
import com.queentech.data.model.service.UserService
import com.queentech.domain.model.login.SignUpResultStatus
import com.queentech.domain.model.login.User
import com.queentech.domain.usecase.login.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userService: UserService,
    private val lottoService: LottoService,
    private val localDataSource: UserLocalDataSource,
    private val lottoIssueDao: LottoIssueDao,
    private val scanHistoryDao: ScanHistoryDao,
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

            val mainResponse = lottoService.registerUser(requestBody)
            if (mainResponse.statusInt != SignUpResultStatus.OK.status) {
                val errorMessage = when (mainResponse.statusInt) {
                    SignUpResultStatus.DUPLICATED_EMAIL.status -> "이미 등록된 이메일입니다."
                    SignUpResultStatus.DUPLICATED_PHONE_NUMBER.status -> "이미 등록된 전화번호입니다."
                    SignUpResultStatus.ERROR_REGISTER.status -> "등록 중 오류가 발생했습니다."
                    SignUpResultStatus.ERROR_REQUEST.status -> "요청 오류가 발생했습니다."
                    else -> "번호 발급 중 오류가 발생했습니다. (${mainResponse.status})"
                }
                return Result.failure(Exception(errorMessage))
            }

            val response = userService.signUpUser(requestBody)

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
            val response = userService.getUser(body)

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
        lottoIssueDao.deleteAll()
        scanHistoryDao.deleteAll()
    }

    override suspend fun updateTier(tier: String) {
        _currentUser.value = _currentUser.value?.copy(tier = tier)
        localDataSource.updateTier(tier)

        val user = _currentUser.value ?: return
        try {
            userService.updateTier(
                TierRequest(
                    email = user.email,
                    phone = user.phone,
                    isPremium = tier == User.TIER_PREMIUM,
                )
            )
        } catch (e: Exception) {
            Log.w(TAG, "서버 tier 업데이트 실패 (로컬은 적용됨)", e)
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        val user = _currentUser.value
            ?: return Result.failure(Exception("로그인된 사용자가 없습니다."))

        return try {
            userService.withdraw(GetUserRequestBody(email = user.email, phone = user.phone))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            // 서버 처리 성공/실패와 무관하게 로컬 데이터는 항상 정리
            logout()
        }
    }

    companion object {
        private const val TAG = "UserRepositoryImpl"
    }
}
