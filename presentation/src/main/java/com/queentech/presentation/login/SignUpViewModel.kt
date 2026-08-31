package com.queentech.presentation.login

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.queentech.domain.model.login.SignUpException
import com.queentech.domain.model.login.SignUpResultStatus
import com.queentech.domain.model.login.EmailVerificationPurpose
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
class SignUpViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel(), ContainerHost<SignUpState, SignUpSideEffect> {

    override val container: Container<SignUpState, SignUpSideEffect> = container(
        initialState = SignUpState(),
        buildSettings = {
            exceptionHandler = CoroutineExceptionHandler { _, _ ->
                intent {
                    postSideEffect(SignUpSideEffect.Toast("회원가입 중 오류가 발생했습니다."))
                    Log.e(TAG, "Sign-up failed")
                }
            }
        },
    )

    private var verificationToken: String? = null
    private var verificationEmail: String? = null
    private var countdownJob: Job? = null

    companion object {
        const val TAG = "SignUpViewModel"
        const val CODE_TTL_SECONDS = 5 * 60
    }

    @OptIn(OrbitExperimental::class)
    fun onEmailChanged(value: String) = blockingIntent {
        val normalizedEmail = value.trim().lowercase()
        if (verificationEmail != null && verificationEmail != normalizedEmail) {
            clearVerification()
            reduce {
                state.copy(
                    emailVerificationStep = EmailVerificationStep.IDLE,
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
        countdownJob = null

        val previousStep = state.emailVerificationStep
        val previousEmail = verificationEmail
        verificationEmail = email
        verificationToken = null
        reduce {
            state.copy(
                emailVerificationStep = EmailVerificationStep.SENDING,
                remainingSeconds = 0,
                verificationError = null,
            )
        }

        userRepository.sendVerificationCode(email, EmailVerificationPurpose.REGISTRATION)
            .onSuccess { response ->
                if (verificationEmail != email) return@onSuccess
                when (response.statusInt) {
                    SignUpResultStatus.OK.status -> {
                        reduce {
                            state.copy(
                                emailVerificationStep = EmailVerificationStep.CODE_SENT,
                                remainingSeconds = CODE_TTL_SECONDS,
                                verificationError = null,
                            )
                        }
                        startCountdown(email)
                        postSideEffect(SignUpSideEffect.Toast("인증번호를 발송했습니다."))
                    }

                    SignUpResultStatus.EMAIL_SEND_LIMIT_EXCEEDED.status -> {
                        verificationEmail = previousEmail
                        reduce {
                            state.copy(
                                emailVerificationStep = previousStep,
                                remainingSeconds = 0,
                                verificationError = "인증번호 발송 횟수를 초과했습니다. 잠시 후 다시 시도해주세요.",
                            )
                        }
                    }

                    else -> {
                        verificationEmail = previousEmail
                        reduce {
                            state.copy(
                                emailVerificationStep = previousStep,
                                remainingSeconds = 0,
                                verificationError = "인증번호를 발송하지 못했습니다. 다시 시도해주세요.",
                            )
                        }
                    }
                }
            }
            .onFailure {
                if (verificationEmail == email) {
                    verificationEmail = previousEmail
                    reduce {
                        state.copy(
                            emailVerificationStep = previousStep,
                            remainingSeconds = 0,
                            verificationError = "인증번호를 발송하지 못했습니다. 네트워크를 확인해주세요.",
                        )
                    }
                }
            }
    }

    fun onVerifyEmailCode(inputEmail: String, inputCode: String) = intent {
        val email = inputEmail.trim().lowercase()
        val code = inputCode.trim()
        if (
            verificationEmail != email
            || state.emailVerificationStep !in setOf(
                EmailVerificationStep.CODE_SENT,
                EmailVerificationStep.VERIFYING,
            )
        ) {
            reduce { state.copy(verificationError = "인증번호를 새로 받아주세요.") }
            return@intent
        }
        if (!code.matches(Regex("^\\d{6}$"))) {
            reduce { state.copy(verificationError = "6자리 인증번호를 입력해주세요.") }
            return@intent
        }

        reduce {
            state.copy(
                emailVerificationStep = EmailVerificationStep.VERIFYING,
                verificationError = null,
            )
        }

        userRepository.verifyEmailCode(email, code, EmailVerificationPurpose.REGISTRATION)
            .onSuccess { response ->
                if (verificationEmail != email) return@onSuccess
                when (response.statusInt) {
                    SignUpResultStatus.OK.status -> {
                        val token = response.verificationToken
                        if (token.isNullOrBlank()) {
                            reduce {
                                state.copy(
                                    emailVerificationStep = EmailVerificationStep.CODE_SENT,
                                    verificationError = "이메일 인증 결과를 확인하지 못했습니다. 다시 시도해주세요.",
                                )
                            }
                            return@onSuccess
                        }
                        verificationToken = token
                        countdownJob?.cancel()
                        reduce {
                            state.copy(
                                emailVerificationStep = EmailVerificationStep.VERIFIED,
                                remainingSeconds = 0,
                                verificationError = null,
                            )
                        }
                    }

                    SignUpResultStatus.EMAIL_CODE_ATTEMPTS_EXCEEDED.status -> {
                        clearVerification()
                        reduce {
                            state.copy(
                                emailVerificationStep = EmailVerificationStep.EXPIRED,
                                remainingSeconds = 0,
                                verificationError = "인증번호 입력 횟수를 초과했습니다. 새 인증번호를 받아주세요.",
                            )
                        }
                    }

                    SignUpResultStatus.EMAIL_CODE_INVALID.status -> reduce {
                        state.copy(
                            emailVerificationStep = EmailVerificationStep.CODE_SENT,
                            verificationError = "인증번호가 일치하지 않거나 만료되었습니다.",
                        )
                    }

                    else -> reduce {
                        state.copy(
                            emailVerificationStep = EmailVerificationStep.CODE_SENT,
                            verificationError = "인증번호를 확인하지 못했습니다. 다시 시도해주세요.",
                        )
                    }
                }
            }
            .onFailure {
                if (verificationEmail == email) {
                    reduce {
                        state.copy(
                            emailVerificationStep = EmailVerificationStep.CODE_SENT,
                            verificationError = "인증번호를 확인하지 못했습니다. 네트워크를 확인해주세요.",
                        )
                    }
                }
            }
    }

    fun onSignUpSubmitClick(
        inputName: String,
        inputEmail: String,
        inputBirth: String,
        inputPhone: String,
    ) = intent {
        if (state.isSigningUp || state.isSignUpComplete) return@intent

        val name = inputName.trim()
        val email = inputEmail.trim().lowercase()
        val birth = inputBirth.trim()
        val phone = inputPhone.trim()

        if (name.isBlank()) {
            postSideEffect(SignUpSideEffect.Toast("이름을 입력해주세요."))
            return@intent
        }
        if (!ValidCheckHelper.checkEmail(email)) {
            postSideEffect(SignUpSideEffect.Toast("이메일 형식을 확인해주세요."))
            return@intent
        }
        if (birth.length != 8) {
            postSideEffect(SignUpSideEffect.Toast("생년월일을 8자리(YYYYMMDD)로 입력해주세요."))
            return@intent
        }
        if (phone.length < 10) {
            postSideEffect(SignUpSideEffect.Toast("전화번호를 확인해주세요."))
            return@intent
        }

        val token = verificationToken
        if (
            state.emailVerificationStep != EmailVerificationStep.VERIFIED
            || verificationEmail != email
            || token == null
        ) {
            postSideEffect(SignUpSideEffect.Toast("이메일 인증을 완료해주세요."))
            return@intent
        }

        reduce { state.copy(isSigningUp = true) }
        userRepository.signUp(name, email, birth, phone, token)
            .onSuccess {
                clearVerification()
                reduce {
                    state.copy(
                        isSignUpComplete = true,
                        isSigningUp = false,
                        emailVerificationStep = EmailVerificationStep.IDLE,
                    )
                }
                postSideEffect(SignUpSideEffect.Toast("회원가입 정보가 저장됐어요."))
                delay(3000L)
                reduce { state.copy(isSignUpComplete = false) }
                postSideEffect(SignUpSideEffect.SignUpDoneNavigateToLogin)
            }
            .onFailure { error ->
                if (
                    error is SignUpException
                    && error.status == SignUpResultStatus.EMAIL_PROOF_INVALID
                ) {
                    clearVerification()
                    reduce {
                        state.copy(
                            isSigningUp = false,
                            emailVerificationStep = EmailVerificationStep.IDLE,
                            verificationError = "이메일 인증이 만료되었습니다. 다시 인증해주세요.",
                        )
                    }
                } else {
                    reduce { state.copy(isSigningUp = false) }
                }
                postSideEffect(
                    SignUpSideEffect.Toast(error.message ?: "회원가입에 실패했습니다.")
                )
            }
    }

    private fun startCountdown(email: String) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            for (remaining in CODE_TTL_SECONDS - 1 downTo 0) {
                delay(1000L)
                intent {
                    if (
                        verificationEmail != email
                        || state.emailVerificationStep !in setOf(
                            EmailVerificationStep.CODE_SENT,
                            EmailVerificationStep.VERIFYING,
                        )
                    ) {
                        return@intent
                    }

                    if (remaining == 0) {
                        verificationToken = null
                        verificationEmail = null
                        reduce {
                            state.copy(
                                emailVerificationStep = EmailVerificationStep.EXPIRED,
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
}

enum class EmailVerificationStep {
    IDLE,
    SENDING,
    CODE_SENT,
    VERIFYING,
    VERIFIED,
    EXPIRED,
}

@Immutable
data class SignUpState(
    val isSignUpComplete: Boolean = false,
    val isSigningUp: Boolean = false,
    val emailVerificationStep: EmailVerificationStep = EmailVerificationStep.IDLE,
    val remainingSeconds: Int = 0,
    val verificationError: String? = null,
)

sealed interface SignUpSideEffect {
    data class Toast(val message: String) : SignUpSideEffect
    data object SignUpDoneNavigateToLogin : SignUpSideEffect
}
