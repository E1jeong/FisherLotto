package com.queentech.presentation.main.statistic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.queentech.domain.model.lotto.GetLottoNumber
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

// 탭 상태를 정의하는 Enum Class
enum class StatisticTab(val title: String) {
    COUNT("당첨 인원"),
    MONEY("당첨 금액")
}

// 회차, 1등, 2등, 3등, 4등, 5등의 가로 비율 (총합에 대한 비율로 자동 계산됨)
private val countColumnWeights = listOf(1.2f, 1.0f, 1.0f, 1.2f, 1.6f, 2.0f)
private val moneyColumnWeights = listOf(1.2f, 2.5f, 2.0f, 2.0f)

@Composable
fun StatisticScreen(viewModel: StatisticViewModel = hiltViewModel()) {
    val state by viewModel.container.stateFlow.collectAsState()

    StatisticContent(
        state = state,
        onRefresh = viewModel::refresh,
        onLoadMore = viewModel::loadMoreData
    )
}

@Composable
private fun StatisticContent(
    state: StatisticState,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
) {
    var selectedTab by remember { mutableStateOf(StatisticTab.COUNT) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
        contentPadding = PaddingValues(bottom = Paddings.xextra)
    ) {
        // 1. 헤더 섹션
        item {
            StatisticHeader(
                modifier = Modifier.padding(
                    Paddings.xlarge,
                    Paddings.xlarge,
                    Paddings.xlarge,
                    Paddings.medium
                ),
                isLoading = state.isLoading,
                onRefresh = onRefresh,
            )
        }

        // 2. 탭 선택 섹션
        item {
            StatisticTabRow(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                modifier = Modifier.padding(horizontal = Paddings.xlarge, vertical = Paddings.medium)
            )
        }

        // 3. 테이블 헤더 (선택된 탭에 따라 다르게 렌더링)
        item {
            if (selectedTab == StatisticTab.COUNT) {
                StatisticCountTableHeader(modifier = Modifier.padding(horizontal = Paddings.xlarge))
            } else {
                StatisticMoneyTableHeader(modifier = Modifier.padding(horizontal = Paddings.xlarge))
            }
        }

        // 4. 테이블 데이터
        if (state.lottoList.isEmpty() && !state.isLoading) {
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
                items = state.lottoList,
                key = { _, item -> item.roundInt }
            ) { index, data ->

                if (selectedTab == StatisticTab.COUNT) {
                    StatisticCountTableRow(
                        modifier = Modifier.padding(horizontal = Paddings.xlarge),
                        data = data,
                        isEvenRow = index % 2 == 0,
                    )
                } else {
                    StatisticMoneyTableRow(
                        modifier = Modifier.padding(horizontal = Paddings.xlarge),
                        data = data,
                        isEvenRow = index % 2 == 0,
                    )
                }

                // 무한 스크롤 트리거
                if (index == state.lottoList.lastIndex && !state.isLoading && !state.isPaginating) {
                    LaunchedEffect(index) {
                        onLoadMore()
                    }
                }
            }
        }

        // 하단 페이징 스피너
        if (state.isPaginating) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Paddings.xlarge),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = AccentBlue,
                        strokeWidth = 2.dp
                    )
                }
            }
        }
    }
}

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
            text = "최근 회차별 등수 당첨 조합 수를 확인하세요.",
            color = TextSecondary,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun StatisticTabRow(
    selectedTab: StatisticTab,
    onTabSelected: (StatisticTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CardBg)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StatisticTab.entries.forEach { tab ->
            val isSelected = selectedTab == tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) SectionBg else Color.Transparent)
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = Paddings.medium),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab.title,
                    color = if (isSelected) TextPrimary else TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

// 금액을 "OO억 OO만" 형태로 포맷팅하는 유틸 함수
private fun formatMoney(moneyStr: String): String {
    val amount = moneyStr.toLongOrNull() ?: return "-"
    if (amount == 0L) return "0"

    val uk = amount / 100_000_000
    val man = (amount % 100_000_000) / 10_000

    return buildString {
        if (uk > 0) append("${uk}억 ")
        if (man > 0) append("${man}만")
        if (uk == 0L && man == 0L) append("$amount")
    }.trim()
}

