package com.queentech.presentation.main.information

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.queentech.domain.model.news.NewsArticle
import com.queentech.presentation.main.information.component.LatestDrawInfo
import com.queentech.presentation.theme.AccentBlue
import com.queentech.presentation.theme.BgDark
import com.queentech.presentation.theme.DividerColor
import com.queentech.presentation.theme.FisherLottoTheme
import com.queentech.presentation.theme.Paddings
import com.queentech.presentation.theme.SectionBg
import com.queentech.presentation.theme.TextPrimary
import com.queentech.presentation.theme.TextSecondary
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun InformationScreen(
    viewModel: InformationViewModel = hiltViewModel()
) {
    val state by viewModel.container.stateFlow.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshNews()
    }

    val winningNumbers = listOf(
        state.getLottoNumberResponse.num1Int,
        state.getLottoNumberResponse.num2Int,
        state.getLottoNumberResponse.num3Int,
        state.getLottoNumberResponse.num4Int,
        state.getLottoNumberResponse.num5Int,
        state.getLottoNumberResponse.num6Int,
        state.getLottoNumberResponse.bonusInt
    )

    InformationContent(
        latestDrawNumber = state.getLottoNumberResponse.roundInt,
        latestDrawDate = state.getLottoNumberResponse.pdate,
        winningNumbers = winningNumbers,
        latestWinnerCount = state.getLottoNumberResponse.firstCountInt,
        latestTotalWinnings = state.getLottoNumberResponse.firstMoneyLong,
        news = state.news,
        isNewsLoading = state.isNewsLoading,
        onRefreshNews = viewModel::refreshNews,
    )
}

@Composable
private fun InformationContent(
    latestDrawNumber: Int,
    latestDrawDate: String,
    winningNumbers: List<Int>,
    latestWinnerCount: Int,
    latestTotalWinnings: Long,
    news: List<NewsArticle>,
    isNewsLoading: Boolean,
    onRefreshNews: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
        contentPadding = PaddingValues(bottom = Paddings.xextra)
    ) {
        // 최신 당첨 정보 섹션
        item {
            LatestDrawInfo(
                modifier = Modifier.padding(
                    Paddings.xlarge,
                    Paddings.xlarge,
                    Paddings.xlarge,
                    Paddings.medium
                ),
                latestDrawNumber = latestDrawNumber,
                latestDrawDate = latestDrawDate,
                winningNumbers = winningNumbers,
                latestWinnerCount = latestWinnerCount,
                latestTotalWinnings = latestTotalWinnings,
            )
        }

        // 뉴스 섹션
        item {
            LotteryNewsHeader(
                modifier = Modifier.padding(
                    horizontal = Paddings.xlarge,
                    vertical = Paddings.large
                ),
                isLoading = isNewsLoading,
                onRefresh = onRefreshNews
            )
        }

        if (news.isEmpty() && !isNewsLoading) {
            item {
                Text(
                    modifier = Modifier.padding(horizontal = Paddings.xlarge),
                    text = "표시할 뉴스가 없어요.",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        } else {
            items(
                items = news,
                key = { it.link }
            ) { article ->
                LotteryNewsItem(
                    modifier = Modifier
                        .padding(horizontal = Paddings.xlarge, vertical = Paddings.small)
                        .fillMaxWidth(),
                    article = article,
                    onClick = { uriHandler.openUri(article.link) }
                )
            }
        }
    }
}

@Composable
private fun LotteryNewsHeader(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    onRefresh: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "복권 뉴스",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
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
                    contentDescription = "Refresh",
                    tint = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun LotteryNewsItem(
    modifier: Modifier = Modifier,
    article: NewsArticle,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SectionBg)
            .clickable(onClick = onClick)
            .padding(Paddings.xlarge)
    ) {
        Text(
            text = article.title,
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        val meta = buildString {
            if (article.source.isNotBlank()) append(article.source)
            val dateText = article.publishedAtEpochMillis.toDisplayDate()
            if (dateText.isNotBlank()) {
                if (isNotEmpty()) append(" · ")
                append(dateText)
            }
        }

        if (meta.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = meta,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        if (article.summary.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
            Spacer(Modifier.height(8.dp))
            Text(
                text = article.summary,
                color = TextSecondary,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )
        }
    }
}

private fun Long.toDisplayDate(): String {
    if (this <= 0L) return ""
    val date = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
    return date.format(DateTimeFormatter.ISO_LOCAL_DATE)
}

@Composable
@Preview
fun InformationScreenPreview() {
    FisherLottoTheme {
        Surface {
            InformationContent(
                latestDrawNumber = 1,
                latestDrawDate = "2023-08-01",
                winningNumbers = listOf(1, 2, 3, 4, 5, 6, 7),
                latestWinnerCount = 10,
                latestTotalWinnings = 1000000000,
                news = listOf(
                    NewsArticle(
                        title = "로또 관련 뉴스 예시 타이틀",
                        link = "https://example.com",
                        source = "example.com",
                        publishedAtEpochMillis = System.currentTimeMillis(),
                        summary = "요약 텍스트 예시입니다."
                    )
                ),
                isNewsLoading = false,
                onRefreshNews = {},
            )
        }
    }
}
