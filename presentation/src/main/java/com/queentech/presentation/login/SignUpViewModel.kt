package com.queentech.presentation.login

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
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
class SignUpViewModel @Inject constructor(
    private val prefs: SharedPreferences,
) : ViewModel(), ContainerHost<SignUpState, SignUpSideEffect> {

    override val container: Container<SignUpState, SignUpSideEffect> = container(
        initialState = SignUpState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                intent {
                    postSideEffect(SignUpSideEffect.Toast(throwable.message.toString()))
                    Log.e(TAG, "error handler: ${throwable.message}", throwable)
                }
            }
        },
        onCreate = {

        },
    )

    companion object {
        const val TAG = "SignUpViewModel"

        const val KEY_SIGNUP_NAME = "signup_name"
        const val KEY_SIGNUP_EMAIL = "signup_email"
        const val KEY_SIGNUP_BIRTH = "signup_birth"
        const val KEY_SIGNUP_PHONE = "signup_phone"
    }

    /*****************************************************
     *  Signup Screen
     *****************************************************/
    fun onSignUpNameChanged(v: String) = intent {
        reduce { state.copy(name = v) }
    }

    fun onSignUpEmailChanged(v: String) = intent {
        reduce { state.copy(email = v) }
    }

    fun onSignUpBirthChanged(v: String) = intent {
        reduce { state.copy(birth = v) }
    }

    fun onSignUpPhoneChanged(v: String) = intent {
        reduce { state.copy(phone = v) }
    }

    fun onSignUpSubmitClick() = intent {

        val name = state.name.trim()
        val email = state.email.trim()
        val birth = state.birth.trim()
        val phone = state.phone.trim()

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

        // ✅ 정책: 회원가입 입력값 prefs 저장
        saveSignUpInfo(name, email, birth, phone)

        reduce { state.copy(name = name, email = email, birth = birth, phone = phone) }

        postSideEffect(SignUpSideEffect.Toast("회원가입 정보가 저장됐어요."))
        postSideEffect(SignUpSideEffect.SignUpDoneNavigateToLogin)
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
data class SignUpState(
    val name: String = "",
    val email: String = "",
    val birth: String = "",
    val phone: String = ""
)

sealed interface SignUpSideEffect {
    data class Toast(val message: String) : SignUpSideEffect
    data object SignUpDoneNavigateToLogin : SignUpSideEffect
}
