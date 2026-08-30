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
import kotlinx.coroutines.sync.withLock
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.SimpleSyntax
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
                    postSideEffect(ExpectNumberSideEffect.Toast("추천 번호를 불러오지 못했습니다."))
                    Log.e(TAG, "Expected-number loading failed")
                }
            }
        },
        onCreate = {
            loadCachedUser()
            loadSavedNumbers()
            checkIssueWindow()
            observeExpectedNumberReset()
            loadWinningStatus()
        },
    )

    private val issueMutex = Mutex()

    // Room 조회와 번호 상태 반영의 원자성 보장용
    private val numbersMutex = Mutex()

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
        // 2주 이전 데이터 정리
        val cutoff = DateUtils.getCutoffWeekStartMillis()
        lottoIssueRepository.cleanupOldData(cutoff)

        refreshNumbers()
    }

    // Room 조회와 상태 반영을 한 단위로 묶는다.
    private suspend fun SimpleSyntax<ExpectNumberState, ExpectNumberSideEffect>.refreshNumbers() {
        numbersMutex.withLock {
            val thisWeekStart = DateUtils.getCurrentWeekStartMillis()
            val lastWeekStart = DateUtils.getLastWeekStartMillis()

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
    }

    private fun checkIssueWindow() = intent {
        reduce { state.copy(isIssueWindowClosed = DateUtils.isIssueWindowClosed()) }
    }

    private var pendingExpectNumber: GetExpectNumber? = null

    fun onExpectNumberClick() = intent {
        val isIssueWindowClosed = DateUtils.isIssueWindowClosed()
        if (isIssueWindowClosed) {
            reduce { state.copy(isIssueWindowClosed = true, showIssueWindowClosedDialog = true) }
            return@intent
        }
        reduce { state.copy(isIssueWindowClosed = false) }

        val thisWeekStart = DateUtils.getCurrentWeekStartMillis()
        if (lottoIssueRepository.isThisWeekIssued(thisWeekStart)) {
            postSideEffect(ExpectNumberSideEffect.Toast("이번주에 이미 발급했습니다"))
            return@intent
        }

        if (state.isLoading) return@intent
        reduce { state.copy(isLoading = true) }

        val result = getExpectNumberUseCase(state.userEmail, state.userPhone)
            .getOrElse {
                reduce { state.copy(isLoading = false) }
                postSideEffect(ExpectNumberSideEffect.Toast("추천 번호를 불러오지 못했습니다. 네트워크를 확인해주세요."))
                return@intent
            }

        if (result.count <= 0 || result.lotto.isEmpty()) {
            reduce { state.copy(isLoading = false) }
            postSideEffect(ExpectNumberSideEffect.Toast("이번 회차 추천 번호가 아직 준비되지 않았습니다."))
            return@intent
        }

        val isSubscribed = billingRepository.subscriptionStatus.firstOrNull()?.isActive == true
        if (isSubscribed) {
            applyIssuedNumbers(result, thisWeekStart)
        } else {
            pendingExpectNumber = result
            reduce { state.copy(isLoading = false) }
            postSideEffect(ExpectNumberSideEffect.ShowRewardAd)
        }
    }

    fun onAdWatchedSuccessfully() = intent {
        val pending = pendingExpectNumber
        if (pending == null || pending.count <= 0 || pending.lotto.isEmpty()) {
            postSideEffect(ExpectNumberSideEffect.Toast("발급할 번호 정보를 찾을 수 없습니다. 다시 시도해주세요."))
            return@intent
        }

        if (!issueMutex.tryLock()) return@intent

        try {
            val thisWeekStart = DateUtils.getCurrentWeekStartMillis()

            // 이중 발급 방어: 광고 콜백 중복 호출 등 예외 상황 대비
            if (lottoIssueRepository.isThisWeekIssued(thisWeekStart)) {
                postSideEffect(ExpectNumberSideEffect.Toast("이번주에 이미 발급했습니다"))
                pendingExpectNumber = null
                return@intent
            }

            applyIssuedNumbers(pending, thisWeekStart)
            pendingExpectNumber = null
        } finally {
            if (issueMutex.isLocked) issueMutex.unlock()
        }
    }

    private suspend fun SimpleSyntax<ExpectNumberState, ExpectNumberSideEffect>.applyIssuedNumbers(
        result: GetExpectNumber,
        thisWeekStart: Long,
    ) {
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
                isThisWeekIssued = true,
                isLoading = false,
            )
        }
    }

    private fun observeExpectedNumberReset() = intent {
        billingRepository.expectedNumberResetEvents.collect {
            refreshNumbers()
            postSideEffect(ExpectNumberSideEffect.Toast("구독 혜택이 적용되었습니다. 다시 발급해 주세요"))
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

    fun dismissIssueWindowClosedDialog() = intent {
        reduce { state.copy(showIssueWindowClosedDialog = false) }
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
    val isIssueWindowClosed: Boolean = false,
    val showIssueWindowClosedDialog: Boolean = false,
    val winningNumbers: List<Int> = emptyList(),
    val isSubscribed: Boolean = false,
    val isLoading: Boolean = false,
)

sealed interface ExpectNumberSideEffect {
    data class Toast(val message: String) : ExpectNumberSideEffect
    object ShowRewardAd : ExpectNumberSideEffect
}
