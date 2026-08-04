package com.queentech.presentation.login

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.queentech.domain.usecase.login.UserRepository
import com.queentech.presentation.util.ValidCheckHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
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
            this.exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                intent {
                    postSideEffect(SignUpSideEffect.Toast(throwable.message.toString()))
                    Log.e(TAG, "error handler: ${throwable.message}", throwable)
                }
            }
        },
    )

    companion object {
        const val TAG = "SignUpViewModel"
    }

    fun onSignUpSubmitClick(
        inputName: String,
        inputEmail: String,
        inputBirth: String,
        inputPhone: String,
    ) = intent {

        val name = inputName.trim()
        val email = inputEmail.trim()
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

        val result = userRepository.signUp(name, email, birth, phone)

        result.onSuccess {
            reduce { state.copy(isSignUpComplete = true) }
            postSideEffect(SignUpSideEffect.Toast("회원가입 정보가 저장됐어요."))
            delay(3000L)
            reduce { state.copy(isSignUpComplete = false) }
            postSideEffect(SignUpSideEffect.SignUpDoneNavigateToLogin)
        }.onFailure {
            postSideEffect(SignUpSideEffect.Toast(it.message ?: "회원가입에 실패했습니다."))
        }
    }
}

@Immutable
data class SignUpState(
    val isSignUpComplete: Boolean = false
)

sealed interface SignUpSideEffect {
    data class Toast(val message: String) : SignUpSideEffect
    data object SignUpDoneNavigateToLogin : SignUpSideEffect
}
