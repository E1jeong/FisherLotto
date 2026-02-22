package com.queentech.presentation.main.information.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.queentech.presentation.theme.AccentGold
import com.queentech.presentation.theme.DividerColor
import com.queentech.presentation.theme.Paddings
import com.queentech.presentation.theme.SectionBg
import com.queentech.presentation.theme.TextPrimary
import com.queentech.presentation.theme.TextSecondary

@Composable
fun FisherLottoResultInfo(
    modifier: Modifier = Modifier,
    latestDrawNumber: Int,
    latestDrawDate: String,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SectionBg),
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
                    text = "${latestDrawNumber}회차 어부로또 성적",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = latestDrawDate,
                    color = TextSecondary,
                    fontSize = 14.sp,
                )
            }
            MyTable()
        }
    }
}

@Composable
fun MyTable() {
    val cellBorderModifier = Modifier
        .border(width = 1.dp, color = DividerColor)
        .padding(8.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DividerColor)
    ) {
        // 첫 번째 행 (헤더)
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "1등",
                modifier = Modifier
                    .weight(1f)
                    .then(cellBorderModifier),
                textAlign = TextAlign.Center,
                color = TextSecondary,
            )
            Text(
                text = "2등",
                modifier = Modifier
                    .weight(1f)
                    .then(cellBorderModifier),
                textAlign = TextAlign.Center,
                color = TextSecondary,
            )
            Text(
                text = "3등",
                modifier = Modifier
                    .weight(1f)
                    .then(cellBorderModifier),
                textAlign = TextAlign.Center,
                color = TextSecondary,
            )
        }

        // 두 번째 행 (숫자)
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "1",
                modifier = Modifier
                    .weight(1f)
                    .then(cellBorderModifier),
                textAlign = TextAlign.Center,
                color = AccentGold,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "4",
                modifier = Modifier
                    .weight(1f)
                    .then(cellBorderModifier),
                textAlign = TextAlign.Center,
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "80",
                modifier = Modifier
                    .weight(1f)
                    .then(cellBorderModifier),
                textAlign = TextAlign.Center,
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 세 번째 행 (소제목)
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "1조합",
                modifier = Modifier
                    .weight(1f)
                    .then(cellBorderModifier),
                textAlign = TextAlign.Center,
                color = TextSecondary,
            )
            Text(
                text = "4조합",
                modifier = Modifier
                    .weight(1f)
                    .then(cellBorderModifier),
                textAlign = TextAlign.Center,
                color = TextSecondary,
            )
            Text(
                text = "80조합",
                modifier = Modifier
                    .weight(1f)
                    .then(cellBorderModifier),
                textAlign = TextAlign.Center,
                color = TextSecondary,
            )
        }
    }
}