package com.queentech.presentation.login

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@OptIn(OrbitExperimental::class)
@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) : ViewModel(), ContainerHost<LoginState, LoginSideEffect> {

    override val container: Container<LoginState, LoginSideEffect> = container(
        initialState = LoginState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                intent {
                    postSideEffect(LoginSideEffect.Toast(throwable.message.toString()))
                    Log.e(TAG, "error handler: ${throwable.message}")
                }
            }
        },
        onCreate = {},
    )

    // 회원가입 클릭 시 호출할 함수
    fun onSignUpClick() = intent {
        postSideEffect(LoginSideEffect.NavigateToSignUp)
    }

    // 정보 화면으로 이동하고 싶다면 이런 것도 가능
    fun onInformationClick() = intent {
        postSideEffect(LoginSideEffect.NavigateToInformation)
    }

    @SuppressLint("HardwareIds")
    private fun getAndroidId() = blockingIntent {
        val rawId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val androidId = rawId?.uppercase()?.chunked(2)?.joinToString(":") ?: ""

        reduce { state.copy(androidId = androidId) }
    }

    companion object {
        const val TAG = "LoginViewModel"
    }
}

@Immutable
data class LoginState(
    val id: String = "",
    val androidId: String = "",
)

sealed interface LoginSideEffect {
    data class Toast(val message: String) : LoginSideEffect
    data object NavigateToSignUp : LoginSideEffect
    data object NavigateToInformation : LoginSideEffect
}