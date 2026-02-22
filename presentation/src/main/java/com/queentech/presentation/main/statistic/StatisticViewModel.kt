package com.queentech.presentation.main.statistic

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
class StatisticViewModel @Inject constructor(
    // TODO: 서버 API UseCase 추가 예정 (서버 개발자 상의 후)
    // private val getStatisticUseCase: GetStatisticUseCase,
) : ViewModel(), ContainerHost<StatisticState, StatisticSideEffect> {

    override val container: Container<StatisticState, StatisticSideEffect> = container(
        initialState = StatisticState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                intent {
                    postSideEffect(StatisticSideEffect.Toast(throwable.message ?: "Unknown Error"))
                    Log.e(TAG, "error handler: ${throwable.message}", throwable)
                }
            }
        },
        onCreate = {
            loadStatistics()
        }
    )

    private fun loadStatistics() = intent {
        reduce { state.copy(isLoading = true) }

        // TODO: 서버 API 연동 후 실제 데이터로 교체
        // val response = getStatisticUseCase().getOrThrow()
        // reduce { state.copy(winnerDataList = response, isLoading = false) }

        // 임시 더미 데이터 (서버 연동 전까지 UI 확인용)
        val dummyData = listOf(
            WinnerData(1161, 1, 3, 50, 233, 566, 788),
            WinnerData(1160, 2, 10, 32, 233, 566, 788),
            WinnerData(1159, 1, 7, 28, 233, 566, 788),
            WinnerData(1158, 2, 6, 75, 233, 566, 788),
            WinnerData(1157, 3, 12, 61, 210, 490, 720),
        )
        reduce {
            state.copy(
                winnerDataList = dummyData,
                isLoading = false
            )
        }
    }

    fun refresh() = intent {
        reduce { state.copy(isLoading = true) }
        // TODO: 서버 API 호출
        loadStatistics()
    }

    companion object {
        const val TAG = "StatisticViewModel"
    }
}

@Immutable
data class StatisticState(
    val winnerDataList: List<WinnerData> = emptyList(),
    val isLoading: Boolean = false,
)

sealed interface StatisticSideEffect {
    data class Toast(val message: String) : StatisticSideEffect
}
