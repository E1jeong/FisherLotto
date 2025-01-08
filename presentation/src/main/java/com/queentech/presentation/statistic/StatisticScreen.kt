package com.queentech.presentation.statistic

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.queentech.presentation.theme.FisherLottoTheme

@Composable
fun StatisticScreen() {
    Text("StatisticScreen")
}

@Composable
@Preview
fun StatisticScreenPreview() {
    FisherLottoTheme {
        Surface {
            StatisticScreen()
        }
    }
}