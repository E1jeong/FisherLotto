package com.queentech.presentation.main.information

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.queentech.presentation.theme.FisherLottoTheme
import com.queentech.presentation.theme.Paddings

@Composable
fun InformationScreen(
    viewModel: InformationViewModel = hiltViewModel()
) {
    val state by viewModel.container.stateFlow.collectAsState()

    val winningNumbers = listOf(
        state.getLottoNumberResponse.drwtNo1,
        state.getLottoNumberResponse.drwtNo2,
        state.getLottoNumberResponse.drwtNo3,
        state.getLottoNumberResponse.drwtNo4,
        state.getLottoNumberResponse.drwtNo5,
        state.getLottoNumberResponse.drwtNo6,
        state.getLottoNumberResponse.bnusNo
    )

    InformationContent(
        latestDrawNumber = state.latestDrawNumber,
        latestDrawDate = state.getLottoNumberResponse.drwNoDate,
        winningNumbers = winningNumbers,
        latestWinnerCount = state.getLottoNumberResponse.winnerCount,
        latestTotalWinnings = state.getLottoNumberResponse.totalWinnings,
    )
}

@Composable
private fun InformationContent(
    latestDrawNumber: Int,
    latestDrawDate: String,
    winningNumbers: List<Int>,
    latestWinnerCount: Int,
    latestTotalWinnings: Long,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LatestDrawInfo(
            modifier = Modifier.padding(Paddings.medium, Paddings.xextra, Paddings.medium, Paddings.medium),
            latestDrawNumber = latestDrawNumber,
            latestDrawDate = latestDrawDate,
            winningNumbers = winningNumbers,
            latestWinnerCount = latestWinnerCount,
            latestTotalWinnings = latestTotalWinnings,
        )
        FisherLottoResultInfo(
            modifier = Modifier.padding(Paddings.medium, Paddings.medium, Paddings.medium, Paddings.xextra),
            latestDrawNumber = latestDrawNumber,
            latestDrawDate = latestDrawDate,
        )
    }
}

@Composable
@Preview
fun InformationScreenPreview() {
    FisherLottoTheme {
        Surface {
            InformationContent(
                latestDrawNumber = 1,
                latestDrawDate = "2023-08-01",
                winningNumbers = listOf(1, 2, 3, 4, 5, 6, 7),
                latestWinnerCount = 10,
                latestTotalWinnings = 1000000000,
            )
        }
    }
}