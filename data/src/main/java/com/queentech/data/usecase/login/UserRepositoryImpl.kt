package com.queentech.data.usecase.login

import com.queentech.data.database.datastore.UserLocalDataSource
import com.queentech.data.database.room.dao.LottoIssueDao
import com.queentech.data.database.room.dao.ScanHistoryDao
import com.queentech.data.model.common.toDomainModel
import com.queentech.data.model.login.EmailRequestBody
import com.queentech.data.model.login.GetUserRequestBody
import com.queentech.data.model.login.RecoverUserRequestBody
import com.queentech.data.model.login.SignUpUserRequestBody
import com.queentech.data.model.login.VerifyEmailCodeRequestBody
import com.queentech.data.model.service.UserService
import com.queentech.domain.model.common.CommonResponse
import com.queentech.domain.model.login.SignUpException
import com.queentech.domain.model.login.SignUpResultStatus
import com.queentech.domain.model.login.User
import com.queentech.domain.model.login.EmailVerificationPurpose
import com.queentech.domain.usecase.login.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userService: UserService,
    private val localDataSource: UserLocalDataSource,
    private val lottoIssueDao: LottoIssueDao,
    private val scanHistoryDao: ScanHistoryDao,
) : UserRepository {

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    override suspend fun sendVerificationCode(
        email: String,
        purpose: EmailVerificationPurpose,
    ): Result<CommonResponse> = runCatching {
        userService.sendVerificationCode(
            EmailRequestBody(
                email = email.trim().lowercase(),
                purpose = purpose.name.lowercase(),
            )
        ).toDomainModel()
    }

    override suspend fun verifyEmailCode(
        email: String,
        code: String,
        purpose: EmailVerificationPurpose,
    ): Result<CommonResponse> = runCatching {
        userService.verifyEmailCode(
            VerifyEmailCodeRequestBody(
                email = email.trim().lowercase(),
                code = code,
                purpose = purpose.name.lowercase(),
            )
        ).toDomainModel()
    }

    override suspend fun recoverAccount(
        email: String,
        phone: String,
        verificationToken: String,
    ): Result<User> = runCatching {
        val response = userService.recoverUser(
            RecoverUserRequestBody(
                email = email.trim().lowercase(),
                phone = phone.trim(),
                verificationToken = verificationToken,
            )
        )
        if (response.status.toIntOrNull() != SignUpResultStatus.OK.status) {
            throw IllegalStateException("Account recovery failed")
        }

        val user = User(
            name = response.name?.takeIf(String::isNotBlank)
                ?: throw IllegalStateException("Missing recovered name"),
            email = response.email?.takeIf(String::isNotBlank)
                ?: throw IllegalStateException("Missing recovered email"),
            birth = response.birth?.takeIf(String::isNotBlank)
                ?: throw IllegalStateException("Missing recovered birth"),
            phone = response.phone?.takeIf(String::isNotBlank)
                ?: throw IllegalStateException("Missing recovered phone"),
            tier = response.tier?.takeIf { it == User.TIER_FREE || it == User.TIER_PREMIUM }
                ?: throw IllegalStateException("Invalid recovered tier"),
        )
        localDataSource.saveUser(user)
        _currentUser.value = user
        user
    }

    override suspend fun signUp(
        name: String,
        email: String,
        birth: String,
        phone: String,
        verificationToken: String,
    ): Result<User> {
        return try {
            val normalizedEmail = email.trim().lowercase()
            val requestBody = SignUpUserRequestBody(
                name = name,
                email = normalizedEmail,
                birth = birth,
                phone = phone,
                verificationToken = verificationToken,
            )

            val response = userService.signUpUser(requestBody).toDomainModel()
            if (response.statusInt == SignUpResultStatus.OK.status) {
                val user = User(name, normalizedEmail, birth, phone)
                localDataSource.saveUser(user) // DataStore에 영속 저장
                Result.success(user)
            } else {
                val status = SignUpResultStatus.entries.firstOrNull {
                    it.status == response.statusInt
                } ?: SignUpResultStatus.ERROR
                val errorMessage = when (status) {
                    SignUpResultStatus.DUPLICATED_EMAIL -> "이미 등록된 이메일입니다."
                    SignUpResultStatus.DUPLICATED_PHONE_NUMBER -> "이미 등록된 전화번호입니다."
                    SignUpResultStatus.ERROR_REGISTER -> "등록 중 오류가 발생했습니다."
                    SignUpResultStatus.ERROR_REQUEST -> "요청 오류가 발생했습니다."
                    SignUpResultStatus.EMAIL_PROOF_INVALID -> "이메일 인증을 다시 진행해주세요."
                    else -> "회원가입에 실패했습니다. (${response.status})"
                }
                return Result.failure(SignUpException(status, errorMessage))
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
            val response = userService.getUser(body).toDomainModel()

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
        val user = runCatching { localDataSource.userFlow.first() }.getOrNull()
        _currentUser.value = user
    }

    override suspend fun logout() {
        _currentUser.value = null
        runCatching { localDataSource.clear() }
        runCatching { lottoIssueDao.deleteAll() }
        runCatching { scanHistoryDao.deleteAll() }
    }

    // 서버 tier는 서브백엔드가 Google Play 검증 결과로만 갱신한다. 앱은 로컬 캐시만 관리한다.
    override suspend fun updateTier(tier: String) {
        val user = _currentUser.value ?: return
        _currentUser.value = user.copy(tier = tier)
        localDataSource.updateTier(tier)
    }

    override suspend fun deleteAccount(): Result<Unit> {
        val user = _currentUser.value
            ?: return Result.failure(Exception("로그인된 사용자가 없습니다."))

        return try {
            val response = userService.withdraw(
                GetUserRequestBody(email = user.email, phone = user.phone)
            ).toDomainModel()
            if (response.statusInt == SignUpResultStatus.OK.status) {
                logout()
                Result.success(Unit)
            } else {
                Result.failure(Exception("회원탈퇴에 실패했습니다."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
