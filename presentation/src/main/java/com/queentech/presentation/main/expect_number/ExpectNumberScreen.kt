package com.queentech.presentation.main.expect_number

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.queentech.presentation.theme.AccentBlue
import com.queentech.presentation.theme.BgDark
import com.queentech.presentation.theme.CardBg
import com.queentech.presentation.theme.DividerColor
import com.queentech.presentation.theme.FisherLottoTheme
import com.queentech.presentation.theme.LastWeekAccent
import com.queentech.presentation.theme.SectionBg
import com.queentech.presentation.theme.TextPrimary
import com.queentech.presentation.theme.TextSecondary
import com.queentech.presentation.util.ColorHelper
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ExpectNumberScreen(
    navController: NavHostController,
    viewModel: ExpectNumberViewModel = hiltViewModel()
) {
    val state by viewModel.container.stateFlow.collectAsState()
    val context = LocalContext.current

    InitExpectNumberScreen(
        context = context,
        navController = navController,
        viewModel = viewModel
    )

    ExpectNumberContent(
        lastWeekNumbers = state.lastWeekNumbers,
        thisWeekNumbers = state.thisWeekNumbers,
        isThisWeekIssued = state.isThisWeekIssued,
        onNumberIssueClick = viewModel::onExpectNumberClick
    )
}

@Composable
private fun InitExpectNumberScreen(
    context: Context,
    navController: NavHostController,
    viewModel: ExpectNumberViewModel
) {
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ExpectNumberSideEffect.Toast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
private fun ExpectNumberContent(
    lastWeekNumbers: List<String>,
    thisWeekNumbers: List<String>,
    isThisWeekIssued: Boolean,
    onNumberIssueClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        // ── 저번주 섹션 (상단 절반) ──
        WeekSection(
            modifier = Modifier.weight(1f),
            label = "LAST WEEK",
            labelKor = "저번주 예상 번호",
            accentColor = LastWeekAccent,
            numbers = lastWeekNumbers,
            isIssued = lastWeekNumbers.isNotEmpty(),
            emptyMessage = "저번주 번호가 없어요",
            emptyEmoji = "📭",
        )

        // ── 구분선 ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(BgDark, DividerColor, BgDark)
                    )
                )
        )

        // ── 이번주 섹션 (하단 절반) ──
        WeekSection(
            modifier = Modifier.weight(1f),
            label = "THIS WEEK",
            labelKor = "이번주 예상 번호",
            accentColor = AccentBlue,
            numbers = thisWeekNumbers,
            isIssued = isThisWeekIssued,
            emptyMessage = "발급하기를 눌러보세요",
            emptyEmoji = "🎣",
            showIssueButton = true,
            onIssueClick = onNumberIssueClick
        )
    }
}

@Composable
private fun WeekSection(
    modifier: Modifier = Modifier,
    label: String,
    labelKor: String,
    accentColor: Color,
    numbers: List<String>,
    isIssued: Boolean,
    emptyMessage: String,
    emptyEmoji: String,
    showIssueButton: Boolean = false,
    onIssueClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SectionBg)
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        // 헤더 영역
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 라벨 + 억센트 바
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(36.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(accentColor)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = label,
                        color = accentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = labelKor,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // 발급하기 버튼 (이번주만)
            if (showIssueButton) {
                Button(
                    onClick = onIssueClick,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp)
                ) {
                    Text(
                        text = "발급하기",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 번호 리스트 or 빈 상태
        if (!isIssued) {
            EmptyState(emoji = emptyEmoji, message = emptyMessage)
        } else {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically()
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    itemsIndexed(numbers) { index, numberSet ->
                        LottoNumberRow(index = index + 1, numberSet = numberSet)
                    }
                }
            }
        }
    }
}

@Composable
private fun LottoNumberRow(index: Int, numberSet: String) {
    val numbers = numberSet.split(",").mapNotNull { it.trim().toIntOrNull() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(CardBg)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 순번 뱃지
        Text(
            text = "$index",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(20.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.width(10.dp))

        // 로또 번호 공들
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            numbers.forEach { number ->
                LottoBall(number = number)
            }
        }
    }
}

@Composable
private fun LottoBall(number: Int) {
    val ballColor = ColorHelper.selectBallColor(number)

    Box(
        modifier = Modifier
            .size(34.dp)
            .shadow(3.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        ballColor.copy(alpha = 1f),
                        ballColor.copy(alpha = 0.75f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EmptyState(emoji: String, message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = emoji, fontSize = 38.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = message,
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
@Preview
fun ExpectNumberScreenPreview() {
    FisherLottoTheme {
        Surface {
            ExpectNumberContent(
                lastWeekNumbers = listOf(
                    "1,7,15,23,38,42",
                    "3,12,18,27,33,41",
                    "5,9,21,28,35,44"
                ),
                thisWeekNumbers = listOf(
                    "2,11,19,25,36,43",
                    "4,8,22,30,37,40"
                ),
                isThisWeekIssued = true,
                onNumberIssueClick = {}
            )
        }
    }
}