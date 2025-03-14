package com.queentech.presentation.main.statistic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.breens.beetablescompose.BeeTablesCompose
import com.queentech.presentation.theme.FisherLottoTheme

@Composable
fun StatisticScreen() {
    val winnerDatas = listOf(
        WinnerData(1161, 1, 3, 50),
        WinnerData(1160, 2, 10, 32),
        WinnerData(1159, 1, 7, 28),
        WinnerData(1158, 2, 6, 75),
    )

    val tableHeader = listOf("회차", "1등", "2등", "3등")
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        BeeTablesCompose(
            data = winnerDatas,
            headerTableTitles = tableHeader,
            headerTitlesBackGroundColor = Color.LightGray
        )
    }
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

data class WinnerData(
    val drawNumber: Int,
    val firstPrize: Int,
    val secondPrize: Int,
    val thirdPrize: Int,
)