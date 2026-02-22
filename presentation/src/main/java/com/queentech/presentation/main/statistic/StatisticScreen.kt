package com.queentech.presentation.main.statistic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.queentech.presentation.theme.AccentBlue
import com.queentech.presentation.theme.AccentGold
import com.queentech.presentation.theme.AccentGreen
import com.queentech.presentation.theme.AccentRed
import com.queentech.presentation.theme.BgDark
import com.queentech.presentation.theme.CardBg
import com.queentech.presentation.theme.DividerColor
import com.queentech.presentation.theme.FisherLottoTheme
import com.queentech.presentation.theme.Paddings
import com.queentech.presentation.theme.SectionBg
import com.queentech.presentation.theme.TextPrimary
import com.queentech.presentation.theme.TextSecondary

@Composable
fun StatisticScreen(viewModel: StatisticViewModel = hiltViewModel()) {
    val state by viewModel.container.stateFlow.collectAsState()

    StatisticContent(
        winnerDataList = state.winnerDataList,
        isLoading = state.isLoading,
        onRefresh = viewModel::refresh,
    )
}

@Composable
private fun StatisticContent(
    winnerDataList: List<WinnerData>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
        contentPadding = PaddingValues(bottom = Paddings.xextra)
    ) {
        // 헤더 섹션
        item {
            StatisticHeader(
                modifier = Modifier.padding(
                    Paddings.xlarge,
                    Paddings.xlarge,
                    Paddings.xlarge,
                    Paddings.medium
                ),
                isLoading = isLoading,
                onRefresh = onRefresh,
            )
        }

        // 테이블 헤더
        item {
            StatisticTableHeader(
                modifier = Modifier.padding(horizontal = Paddings.xlarge)
            )
        }

        // 테이블 데이터 행
        if (winnerDataList.isEmpty() && !isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Paddings.xlarge),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "표시할 통계 데이터가 없어요.",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            itemsIndexed(
                items = winnerDataList,
                key = { _, item -> item.drawNumber }
            ) { index, data ->
                StatisticTableRow(
                    modifier = Modifier.padding(horizontal = Paddings.xlarge),
                    data = data,
                    isEvenRow = index % 2 == 0,
                )
            }
        }

        // 하단 안내
        item {
            Spacer(Modifier.height(Paddings.xlarge))
            Text(
                modifier = Modifier.padding(horizontal = Paddings.xlarge),
                text = "※ 등수별 당첨 조합 수 기준 (서버 연동 후 실제 데이터 적용 예정)",
                color = TextSecondary,
                fontSize = 11.sp,
            )
        }
    }
}

// ─── Header ─────────────────────────────────────────

@Composable
private fun StatisticHeader(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    onRefresh: () -> Unit,
) {
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
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "어부로또 당첨 통계",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = AccentBlue
                    )
                    Spacer(Modifier.width(8.dp))
                }
                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "새로고침",
                        tint = TextSecondary
                    )
                }
            }
        }

        Spacer(Modifier.height(Paddings.medium))

        Text(
            text = "회차별 등수 당첨 조합 수를 확인하세요.",
            color = TextSecondary,
            fontSize = 13.sp
        )
    }
}

// ─── Table Header ───────────────────────────────────

@Composable
private fun StatisticTableHeader(modifier: Modifier = Modifier) {
    val headers = listOf("회차", "1등", "2등", "3등", "4등", "5등", "6등")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .background(SectionBg)
            .padding(vertical = Paddings.large, horizontal = Paddings.medium),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        headers.forEachIndexed { index, title ->
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = if (index == 0) AccentBlue else TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

// ─── Table Row ──────────────────────────────────────

@Composable
private fun StatisticTableRow(
    modifier: Modifier = Modifier,
    data: WinnerData,
    isEvenRow: Boolean,
) {
    val rowBg = if (isEvenRow) CardBg else SectionBg

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(rowBg)
                .padding(vertical = Paddings.large, horizontal = Paddings.medium),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 회차
            Text(
                text = "${data.drawNumber}",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
            // 1등 (강조)
            Text(
                text = "${data.firstPrize}",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = AccentGold,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
            // 2등
            Text(
                text = "${data.secondPrize}",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = AccentGreen,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
            // 3등
            Text(
                text = "${data.thirdPrize}",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = AccentRed,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
            // 4등
            Text(
                text = "${data.fourthPrize}",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = TextSecondary,
                fontSize = 13.sp,
            )
            // 5등
            Text(
                text = "${data.fifthPrize}",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = TextSecondary,
                fontSize = 13.sp,
            )
            // 6등
            Text(
                text = "${data.sixthPrize}",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = TextSecondary,
                fontSize = 13.sp,
            )
        }
        HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
    }
}

// ─── Preview ────────────────────────────────────────

@Composable
@Preview
fun StatisticScreenPreview() {
    FisherLottoTheme {
        Surface {
            StatisticContent(
                winnerDataList = listOf(
                    WinnerData(1161, 1, 3, 50, 233, 566, 788),
                    WinnerData(1160, 2, 10, 32, 233, 566, 788),
                    WinnerData(1159, 1, 7, 28, 233, 566, 788),
                    WinnerData(1158, 2, 6, 75, 233, 566, 788),
                ),
                isLoading = false,
                onRefresh = {},
            )
        }
    }
}

data class WinnerData(
    val drawNumber: Int,
    val firstPrize: Int,
    val secondPrize: Int,
    val thirdPrize: Int,
    val fourthPrize: Int,
    val fifthPrize: Int,
    val sixthPrize: Int,
)
