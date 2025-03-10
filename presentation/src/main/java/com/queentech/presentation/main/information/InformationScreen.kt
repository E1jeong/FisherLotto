package com.queentech.presentation.main.information

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

    LatestDrawInfo(
        modifier = Modifier.padding(Paddings.medium, Paddings.xextra, Paddings.medium, Paddings.medium),
        latestDrawNumber = state.latestDrawNumber,
        latestDrawDate = state.getLottoNumberResponse.drwNoDate,
        winningNumbers = winningNumbers,
        latestWinnerCount = state.getLottoNumberResponse.winnerCount,
        latestTotalWinnings = state.getLottoNumberResponse.totalWinnings,
    )
}

@Composable
@Preview
fun InformationScreenPreview() {
    FisherLottoTheme {
        Surface {
            InformationScreen()
        }
    }
}