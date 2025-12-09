package com.queentech.presentation.main.camera

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
class CameraViewModel @Inject constructor(
    private val getLottoNumberUseCase: GetLottoNumberUseCase,
) : ViewModel(), ContainerHost<CameraState, CameraSideEffect> {

    override val container: Container<CameraState, CameraSideEffect> = container(
        initialState = CameraState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                intent {
                    postSideEffect(CameraSideEffect.Toast(throwable.message ?: "Unknown Error"))
                }
            }
        }
    )

    fun onQrCodeScanned(rawValue: String) = intent {
        Log.d("CameraViewModel", "raw QR: $rawValue")
        val result = LottoQrResult.parse(rawValue) ?: return@intent
        Log.d("CameraViewModel", "Parsed drawNo=${result.drawNo}, games=${result.games}")
        val winning = getLottoNumberUseCase(result.drawNo).getOrNull()
        reduce { state.copy(result = result, winningNumbers = winning) }
    }
}

@Immutable
data class CameraState(
    val result: LottoQrResult? = null,
    val winningNumbers: GetLottoNumber? = null,
)

sealed interface CameraSideEffect {
    class Toast(val message: String) : CameraSideEffect
}