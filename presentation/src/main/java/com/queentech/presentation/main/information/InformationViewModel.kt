package com.queentech.presentation.main.information

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.queentech.domain.model.GetLottoNumber
import com.queentech.domain.usecase.GetLottoNumberUseCase
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
class InformationViewModel @Inject constructor(
    private val getLottoNumberUseCase: GetLottoNumberUseCase,
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
            getLottoNumber()
        }
    )


    private fun getLottoNumber() = intent {
        val response = getLottoNumberUseCase(drwNo = 1023).getOrThrow()
        Log.d("!!@@", "response: $response")

        reduce {
            state.copy(getLottoNumberResponse = response)
        }
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
    )
)

sealed interface InformationSideEffect {
    class Toast(val message: String) : InformationSideEffect
}