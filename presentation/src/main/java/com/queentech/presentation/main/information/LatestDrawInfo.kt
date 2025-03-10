package com.queentech.presentation.main.information

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.queentech.presentation.theme.FisherLottoTheme
import com.queentech.presentation.theme.Paddings

@Composable
fun LatestDrawInfo(
    modifier: Modifier = Modifier,
    latestDrawNumber: Int,
    latestDrawDate: String,
    winningNumbers: List<Int>,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Paddings.medium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "${latestDrawNumber}회차 당첨번호",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = latestDrawDate,
                    style = MaterialTheme.typography.titleSmall
                )
            }

            WinningNumbersInfo(
                modifier = Modifier.padding(vertical = Paddings.extra),
                winningNumbers = winningNumbers
            )
        }
    }
}

@Composable
@Preview
private fun LatestDrawInfoPreview() {
    FisherLottoTheme {
        LatestDrawInfo(
            latestDrawNumber = 1,
            latestDrawDate = "2023-08-01",
            winningNumbers = listOf(1, 2, 3, 4, 5, 6, 7)
        )
    }
}
