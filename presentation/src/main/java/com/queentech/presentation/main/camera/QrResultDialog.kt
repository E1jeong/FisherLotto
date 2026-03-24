package com.queentech.presentation.main.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.queentech.domain.model.lotto.GetLottoNumber
import com.queentech.presentation.theme.AccentBlue
import com.queentech.presentation.theme.AccentGold
import com.queentech.presentation.theme.AccentGreen
import com.queentech.presentation.theme.BgDark
import com.queentech.presentation.theme.CardBg
import com.queentech.presentation.theme.DividerColor
import com.queentech.presentation.theme.SectionBg
import com.queentech.presentation.theme.TextPrimary
import com.queentech.presentation.theme.TextSecondary
import com.queentech.presentation.util.ColorHelper

@Composable
fun QrResultDialog(
    visible: Boolean,
    result: LottoQrResult?,
    winning: GetLottoNumber?,
    onDismissRequest: () -> Unit,
) {
    if (!visible || result == null || winning == null) return

    val mainWinningNumbers = listOf(
        winning.num1Int,
        winning.num2Int,
        winning.num3Int,
        winning.num4Int,
        winning.num5Int,
        winning.num6Int,
    )
    val bonusNumber = winning.bonusInt
    val allWinningNumbers = mainWinningNumbers + bonusNumber

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(20.dp))
                .background(BgDark)
                .padding(20.dp)
        ) {
            // ── 회차 헤더 ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(AccentGold)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "ROUND ${result.drawNo}",
                        color = AccentGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "${result.drawNo}회차 QR 결과",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── 당첨 번호 섹션 ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SectionBg)
                    .padding(14.dp)
            ) {
                Text(
                    text = "당첨 번호",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    mainWinningNumbers.forEach { number ->
                        LottoBallSmall(number = number)
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    Text(
                        text = "+",
                        color = TextSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    LottoBallSmall(number = bonusNumber)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── 구분선 ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(BgDark, DividerColor, BgDark)
                        )
                    )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── 내 번호 섹션 ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SectionBg)
                    .padding(14.dp)
            ) {
                Text(
                    text = "내 번호",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                result.games.forEachIndexed { index, game ->
                    val rank = calculateLottoRank(game, mainWinningNumbers, bonusNumber)
                    val isWinning = rank != "낙첨"
                    val borderColor = when (rank) {
                        "1등" -> AccentGold
                        "2등" -> AccentBlue
                        "3등" -> AccentGreen
                        "4등", "5등" -> TextSecondary
                        else -> Color.Transparent
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (index > 0) Modifier.padding(top = 8.dp) else Modifier
                            )
                            .clip(RoundedCornerShape(10.dp))
                            .then(
                                if (isWinning) Modifier.border(1.dp, borderColor, RoundedCornerShape(10.dp))
                                else Modifier
                            )
                            .background(CardBg)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 게임 라벨 (A, B, C ...)
                        Text(
                            text = "${'A' + index}",
                            color = AccentBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(20.dp),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            game.forEach { number ->
                                val isMatch = allWinningNumbers.contains(number)
                                if (isMatch) {
                                    LottoBallSmall(number = number)
                                } else {
                                    // 미당첨 번호 → 어두운 스타일
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(DividerColor),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = number.toString(),
                                            color = TextSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        RankBadge(rank = rank)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── 닫기 버튼 ──
            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "닫기",
                    color = AccentBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun calculateLottoRank(
    game: List<Int>,
    mainWinningNumbers: List<Int>,
    bonusNumber: Int,
): String {
    val mainMatchCount = game.count { it in mainWinningNumbers }
    val hasBonusMatch = bonusNumber in game

    return when {
        mainMatchCount == 6 -> "1등"
        mainMatchCount == 5 && hasBonusMatch -> "2등"
        mainMatchCount == 5 -> "3등"
        mainMatchCount == 4 -> "4등"
        mainMatchCount == 3 -> "5등"
        else -> "낙첨"
    }
}

@Composable
private fun RankBadge(rank: String) {
    val bgColor: Color
    val textColor: Color

    when (rank) {
        "1등" -> {
            bgColor = AccentGold; textColor = Color.DarkGray
        }
        "2등" -> {
            bgColor = AccentBlue; textColor = Color.White
        }

        "3등" -> {
            bgColor = AccentGreen; textColor = Color.White
        }

        "4등", "5등" -> {
            bgColor = DividerColor; textColor = TextPrimary
        }

        else -> {
            bgColor = Color.Transparent; textColor = TextSecondary
        }
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .then(
                if (rank != "낙첨") Modifier.background(bgColor) else Modifier
            )
            .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = rank,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = if (rank == "낙첨") FontWeight.Normal else FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LottoBallSmall(number: Int) {
    val ballColor = ColorHelper.selectBallColor(number)

    Box(
        modifier = Modifier
            .size(30.dp)
            .shadow(2.dp, CircleShape)
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
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1B2A)
@Composable
private fun RankBadgePreview() {
    Surface(color = BgDark) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("1등", "2등", "3등", "4등", "5등", "낙첨").forEach { rank ->
                RankBadge(rank = rank)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF152536, widthDp = 360)
@Composable
private fun GameRowPreview() {
    // 당첨번호: 3, 11, 15, 23, 34, 40 / 보너스: 7
    val mainWinning = listOf(3, 11, 15, 23, 34, 40)
    val bonus = 7
    val allWinning = mainWinning + bonus

    // 각 등수별 시나리오
    val games = listOf(
        listOf(3, 11, 15, 23, 34, 40),  // A: 6개 일치 → 1등
        listOf(3, 11, 15, 23, 34, 7),   // B: 5개 + 보너스 → 2등
        listOf(3, 11, 15, 23, 34, 9),   // C: 5개 일치 → 3등
        listOf(3, 11, 15, 23, 8, 9),    // D: 4개 일치 → 4등
        listOf(3, 11, 15, 5, 8, 9),     // E: 3개 일치 → 5등
        listOf(3, 11, 5, 6, 8, 9),      // F: 2개 일치 → 낙첨
    )

    Surface(color = SectionBg) {
        Column(modifier = Modifier.padding(14.dp)) {
            games.forEachIndexed { index, game ->
                val rank = calculateLottoRank(game, mainWinning, bonus)
                val isWinning = rank != "낙첨"
                val borderColor = when (rank) {
                    "1등" -> AccentGold
                    "2등" -> AccentBlue
                    "3등" -> AccentGreen
                    "4등", "5등" -> TextSecondary
                    else -> Color.Transparent
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (index > 0) Modifier.padding(top = 8.dp) else Modifier)
                        .clip(RoundedCornerShape(10.dp))
                        .then(
                            if (isWinning) Modifier.border(1.dp, borderColor, RoundedCornerShape(10.dp))
                            else Modifier
                        )
                        .background(CardBg)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${'A' + index}",
                        color = AccentBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(20.dp),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        game.forEach { number ->
                            if (allWinning.contains(number)) {
                                LottoBallSmall(number = number)
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(DividerColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = number.toString(),
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    RankBadge(rank = rank)
                }
            }
        }
    }
}