// -------------------------------------------------------------------------
// 당첨 [인원] 테이블 컴포넌트
// -------------------------------------------------------------------------
@Composable
private fun StatisticCountTableHeader(modifier: Modifier = Modifier) {
    val headers = listOf("회차", "1등", "2등", "3등", "4등", "5등")
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .background(SectionBg)
            .padding(vertical = Paddings.large, horizontal = Paddings.medium),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        headers.forEachIndexed { index, title ->
            Text(
                text = title,
                modifier = Modifier.weight(countColumnWeights[index]),
                textAlign = TextAlign.Center,
                color = if (index == 0) AccentBlue else TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
            if (index == 0) {
                Box(modifier = Modifier.width(1.dp).height(14.dp).background(DividerColor))
            }
        }
    }
}

@Composable
private fun StatisticCountTableRow(
    modifier: Modifier = Modifier,
    data: GetLottoNumber,
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
            Text(
                text = "${data.roundInt}",
                modifier = Modifier.weight(countColumnWeights[0]),
                textAlign = TextAlign.Center,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
            Box(modifier = Modifier.width(1.dp).height(24.dp).background(DividerColor))
            Text(
                text = "${data.firstCountInt}",
                modifier = Modifier.weight(countColumnWeights[1]),
                textAlign = TextAlign.Center,
                color = AccentGold,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "${data.secondCountInt}",
                modifier = Modifier.weight(countColumnWeights[2]),
                textAlign = TextAlign.Center,
                color = AccentGreen,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "${data.thirdCountInt}",
                modifier = Modifier.weight(countColumnWeights[3]),
                textAlign = TextAlign.Center,
                color = AccentRed,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "${data.fourthCountInt}",
                modifier = Modifier.weight(countColumnWeights[4]),
                textAlign = TextAlign.Center,
                color = TextSecondary,
                fontSize = 13.sp,
            )
            Text(
                text = "${data.fifthCountInt}",
                modifier = Modifier.weight(countColumnWeights[5]),
                textAlign = TextAlign.Center,
                color = TextSecondary,
                fontSize = 13.sp,
            )
        }
        HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
    }
}

// -------------------------------------------------------------------------
// 당첨 [금액] 테이블 컴포넌트
// -------------------------------------------------------------------------
@Composable
private fun StatisticMoneyTableHeader(modifier: Modifier = Modifier) {
    val headers = listOf("회차", "1등", "2등", "3등")
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .background(SectionBg)
            .padding(vertical = Paddings.large, horizontal = Paddings.medium),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        headers.forEachIndexed { index, title ->
            Text(
                text = title,
                modifier = Modifier.weight(moneyColumnWeights[index]),
                textAlign = TextAlign.Center,
                color = if (index == 0) AccentBlue else TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
            if (index == 0) {
                Box(modifier = Modifier.width(1.dp).height(14.dp).background(DividerColor))
            }
        }
    }
}

@Composable
private fun StatisticMoneyTableRow(
    modifier: Modifier = Modifier,
    data: GetLottoNumber,
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
            Text(
                text = "${data.roundInt}",
                modifier = Modifier.weight(moneyColumnWeights[0]),
                textAlign = TextAlign.Center,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
            Box(modifier = Modifier.width(1.dp).height(24.dp).background(DividerColor))

            Text(
                text = formatMoney(data.firstMoney),
                modifier = Modifier.weight(moneyColumnWeights[1]),
                textAlign = TextAlign.Center,
                color = AccentGold,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = formatMoney(data.secondMoney),
                modifier = Modifier.weight(moneyColumnWeights[2]),
                textAlign = TextAlign.Center,
                color = AccentGreen,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = formatMoney(data.thirdMoney),
                modifier = Modifier.weight(moneyColumnWeights[3]),
                textAlign = TextAlign.Center,
                color = AccentRed,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
    }
}

@Composable
@Preview
fun StatisticScreenPreview() {
    // Preview를 위한 Mock GetLottoNumber 생성 함수
    fun createMockLotto(round: String, first: String, second: String) = GetLottoNumber(
        firstCount = first, firstMoney = "0", secondCount = second, secondMoney = "0",
        thirdCount = "300", thirdMoney = "0", fourthCount = "1000", fourthMoney = "0",
        fifthCount = "50000", fifthMoney = "0", bonus = "0", num1 = "1", num2 = "2",
        num3 = "3", num4 = "4", num5 = "5", num6 = "6", pdate = "2026-02-07", round = round
    )

    FisherLottoTheme {
        Surface {
            StatisticContent(
                state = StatisticState(
                    lottoList = listOf(
                        createMockLotto("1161", "3", "50"),
                        createMockLotto("1160", "10", "32"),
                        createMockLotto("1159", "7", "28"),
                    ),
                    isLoading = false
                ),
                onRefresh = {},
                onLoadMore = {}
            )
        }
    }
}
