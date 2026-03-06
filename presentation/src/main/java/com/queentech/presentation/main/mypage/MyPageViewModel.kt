package com.queentech.presentation.main.mypage

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.queentech.domain.model.login.User
import com.queentech.domain.usecase.login.UserRepository
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
class MyPageViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel(), ContainerHost<MyPageState, MyPageSideEffect> {

    override val container: Container<MyPageState, MyPageSideEffect> = container(
        initialState = MyPageState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                intent {
                    reduce { state.copy(isLoading = false) }
                    postSideEffect(MyPageSideEffect.Toast(throwable.message ?: "오류가 발생했습니다."))
                    Log.e(TAG, "error handler: ${throwable.message}", throwable)
                }
            }
        },
        onCreate = {
            loadUser()
        },
    )

    companion object {
        const val TAG = "MyPageViewModel"
    }

    // ── User ──

    private fun loadUser() = intent {
        userRepository.loadCachedUser()
        val user = userRepository.currentUser.value
        if (user != null) {
            reduce { state.copy(user = user) }
        }
    }

    fun onLogoutClick() = intent {
        userRepository.logout()
        reduce { state.copy(user = null) }
        postSideEffect(MyPageSideEffect.NavigateToLogin)
    }
}

@Immutable
data class MyPageState(
    val user: User? = null,
    val isLoading: Boolean = false,
)

sealed interface MyPageSideEffect {
    data class Toast(val message: String) : MyPageSideEffect
    data object NavigateToLogin : MyPageSideEffect
}
