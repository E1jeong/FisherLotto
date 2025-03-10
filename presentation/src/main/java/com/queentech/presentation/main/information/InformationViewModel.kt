package com.queentech.presentation.main.information

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.queentech.domain.model.GetLottoNumber
import com.queentech.domain.usecase.GetLatestDrawNumberUseCase
import com.queentech.domain.usecase.GetLottoNumberUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@OptIn(OrbitExperimental::class)
@HiltViewModel
class InformationViewModel @Inject constructor(
    private val getLottoNumberUseCase: GetLottoNumberUseCase,
    private val getLatestDrawNumberUseCase: GetLatestDrawNumberUseCase,
) : ViewModel(), ContainerHost<InformationState, InformationSideEffect> {

    override val container: Container<InformationState, InformationSideEffect> = container(
        initialState = InformationState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                intent {
                    postSideEffect(InformationSideEffect.Toast(throwable.message ?: "Unknown Error"))
                    Log.e("!!@@", "error handler: ${throwable.message}")
                }
            }
        },
        onCreate = {
            getLatestDrawNumber()
            getLottoNumber()
        }
    )

    private fun getLottoNumber() = blockingIntent {
        val response = getLottoNumberUseCase(drwNo = state.latestDrawNumber).getOrThrow()
        Log.d("!!@@", "getLottoNumber: $response")

        reduce { state.copy(getLottoNumberResponse = response) }
    }

    private fun getLatestDrawNumber() = blockingIntent {
        val response = getLatestDrawNumberUseCase().getOrThrow()
        Log.d("!!@@", "getLatestDrawNumber: $response")

        reduce { state.copy(latestDrawNumber = response) }
    }
}


@Immutable
data class InformationState(
    val getLottoNumberResponse: GetLottoNumber = GetLottoNumber(
        drwNo = 0,
        drwNoDate = "",
        drwtNo1 = 0,
        drwtNo2 = 0,
        drwtNo3 = 0,
        drwtNo4 = 0,
        drwtNo5 = 0,
        drwtNo6 = 0,
        bnusNo = 0,
    ),
    val latestDrawNumber: Int = 0,
)

sealed interface InformationSideEffect {
    class Toast(val message: String) : InformationSideEffect
}