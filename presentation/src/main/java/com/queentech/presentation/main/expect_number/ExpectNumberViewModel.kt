package com.queentech.presentation.main.expect_number

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.queentech.domain.model.lotto.GetExpectNumber
import com.queentech.domain.usecase.login.UserRepository
import com.queentech.domain.usecase.lotto.GetExpectNumberUseCase
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
class ExpectNumberViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val getExpectNumberUseCase: GetExpectNumberUseCase,
) : ViewModel(), ContainerHost<ExpectNumberState, ExpectNumberSideEffect> {
    override val container: Container<ExpectNumberState, ExpectNumberSideEffect> = container(
        initialState = ExpectNumberState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                intent {
                    postSideEffect(ExpectNumberSideEffect.Toast(throwable.message.toString()))
                    Log.e(TAG, "error handler: ${throwable.message}", throwable)
                }
            }
        },
        onCreate = {
            loadCachedUser()
        },
    )

    companion object {
        const val TAG = "ExpectNumberViewModel"
    }

    private fun loadCachedUser() = intent {
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

    fun onExpectNumberClick() = intent {
        val result = getExpectNumberUseCase(state.userEmail, state.userPhone).getOrDefault(
            GetExpectNumber(count = 0, lotto = emptyList())
        )

        if (result.count != 0) {
            reduce {
                state.copy(
                    count = result.count,
                    expectNumber = result.lotto
                )
            }
        }
    }
}

@Immutable
data class ExpectNumberState(
    val count: Int = 0,
    val expectNumber: List<String> = emptyList(),
    val userEmail: String = "",     // 유저 email
    val userName: String = "",      // 유저 이름
    val userBirth: String = "",     // 유저 생년월일
    val userPhone: String = "",     // 유저 전화번호
)

sealed interface ExpectNumberSideEffect {
    data class Toast(val message: String) : ExpectNumberSideEffect
}
