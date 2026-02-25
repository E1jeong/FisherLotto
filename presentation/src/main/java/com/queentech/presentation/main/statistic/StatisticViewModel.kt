package com.queentech.presentation.main.statistic

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.queentech.domain.model.lotto.GetLottoNumber
import com.queentech.domain.usecase.lotto.GetLottoNumberUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class StatisticViewModel @Inject constructor(
    private val getLottoNumberUseCase: GetLottoNumberUseCase,
) : ViewModel(), ContainerHost<StatisticState, StatisticSideEffect> {

    override val container: Container<StatisticState, StatisticSideEffect> = container(
        initialState = StatisticState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                intent {
                    // 에러 발생 시 로딩/페이징 상태 해제
                    reduce { state.copy(isLoading = false, isPaginating = false) }
                    postSideEffect(StatisticSideEffect.Toast(throwable.message ?: "Unknown Error"))
                    Log.e(TAG, "error handler: ${throwable.message}", throwable)
                }
            }
        },
        onCreate = {
            loadInitialData()
        }
    )

    companion object {
        const val TAG = "StatisticViewModel"
        const val PAGE_SIZE = 10 // 한 번에 불러올 회차 개수
    }

    private fun loadInitialData() = intent {
        reduce { state.copy(isLoading = true, lottoList = emptyList()) }

        // 1. 최신 회차(0) 정보 가져오기
        val latestLotto = getLottoNumberUseCase(round = 0).getOrNull()
        if (latestLotto == null) {
            reduce { state.copy(isLoading = false) }
            postSideEffect(StatisticSideEffect.Toast("최신 회차 정보를 불러오지 못했습니다."))
            return@intent
        }

        // GetLottoNumber 모델의 확장 프로퍼티 사용
        val latestRound = latestLotto.roundInt
        val startRound = maxOf(1, latestRound - PAGE_SIZE + 1)

        // 2. 최신 회차부터 10개 병렬로 불러오기
        val fetchedList = fetchLottoRange(startRound, latestRound)

        reduce {
            state.copy(
                lottoList = fetchedList,
                nextRoundToFetch = startRound - 1,
                isLoading = false
            )
        }
    }

    fun loadMoreData() = intent {
        val nextRound = state.nextRoundToFetch

        // 이미 로딩중이거나, 더 이상 불러올 데이터가 없으면 무시
        if (state.isPaginating || state.isLoading || nextRound == null || nextRound < 1) {
            return@intent
        }

        reduce { state.copy(isPaginating = true) }

        delay(1500L)

        val endRound = nextRound
        val startRound = maxOf(1, endRound - PAGE_SIZE + 1)

        val fetchedList = fetchLottoRange(startRound, endRound)

        reduce {
            state.copy(
                lottoList = state.lottoList + fetchedList,
                nextRoundToFetch = startRound - 1,
                isPaginating = false
            )
        }
    }

    fun refresh() = intent {
        loadInitialData()
    }

    private suspend fun fetchLottoRange(start: Int, end: Int): List<GetLottoNumber> = coroutineScope {
        (start..end).map { round ->
            async {
                getLottoNumberUseCase(round).getOrNull()
            }
        }
            .awaitAll()
            .filterNotNull()
            .sortedByDescending { it.roundInt } // GetLottoNumber 모델 자체를 정렬
    }
}

@Immutable
data class StatisticState(
    val lottoList: List<GetLottoNumber> = emptyList(), // 데이터 타입 변경
    val isLoading: Boolean = false,
    val isPaginating: Boolean = false,
    val nextRoundToFetch: Int? = null,
)

sealed interface StatisticSideEffect {
    data class Toast(val message: String) : StatisticSideEffect
}
