package com.queentech.presentation.login

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.google.firebase.messaging.FirebaseMessaging
import com.queentech.domain.usecase.fcm.FcmRepository
import com.queentech.domain.usecase.login.UserRepository
import com.queentech.presentation.util.ValidCheckHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.suspendCancellableCoroutine
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val fcmRepository: FcmRepository,
) : ViewModel(), ContainerHost<LoginState, LoginSideEffect> {

    override val container: Container<LoginState, LoginSideEffect> = container(
        initialState = LoginState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                intent {
                    postSideEffect(LoginSideEffect.Toast(throwable.message.toString()))
                    Log.e(TAG, "error handler: ${throwable.message}", throwable)
                }
            }
        },
        onCreate = {
            loadCachedUser()
        },
    )

    companion object {
        const val TAG = "LoginViewModel"
    }

    fun onSignUpClick() = intent {
        postSideEffect(LoginSideEffect.NavigateToSignUp)
    }

    fun onEmailChanged(value: String) = intent {
        reduce { state.copy(emailInput = value) }
    }

    fun onLoginClick() = intent {
        val email = state.userEmail.ifEmpty { state.emailInput.trim() }

        if (email.isBlank()) {
            postSideEffect(LoginSideEffect.Toast("이메일(ID)을 입력해주세요."))
            return@intent
        }
        if (!ValidCheckHelper.checkEmail(email)) {
            postSideEffect(LoginSideEffect.Toast("이메일 형식을 확인해주세요."))
            return@intent
        }

        val result = userRepository.login(
            name = state.userName,
            birth = state.userBirth,
            phone = state.userPhone,
            email = email
        )

        result.onSuccess {
            reduce { state.copy(userEmail = email) }
            postSideEffect(LoginSideEffect.NavigateToInformation)
            registerFcmToken(email)
        }.onFailure {
            postSideEffect(LoginSideEffect.Toast(it.message ?: "로그인에 실패했습니다."))
        }
    }

    private suspend fun getFirebaseToken(): String? {
        return suspendCancellableCoroutine { continuation ->
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token -> continuation.resume(token) }
                .addOnFailureListener { continuation.resume(null) }
        }
    }

    private fun registerFcmToken(email: String) = intent {
        val token = getFirebaseToken() ?: run {
            Log.w(TAG, "FCM 토큰을 가져오지 못했습니다.")
            return@intent
        }
        val cachedToken = fcmRepository.getCachedToken()
        if (token == cachedToken) {
            Log.d(TAG, "FCM 토큰이 동일합니다. 서버 전송을 생략합니다.")
            return@intent
        }
        fcmRepository.saveTokenToCache(token)
        fcmRepository.sendTokenToServer(email, token)
            .onFailure { Log.e(TAG, "FCM 토큰 서버 전송 실패", it) }
    }

    fun loadCachedUser() = intent {
        userRepository.loadCachedUser()
        val cachedUser = userRepository.currentUser.value
        if (cachedUser != null) {
            reduce {
                state.copy(
                    userEmail = cachedUser.email,
                    userName = cachedUser.name,
                    userBirth = cachedUser.birth,
                    userPhone = cachedUser.phone
                )
            }
        }
    }
}

@Immutable
data class LoginState(
    val emailInput: String = "",    // TextInput
    val userEmail: String = "",     // 유저 email
    val userName: String = "",      // 유저 이름
    val userBirth: String = "",     // 유저 생년월일
    val userPhone: String = "",     // 유저 전화번호
)

sealed interface LoginSideEffect {
    data class Toast(val message: String) : LoginSideEffect
    data object NavigateToSignUp : LoginSideEffect
    data object NavigateToInformation : LoginSideEffect
}
