package com.queentech.presentation.main.expect_number

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.queentech.domain.model.lotto.GetExpectNumber
import com.queentech.domain.usecase.billing.BillingRepository
import com.queentech.domain.usecase.login.UserRepository
import com.queentech.domain.usecase.lotto.GetExpectNumberUseCase
import com.queentech.domain.usecase.lotto.GetLottoNumberUseCase
import com.queentech.domain.usecase.lotto.LottoIssueRepository
import kotlinx.coroutines.flow.firstOrNull
import com.queentech.presentation.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.sync.Mutex
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
    private val getLottoNumberUseCase: GetLottoNumberUseCase,
    private val lottoIssueRepository: LottoIssueRepository,
    private val billingRepository: BillingRepository,
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
            checkDeadline()
            loadWinningStatus()
        },
    )

    private val issueMutex = Mutex()

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
                isThisWeekIssued = thisWeek.isNotEmpty(),
                thisWeekRange = DateUtils.getWeekRangeString(thisWeekStart),
                lastWeekRange = DateUtils.getWeekRangeString(lastWeekStart)
            )
        }
    }

    private fun checkDeadline() = intent {
        reduce { state.copy(isDeadlineClosed = DateUtils.isSaturdayDeadline()) }
    }

    fun onExpectNumberClick() = intent {
        if (DateUtils.isSaturdayDeadline()) {
            reduce { state.copy(isDeadlineClosed = true, showDeadlineDialog = true) }
            return@intent
        }

        val thisWeekStart = DateUtils.getCurrentWeekStartMillis()
        if (lottoIssueRepository.isThisWeekIssued(thisWeekStart)) {
            postSideEffect(ExpectNumberSideEffect.Toast("이번주에 이미 발급했습니다"))
            return@intent
        }

        val isSubscribed = billingRepository.subscriptionStatus.firstOrNull()?.isActive == true
        if (isSubscribed) {
            onAdWatchedSuccessfully()
        } else {
            postSideEffect(ExpectNumberSideEffect.ShowRewardAd)
        }
    }

    fun onAdWatchedSuccessfully() = intent {
        if (!issueMutex.tryLock()) return@intent

        try {
            val thisWeekStart = DateUtils.getCurrentWeekStartMillis()

            // 이중 발급 방어: 광고 콜백 중복 호출 등 예외 상황 대비
            if (lottoIssueRepository.isThisWeekIssued(thisWeekStart)) {
                postSideEffect(ExpectNumberSideEffect.Toast("이번주에 이미 발급했습니다"))
                return@intent
            }

            val result = getExpectNumberUseCase(state.userEmail, state.userPhone).getOrDefault(
                GetExpectNumber(count = 0, lotto = emptyList())
            )

            if (result.count != 0) {
                lottoIssueRepository.saveIssue(
                    numbers = result.lotto,
                    weekStartMillis = thisWeekStart
                )

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
            } else {
                postSideEffect(ExpectNumberSideEffect.Toast("번호 발급에 실패했습니다."))
            }
        } finally {
            if (issueMutex.isLocked) issueMutex.unlock()
        }
    }

    private fun loadWinningStatus() = intent {
        billingRepository.subscriptionStatus.collect { status ->
            val isSubscribed = status.isActive
            reduce { state.copy(isSubscribed = isSubscribed) }

            if (isSubscribed && state.winningNumbers.isEmpty()) {
                getLottoNumberUseCase(round = 0)
                    .onSuccess { result ->
                        val winningNumbers = listOf(
                            result.num1Int, result.num2Int, result.num3Int,
                            result.num4Int, result.num5Int, result.num6Int,
                            result.bonusInt
                        )
                        reduce { state.copy(winningNumbers = winningNumbers) }
                    }
            }
        }
    }

    fun dismissDeadlineDialog() = intent {
        reduce { state.copy(showDeadlineDialog = false) }
    }
}

@Immutable
data class ExpectNumberState(
    val count: Int = 0,
    val lastWeekNumbers: List<String> = emptyList(),  // 저번주 번호 (10개)
    val thisWeekNumbers: List<String> = emptyList(),  // 이번주 번호 (10개)
    val isThisWeekIssued: Boolean = false,             // 이번주 발급 여부
    val thisWeekRange: String = "",                    // 이번주 범위 (02.22 ~ 02.28)
    val lastWeekRange: String = "",                    // 저번주 범위 (02.15 ~ 02.21)
    val userEmail: String = "",
    val userName: String = "",
    val userBirth: String = "",
    val userPhone: String = "",
    val isDeadlineClosed: Boolean = false,
    val showDeadlineDialog: Boolean = false,
    val winningNumbers: List<Int> = emptyList(),
    val isSubscribed: Boolean = false,
)

sealed interface ExpectNumberSideEffect {
    data class Toast(val message: String) : ExpectNumberSideEffect
    object ShowRewardAd : ExpectNumberSideEffect
}
