package com.queentech.presentation.login

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.queentech.domain.usecase.billing.BillingRepository
import com.queentech.domain.usecase.fcm.FcmRepository
import com.queentech.domain.usecase.login.UserRepository
import com.queentech.presentation.util.ValidCheckHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val fcmRepository: FcmRepository,
    private val billingRepository: BillingRepository,
) : ViewModel(), ContainerHost<LoginState, LoginSideEffect> {

    override val container: Container<LoginState, LoginSideEffect> = container(
        initialState = LoginState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                intent {
                    reduce { state.copy(isLoggingIn = false) }
                    postSideEffect(LoginSideEffect.Toast("로그인 중 오류가 발생했습니다."))
                    Log.e(TAG, "Login failed", throwable)
                }
            }
        },
        onCreate = {
            observeCurrentUser()
            loadCachedUser()
        },
    )

    companion object {
        const val TAG = "LoginViewModel"
    }

    private fun observeCurrentUser() = intent {
        userRepository.currentUser.collect { user ->
            if (user != null) {
                reduce {
                    state.copy(
                        userEmail = user.email,
                        emailInput = user.email,
                        userName = user.name,
                        userBirth = user.birth,
                        userPhone = user.phone,
                    )
                }
            } else {
                reduce {
                    state.copy(
                        userEmail = "",
                        emailInput = if (state.userEmail.isNotEmpty()) "" else state.emailInput,
                        userName = "",
                        userBirth = "",
                        userPhone = "",
                    )
                }
            }
        }
    }

    fun onSignUpClick() = intent {
        if (state.isLoggingIn) return@intent
        postSideEffect(LoginSideEffect.NavigateToSignUp)
    }

    fun onEmailChanged(value: String) = intent {
        if (state.userEmail.isNotEmpty() || state.isLoggingIn) return@intent
        reduce { state.copy(emailInput = value) }
    }

    fun onLoginClick() = intent {
        if (state.isLoggingIn) return@intent

        val email = state.emailInput.trim()

        if (email.isBlank()) {
            postSideEffect(LoginSideEffect.Toast("이메일(ID)을 입력해주세요."))
            return@intent
        }
        if (!ValidCheckHelper.checkEmail(email)) {
            postSideEffect(LoginSideEffect.Toast("이메일 형식을 확인해주세요."))
            return@intent
        }

        reduce { state.copy(isLoggingIn = true) }

        val result = userRepository.login(
            name = state.userName,
            birth = state.userBirth,
            phone = state.userPhone,
            email = email
        )

        result.onSuccess {
            reduce { state.copy(userEmail = email, isLoggingIn = false) }
            registerFcmToken(email)
            billingRepository.refreshSubscriptionStatus()
            postSideEffect(LoginSideEffect.NavigateToHome)
        }.onFailure {
            reduce { state.copy(isLoggingIn = false) }
            postSideEffect(LoginSideEffect.Toast("로그인에 실패했습니다."))
        }
    }

    private suspend fun registerFcmToken(email: String) {
        val token = fcmRepository.getFreshToken() ?: return
        val cachedToken = fcmRepository.getCachedToken()
        val cachedEmail = fcmRepository.getCachedEmail()
        if (token == cachedToken && email == cachedEmail) return

        fcmRepository.sendTokenToServer(email, token)
            .onSuccess {
                fcmRepository.saveTokenToCache(token)
                fcmRepository.saveEmailToCache(email)
            }
            .onFailure { Log.e(TAG, "FCM token submission failed") }
    }

    fun loadCachedUser() = intent {
        userRepository.loadCachedUser()
    }
}

@Immutable
data class LoginState(
    val emailInput: String = "",    // TextInput
    val userEmail: String = "",     // 유저 email
    val userName: String = "",      // 유저 이름
    val userBirth: String = "",     // 유저 생년월일
    val userPhone: String = "",     // 유저 전화번호
    val isLoggingIn: Boolean = false,
)

sealed interface LoginSideEffect {
    data class Toast(val message: String) : LoginSideEffect
    data object NavigateToSignUp : LoginSideEffect
    data object NavigateToHome : LoginSideEffect
}
