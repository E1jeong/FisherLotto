package com.queentech.presentation.main.statistic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.breens.beetablescompose.BeeTablesCompose
import com.queentech.presentation.main.information.InformationViewModel
import com.queentech.presentation.main.information.component.FisherLottoResultInfo
import com.queentech.presentation.theme.FisherLottoTheme
import com.queentech.presentation.theme.Paddings

@Composable
fun StatisticScreen(viewModel: InformationViewModel = hiltViewModel()) {
    val state by viewModel.container.stateFlow.collectAsState()

    val winnerDatas = listOf(
        WinnerData(1161, 1, 3, 50),
        WinnerData(1160, 2, 10, 32),
        WinnerData(1159, 1, 7, 28),
        WinnerData(1158, 2, 6, 75),
    )

    val tableHeader = listOf("회차", "1등", "2등", "3등")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Paddings.medium, Paddings.xextra, Paddings.medium, Paddings.medium),
    ) {
        FisherLottoResultInfo(
            latestDrawNumber = state.latestDrawNumber,
            latestDrawDate = state.getLottoNumberResponse.drwNoDate,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            BeeTablesCompose(
                data = winnerDatas,
                headerTableTitles = tableHeader,
                headerTitlesBackGroundColor = Color.LightGray,
                shape = RoundedCornerShape(16.dp)
            )
        }
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