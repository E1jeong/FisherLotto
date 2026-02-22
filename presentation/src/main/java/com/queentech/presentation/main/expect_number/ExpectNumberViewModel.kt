package com.queentech.presentation.main.expect_number

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.queentech.domain.model.lotto.GetExpectNumber
import com.queentech.domain.usecase.login.UserRepository
import com.queentech.domain.usecase.lotto.GetExpectNumberUseCase
import com.queentech.domain.usecase.lotto.LottoIssueRepository
import com.queentech.presentation.util.DateUtils
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
    private val lottoIssueRepository: LottoIssueRepository,
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
            loadSavedNumbers()
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

    private fun loadSavedNumbers() = intent {
        val thisWeekStart = DateUtils.getCurrentWeekStartMillis()
        val lastWeekStart = DateUtils.getLastWeekStartMillis()

        // 2주 이전 데이터 정리
        val cutoff = DateUtils.getCutoffWeekStartMillis()
        lottoIssueRepository.cleanupOldData(cutoff)

        val thisWeek = lottoIssueRepository.getThisWeekNumbers(thisWeekStart)
        val lastWeek = lottoIssueRepository.getLastWeekNumbers(lastWeekStart)

        reduce {
            state.copy(
                thisWeekNumbers = thisWeek,
                lastWeekNumbers = lastWeek,
                isThisWeekIssued = thisWeek.isNotEmpty()
            )
        }
    }

    fun onExpectNumberClick() = intent {
        val thisWeekStart = DateUtils.getCurrentWeekStartMillis()

        // 이미 이번주에 발급했으면 토스트 + 리턴
        if (lottoIssueRepository.isThisWeekIssued(thisWeekStart)) {
            postSideEffect(ExpectNumberSideEffect.Toast("이번주에 이미 발급했습니다"))
            return@intent
        }

        val result = getExpectNumberUseCase(state.userEmail, state.userPhone).getOrDefault(
            GetExpectNumber(count = 0, lotto = emptyList())
        )

        if (result.count != 0) {
            // Room에 저장
            lottoIssueRepository.saveIssue(
                numbers = result.lotto,
                weekStartMillis = thisWeekStart
            )

            // UI 갱신
            val lastWeekStart = DateUtils.getLastWeekStartMillis()
            val lastWeek = lottoIssueRepository.getLastWeekNumbers(lastWeekStart)

            reduce {
                state.copy(
                    count = result.count,
                    lastWeekNumbers = lastWeek,
                    thisWeekNumbers = result.lotto,
                    isThisWeekIssued = true
                )
            }
        }
    }
}

@Immutable
data class ExpectNumberState(
    val count: Int = 0,
    val lastWeekNumbers: List<String> = emptyList(),  // 저번주 번호 (10개)
    val thisWeekNumbers: List<String> = emptyList(),  // 이번주 번호 (10개)
    val isThisWeekIssued: Boolean = false,            // 이번주 발급 여부
    val userEmail: String = "",
    val userName: String = "",
    val userBirth: String = "",
    val userPhone: String = "",
)

sealed interface ExpectNumberSideEffect {
    data class Toast(val message: String) : ExpectNumberSideEffect
}
