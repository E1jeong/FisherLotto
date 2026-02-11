package com.queentech.presentation.login

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
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
            loadRememberedId()
        },
    )
    companion object {
        const val TAG = "LoginViewModel"
        const val KEY_REMEMBER_ID = "remember_id"
        const val KEY_REMEMBER_EMAIL = "remember_email"

        const val KEY_SIGNUP_NAME = "signup_name"
        const val KEY_SIGNUP_EMAIL = "signup_email"
        const val KEY_SIGNUP_BIRTH = "signup_birth"
        const val KEY_SIGNUP_PHONE = "signup_phone"
    }

    fun onSignUpClick() = intent {
        postSideEffect(LoginSideEffect.NavigateToSignUp)
    }

    fun onEmailChanged(value: String) = intent {
        reduce { state.copy(emailInput = value) }

        // ✅ "ID 기억"이 켜져있다면 입력이 바뀔 때마다 저장(정책)
        // 원하면 이 부분을 제거하고 로그인 성공 시점에만 저장해도 됨.
        if (state.rememberId) {
            saveRememberedId(email = value.trim(), remember = true)
        }
    }

    fun onRememberIdChanged(value: Boolean) = intent {
        reduce { state.copy(rememberId = value) }

        if (value) {
            // 켜는 순간: 현재 입력값을 저장
            saveRememberedId(email = state.emailInput.trim(), remember = true)
        } else {
            // 끄는 순간: 저장된 값 삭제
            saveRememberedId(email = "", remember = false)
        }
    }

    fun onEmailLoginClick() = intent {
        val email = state.emailInput.trim()

        if (email.isBlank()) {
            postSideEffect(LoginSideEffect.Toast("이메일(ID)을 입력해주세요."))
            return@intent
        }
        if (!isValidEmailLike(email)) {
            postSideEffect(LoginSideEffect.Toast("이메일 형식을 확인해주세요."))
            return@intent
        }

        // ✅ rememberId가 true면 로그인 시점에 최종 저장 보장
        if (state.rememberId) {
            saveRememberedId(email = email, remember = true)
        }

        // ✅ 로그인 성공 처리(최종 email 확정)
        reduce { state.copy(userEmail = email) }
        postSideEffect(LoginSideEffect.NavigateToInformation)
    }

    private fun loadRememberedId() = intent {
        val remember = prefs.getBoolean(KEY_REMEMBER_ID, false)
        val savedEmail = prefs.getString(KEY_REMEMBER_EMAIL, "") ?: ""

        reduce {
            state.copy(
                rememberId = remember,
                emailInput = if (remember) savedEmail else state.emailInput
            )
        }
    }

    private fun saveRememberedId(email: String, remember: Boolean) {
        prefs.edit()
            .putBoolean(KEY_REMEMBER_ID, remember)
            .putString(KEY_REMEMBER_EMAIL, if (remember) email else "")
            .apply()
    }

    private fun isValidEmailLike(email: String): Boolean {
        // 너무 빡빡하게 하면 실제 이메일인데도 걸릴 수 있어서 최소 체크만
        val at = email.indexOf('@')
        val dot = email.lastIndexOf('.')
        return at > 0 && dot > at + 1 && dot < email.length - 1
    }

    /*****************************************************
     *  Signup Screen
     *****************************************************/
    fun onSignUpNameChanged(v: String) = intent {
        reduce { state.copy(signUp = state.signUp.copy(name = v)) }
    }

    fun onSignUpEmailChanged(v: String) = intent {
        reduce { state.copy(signUp = state.signUp.copy(email = v)) }
    }

    fun onSignUpBirthChanged(v: String) = intent {
        reduce { state.copy(signUp = state.signUp.copy(birth = v)) }
    }

    fun onSignUpPhoneChanged(v: String) = intent {
        reduce { state.copy(signUp = state.signUp.copy(phone = v)) }
    }

    fun onBackToLoginClick() = intent {
        postSideEffect(LoginSideEffect.SignUpDoneNavigateToLogin) // 그냥 뒤로 가기 용도면 이름 바꿔도 됨
    }

    fun onSignUpSubmitClick() = intent {
        val s = state.signUp

        val name = s.name.trim()
        val email = s.email.trim()
        val birth = s.birth.trim()
        val phone = s.phone.trim()

        if (name.isBlank()) {
            postSideEffect(LoginSideEffect.Toast("이름을 입력해주세요."))
            return@intent
        }
        if (!isValidEmailLike(email)) {
            postSideEffect(LoginSideEffect.Toast("이메일 형식을 확인해주세요."))
            return@intent
        }
        if (birth.length != 8) {
            postSideEffect(LoginSideEffect.Toast("생년월일을 8자리(YYYYMMDD)로 입력해주세요."))
            return@intent
        }
        if (phone.length < 10) {
            postSideEffect(LoginSideEffect.Toast("전화번호를 확인해주세요."))
            return@intent
        }

        // ✅ 정책: 회원가입 입력값 prefs 저장
        saveSignUpInfo(name, email, birth, phone)

        // ✅ 저장했으면 UX 상 입력 초기화도 많이 함(선택)
        reduce { state.copy(signUp = SignUpState()) }

        postSideEffect(LoginSideEffect.Toast("회원가입 정보가 저장됐어요."))
        postSideEffect(LoginSideEffect.SignUpDoneNavigateToLogin)
    }

    private fun saveSignUpInfo(name: String, email: String, birth: String, phone: String) {
        prefs.edit()
            .putString(KEY_SIGNUP_NAME, name)
            .putString(KEY_SIGNUP_EMAIL, email)
            .putString(KEY_SIGNUP_BIRTH, birth)
            .putString(KEY_SIGNUP_PHONE, phone)
            .apply()
    }
}

@Immutable
data class LoginState(
    val emailInput: String = "", //TextInput
    val rememberId: Boolean = false,
    val userEmail: String? = null, //최종 email
    val signUp: SignUpState = SignUpState()
)

@Immutable
data class SignUpState(
    val name: String = "",
    val email: String = "",
    val birth: String = "",
    val phone: String = ""
)

sealed interface LoginSideEffect {
    data class Toast(val message: String) : LoginSideEffect
    data object NavigateToSignUp : LoginSideEffect
    data object NavigateToInformation : LoginSideEffect
    data object SignUpDoneNavigateToLogin : LoginSideEffect
}
