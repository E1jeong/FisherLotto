package com.queentech.presentation.main.information

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.queentech.domain.model.news.NewsArticle
import com.queentech.presentation.main.information.component.FisherLottoResultInfo
import com.queentech.presentation.main.information.component.LatestDrawInfo
import com.queentech.presentation.theme.FisherLottoTheme
import com.queentech.presentation.theme.Paddings
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun InformationScreen(
    viewModel: InformationViewModel = hiltViewModel()
) {
    val state by viewModel.container.stateFlow.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshNews() // 또는 viewModel.loadNewsIfNeeded() 같은 함수로
    }

    val winningNumbers = listOf(
        state.getLottoNumberResponse.drwtNo1,
        state.getLottoNumberResponse.drwtNo2,
        state.getLottoNumberResponse.drwtNo3,
        state.getLottoNumberResponse.drwtNo4,
        state.getLottoNumberResponse.drwtNo5,
        state.getLottoNumberResponse.drwtNo6,
        state.getLottoNumberResponse.bnusNo
    )

    InformationContent(
        latestDrawNumber = state.latestDrawNumber,
        latestDrawDate = state.getLottoNumberResponse.drwNoDate,
        winningNumbers = winningNumbers,
        latestWinnerCount = state.getLottoNumberResponse.winnerCount,
        latestTotalWinnings = state.getLottoNumberResponse.totalWinnings,
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
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = Paddings.xextra)
    ) {
        item {
            LatestDrawInfo(
                modifier = Modifier.padding(Paddings.medium, Paddings.xextra, Paddings.medium, Paddings.medium),
                latestDrawNumber = latestDrawNumber,
                latestDrawDate = latestDrawDate,
                winningNumbers = winningNumbers,
                latestWinnerCount = latestWinnerCount,
                latestTotalWinnings = latestTotalWinnings,
            )
        }

        item {
            FisherLottoResultInfo(
                modifier = Modifier.padding(Paddings.medium, Paddings.medium, Paddings.medium, Paddings.medium),
                latestDrawNumber = latestDrawNumber,
                latestDrawDate = latestDrawDate,
            )
        }

        item {
            LotteryNewsHeader(
                modifier = Modifier.padding(horizontal = Paddings.medium, vertical = Paddings.medium),
                isLoading = isNewsLoading,
                onRefresh = onRefreshNews
            )
        }

        if (news.isEmpty() && !isNewsLoading) {
            item {
                Text(
                    modifier = Modifier.padding(horizontal = Paddings.medium),
                    text = "표시할 뉴스가 없어요.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(
                items = news,
                key = { it.link }
            ) { article ->
                LotteryNewsItem(
                    modifier = Modifier
                        .padding(horizontal = Paddings.medium, vertical = Paddings.small)
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
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "복권 뉴스",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp
            )
            Spacer(Modifier.width(8.dp))
        }
        IconButton(onClick = onRefresh) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
        }
    }
}

@Composable
private fun LotteryNewsItem(
    modifier: Modifier = Modifier,
    article: NewsArticle,
    onClick: () -> Unit,
) {
    ElevatedCard(
        modifier = modifier.clickable(onClick = onClick),
    ) {
        Column(modifier = Modifier.padding(Paddings.medium)) {
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleSmall,
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
                Spacer(Modifier.height(6.dp))
                Text(
                    text = meta,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (article.summary.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = article.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
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