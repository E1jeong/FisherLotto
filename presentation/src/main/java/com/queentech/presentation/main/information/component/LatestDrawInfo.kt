package com.queentech.presentation.main.information.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.queentech.presentation.theme.AccentGold
import com.queentech.presentation.theme.AccentRed
import com.queentech.presentation.theme.CardBg
import com.queentech.presentation.theme.DividerColor
import com.queentech.presentation.theme.FisherLottoTheme
import com.queentech.presentation.theme.Paddings
import com.queentech.presentation.theme.TextPrimary
import com.queentech.presentation.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun LatestDrawInfo(
    modifier: Modifier = Modifier,
    latestDrawNumber: Int,
    latestDrawDate: String,
    winningNumbers: List<Int>,
    latestWinnerCount: Int,
    latestTotalWinnings: Long,
) {
    val formattedTotalWinnings = NumberFormat.getInstance(Locale.KOREA).format(latestTotalWinnings)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .padding(Paddings.xlarge)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "${latestDrawNumber}회차 당첨번호",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = latestDrawDate,
                color = TextSecondary,
                fontSize = 13.sp
            )
        }

        Spacer(Modifier.height(Paddings.xlarge))

        WinningNumbersInfo(
            modifier = Modifier,
            winningNumbers = winningNumbers
        )

        Spacer(Modifier.height(Paddings.xlarge))

        HorizontalDivider(color = DividerColor, thickness = 1.dp)

        Spacer(Modifier.height(Paddings.large))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "1등 당첨자",
                color = TextSecondary,
                fontSize = 14.sp
            )
            Text(
                text = "${latestWinnerCount}명",
                color = AccentGold,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(Paddings.medium))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "1인당 당첨금",
                color = TextSecondary,
                fontSize = 14.sp
            )
            Text(
                text = "${formattedTotalWinnings}원",
                color = AccentRed,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
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
            winningNumbers = listOf(1, 2, 3, 4, 5, 6, 7),
            latestWinnerCount = 10,
            latestTotalWinnings = 1000000000,
        )
    }
}
