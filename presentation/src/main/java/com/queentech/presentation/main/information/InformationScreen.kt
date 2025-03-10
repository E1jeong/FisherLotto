package com.queentech.presentation.main.information

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.queentech.presentation.theme.FisherLottoTheme

@Composable
fun InformationScreen(
    viewModel: InformationViewModel = hiltViewModel()
) {
    val state by viewModel.container.stateFlow.collectAsState()

    Text(state.getLottoNumberResponse.toString())
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