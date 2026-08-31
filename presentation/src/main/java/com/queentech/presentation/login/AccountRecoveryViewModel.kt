package com.queentech.presentation.login

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.queentech.domain.model.login.EmailVerificationPurpose
import com.queentech.domain.model.login.SignUpResultStatus
import com.queentech.domain.usecase.billing.BillingRepository
import com.queentech.domain.usecase.fcm.FcmRepository
import com.queentech.domain.usecase.login.UserRepository
import com.queentech.presentation.util.ValidCheckHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class AccountRecoveryViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val fcmRepository: FcmRepository,
    private val billingRepository: BillingRepository,
) : ViewModel(), ContainerHost<AccountRecoveryState, AccountRecoverySideEffect> {

    override val container: Container<AccountRecoveryState, AccountRecoverySideEffect> = container(
        initialState = AccountRecoveryState(),
        buildSettings = {
            exceptionHandler = CoroutineExceptionHandler { _, _ ->
                intent {
                    reduce { state.copy(isRecovering = false) }
                    postSideEffect(AccountRecoverySideEffect.Toast("계정 복구 중 오류가 발생했습니다."))
                    Log.e(TAG, "Account recovery failed")
                }
            }
        },
    )

    private var verificationToken: String? = null
    private var verificationEmail: String? = null
    private var countdownJob: Job? = null

    @OptIn(OrbitExperimental::class)
    fun onEmailChanged(value: String) = blockingIntent {
        val normalizedEmail = value.trim().lowercase()
        if (verificationEmail != null && verificationEmail != normalizedEmail) {
            clearVerification()
            reduce {
                state.copy(
                    verificationStep = EmailVerificationStep.IDLE,
                    remainingSeconds = 0,
                    verificationError = null,
                )
            }
        }
    }

    fun onSendVerificationCode(inputEmail: String) = intent {
        val email = inputEmail.trim().lowercase()
        if (!ValidCheckHelper.checkEmail(email)) {
            reduce { state.copy(verificationError = "이메일 형식을 확인해주세요.") }
            return@intent
        }

        countdownJob?.cancel()
        verificationEmail = email
        verificationToken = null
        reduce {
            state.copy(
                verificationStep = EmailVerificationStep.SENDING,
                remainingSeconds = 0,
                verificationError = null,
            )
        }

        userRepository.sendVerificationCode(email, EmailVerificationPurpose.RECOVERY)
            .onSuccess { response ->
                if (verificationEmail != email) return@onSuccess
                if (response.statusInt == SignUpResultStatus.OK.status) {
                    reduce {
                        state.copy(
                            verificationStep = EmailVerificationStep.CODE_SENT,
                            remainingSeconds = CODE_TTL_SECONDS,
                        )
                    }
                    startCountdown(email)
                    postSideEffect(AccountRecoverySideEffect.Toast("인증번호를 발송했습니다."))
                } else {
                    reduce {
                        state.copy(
                            verificationStep = EmailVerificationStep.IDLE,
                            verificationError = "인증번호를 발송하지 못했습니다. 다시 시도해주세요.",
                        )
                    }
                }
            }
            .onFailure {
                if (verificationEmail == email) {
                    reduce {
                        state.copy(
                            verificationStep = EmailVerificationStep.IDLE,
                            verificationError = "인증번호를 발송하지 못했습니다. 네트워크를 확인해주세요.",
                        )
                    }
                }
            }
    }

    fun onVerifyEmailCode(inputEmail: String, inputCode: String) = intent {
        val email = inputEmail.trim().lowercase()
        val code = inputCode.trim()
        if (verificationEmail != email || state.verificationStep != EmailVerificationStep.CODE_SENT) {
            reduce { state.copy(verificationError = "인증번호를 새로 받아주세요.") }
            return@intent
        }
        if (!code.matches(Regex("^\\d{6}$"))) {
            reduce { state.copy(verificationError = "6자리 인증번호를 입력해주세요.") }
            return@intent
        }

        reduce { state.copy(verificationStep = EmailVerificationStep.VERIFYING, verificationError = null) }
        userRepository.verifyEmailCode(email, code, EmailVerificationPurpose.RECOVERY)
            .onSuccess { response ->
                if (verificationEmail != email) return@onSuccess
                val token = response.verificationToken
                if (response.statusInt == SignUpResultStatus.OK.status && !token.isNullOrBlank()) {
                    verificationToken = token
                    countdownJob?.cancel()
                    reduce {
                        state.copy(
                            verificationStep = EmailVerificationStep.VERIFIED,
                            remainingSeconds = 0,
                        )
                    }
                } else {
                    reduce {
                        state.copy(
                            verificationStep = EmailVerificationStep.CODE_SENT,
                            verificationError = "인증번호를 확인하지 못했습니다. 다시 시도해주세요.",
                        )
                    }
                }
            }
            .onFailure {
                if (verificationEmail == email) {
                    reduce {
                        state.copy(
                            verificationStep = EmailVerificationStep.CODE_SENT,
                            verificationError = "인증번호를 확인하지 못했습니다. 네트워크를 확인해주세요.",
                        )
                    }
                }
            }
    }

    fun onRecoverClick(inputEmail: String, inputPhone: String) = intent {
        if (state.isRecovering) return@intent
        val email = inputEmail.trim().lowercase()
        val phone = inputPhone.trim()
        val token = verificationToken
        if (state.verificationStep != EmailVerificationStep.VERIFIED || verificationEmail != email || token == null) {
            postSideEffect(AccountRecoverySideEffect.Toast("이메일 인증을 완료해주세요."))
            return@intent
        }
        if (phone.length < 10) {
            postSideEffect(AccountRecoverySideEffect.Toast("전화번호를 확인해주세요."))
            return@intent
        }

        reduce { state.copy(isRecovering = true) }
        userRepository.recoverAccount(email, phone, token)
            .onSuccess { user ->
                clearVerification()
                registerFcmToken(user.email)
                billingRepository.refreshSubscriptionStatus()
                reduce { state.copy(isRecovering = false) }
                postSideEffect(AccountRecoverySideEffect.NavigateToHome)
            }
            .onFailure {
                reduce { state.copy(isRecovering = false) }
                postSideEffect(AccountRecoverySideEffect.Toast("계정을 복구하지 못했습니다. 정보를 확인해주세요."))
            }
    }

    private suspend fun registerFcmToken(email: String) {
        val token = fcmRepository.getFreshToken() ?: return
        if (token == fcmRepository.getCachedToken() && email == fcmRepository.getCachedEmail()) return
        fcmRepository.sendTokenToServer(email, token)
            .onSuccess {
                fcmRepository.saveTokenToCache(token)
                fcmRepository.saveEmailToCache(email)
            }
            .onFailure { Log.e(TAG, "FCM token submission failed") }
    }

    private fun startCountdown(email: String) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            for (remaining in CODE_TTL_SECONDS - 1 downTo 0) {
                delay(1_000L)
                intent {
                    if (verificationEmail != email || state.verificationStep !in setOf(EmailVerificationStep.CODE_SENT, EmailVerificationStep.VERIFYING)) return@intent
                    if (remaining == 0) {
                        clearVerification()
                        reduce {
                            state.copy(
                                verificationStep = EmailVerificationStep.EXPIRED,
                                remainingSeconds = 0,
                                verificationError = "인증시간이 만료되었습니다. 새 인증번호를 받아주세요.",
                            )
                        }
                    } else {
                        reduce { state.copy(remainingSeconds = remaining) }
                    }
                }
            }
        }
    }

    private fun clearVerification() {
        countdownJob?.cancel()
        countdownJob = null
        verificationToken = null
        verificationEmail = null
    }

    override fun onCleared() {
        clearVerification()
        super.onCleared()
    }

    private companion object {
        const val TAG = "AccountRecoveryViewModel"
        const val CODE_TTL_SECONDS = 5 * 60
    }
}

@Immutable
data class AccountRecoveryState(
    val verificationStep: EmailVerificationStep = EmailVerificationStep.IDLE,
    val remainingSeconds: Int = 0,
    val verificationError: String? = null,
    val isRecovering: Boolean = false,
)

sealed interface AccountRecoverySideEffect {
    data class Toast(val message: String) : AccountRecoverySideEffect
    data object NavigateToHome : AccountRecoverySideEffect
}
