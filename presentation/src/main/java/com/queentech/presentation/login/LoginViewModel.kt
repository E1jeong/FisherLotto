package com.queentech.presentation.login

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.queentech.presentation.login.SignUpViewModel.Companion.KEY_SIGNUP_EMAIL
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
    private val prefs: SharedPreferences,
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
            loadUserEmail()
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

        // ✅ 로그인 성공 처리(최종 email 확정)
        reduce { state.copy(userEmail = email) }
        postSideEffect(LoginSideEffect.NavigateToInformation)
    }

    fun loadUserEmail() = intent {
        val email = prefs.getString(KEY_SIGNUP_EMAIL, "")
        reduce { state.copy(userEmail = email!!) }
    }
}

@Immutable
data class LoginState(
    val emailInput: String = "", //TextInput
    val userEmail: String = "", //최종 email
)

sealed interface LoginSideEffect {
    data class Toast(val message: String) : LoginSideEffect
    data object NavigateToSignUp : LoginSideEffect
    data object NavigateToInformation : LoginSideEffect
}
