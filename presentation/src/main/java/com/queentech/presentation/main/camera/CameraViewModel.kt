package com.queentech.presentation.main.camera

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.queentech.domain.model.lotto.GetLottoNumber
import com.queentech.domain.model.lotto.ScanHistory
import com.queentech.domain.usecase.lotto.GetLottoNumberUseCase
import com.queentech.domain.usecase.lotto.ScanHistoryRepository
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
    private val scanHistoryRepository: ScanHistoryRepository,
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

    companion object {
        private const val ONE_YEAR_MILLIS = 365L * 24 * 60 * 60 * 1000
    }

    private var lastScannedValue: String? = null

    fun onQrCodeScanned(rawValue: String) = intent {
        if (rawValue == lastScannedValue) return@intent
        lastScannedValue = rawValue
        Log.d("CameraViewModel", "raw QR: $rawValue")
        val result = LottoQrResult.parse(rawValue) ?: return@intent
        Log.d("CameraViewModel", "Parsed drawNo=${result.drawNo}, games=${result.games}")
        val winning = getLottoNumberUseCase(result.drawNo).getOrNull()
        reduce { state.copy(result = result, winningNumbers = winning) }

        // DB에 스캔 이력 저장 (중복 체크)
        if (winning != null) {
            if (!scanHistoryRepository.exists(result.drawNo, result.games)) {
                val winningNumbers = listOf(
                    winning.num1Int, winning.num2Int, winning.num3Int,
                    winning.num4Int, winning.num5Int, winning.num6Int,
                    winning.bonusInt
                )
                val matchCount = result.games.sumOf { game ->
                    game.count { it in winningNumbers }
                }
                scanHistoryRepository.save(result.drawNo, result.games, matchCount)
            }
        }
    }

    fun loadScanHistory() = intent {
        // 1년 이전 데이터 정리
        val cutoff = System.currentTimeMillis() - ONE_YEAR_MILLIS
        scanHistoryRepository.deleteOlderThan(cutoff)
        val list = scanHistoryRepository.getAll()
        reduce { state.copy(scanHistoryList = list, showHistorySheet = true) }
    }

    fun onHistoryItemClick(item: ScanHistory) = intent {
        val winning = getLottoNumberUseCase(item.drawNo).getOrNull()
        reduce { state.copy(selectedHistory = item, selectedHistoryWinning = winning) }
    }

    fun dismissHistorySheet() = intent {
        reduce { state.copy(showHistorySheet = false) }
    }

    fun dismissHistoryDetail() = intent {
        reduce { state.copy(selectedHistory = null, selectedHistoryWinning = null) }
    }

    fun deleteHistoryItem(id: Long) = intent {
        scanHistoryRepository.deleteById(id)
        val list = scanHistoryRepository.getAll()
        reduce { state.copy(scanHistoryList = list) }
    }
}

@Immutable
data class CameraState(
    val result: LottoQrResult? = null,
    val winningNumbers: GetLottoNumber? = null,
    val scanHistoryList: List<ScanHistory> = emptyList(),
    val showHistorySheet: Boolean = false,
    val selectedHistory: ScanHistory? = null,
    val selectedHistoryWinning: GetLottoNumber? = null,
)

sealed interface CameraSideEffect {
    class Toast(val message: String) : CameraSideEffect
}
