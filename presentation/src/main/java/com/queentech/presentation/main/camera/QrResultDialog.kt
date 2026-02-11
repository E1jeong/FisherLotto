package com.queentech.presentation.main.camera

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.queentech.domain.model.lotto.GetLottoNumber
import com.queentech.presentation.util.ColorHelper

@Composable
fun QrResultDialog(
    visible: Boolean,
    result: LottoQrResult?,
    winning: GetLottoNumber?,
    onDismissRequest: () -> Unit,
) {
    if (!visible || result == null || winning == null) return

    // 🔹 당첨번호 / 보너스 번호
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

    val defaultNumberColor = Color.White

    Dialog(onDismissRequest = onDismissRequest) {
        Card {
            Column(modifier = Modifier.padding(16.dp)) {

                // 회차
                Text(
                    text = "${result.drawNo}회차",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ✅ 해당 회차 당첨번호 표시
                Text(
                    text = "당첨 번호",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1등 번호 6개
                    mainWinningNumbers.forEach { number ->
                        Text(
                            text = number.toString().padStart(2, '0') + " ",
                            color = ColorHelper.selectBallColor(number),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    // 보너스 구분 기호
                    Text(
                        text = "+ ",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    // 보너스 번호
                    Text(
                        text = bonusNumber.toString().padStart(2, '0'),
                        color = ColorHelper.selectBallColor(bonusNumber),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                HorizontalDivider()

                Spacer(modifier = Modifier.height(8.dp))

                // ✅ 내 번호들
                Text(
                    text = "내 번호",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))

                result.games.forEachIndexed { index, game ->
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(
                            text = "${'A' + index} : ",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        game.forEach { number ->
                            val color = if (allWinningNumbers.contains(number)) {
                                // 당첨 번호 → 볼 색깔
                                ColorHelper.selectBallColor(number)
                            } else {
                                // 미당첨 번호 → 연한 회색
                                defaultNumberColor
                            }

                            val weight = if (allWinningNumbers.contains(number)) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            }

                            Text(
                                text = number.toString().padStart(2, '0') + " ",
                                color = color,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = weight
                            )
                        }
                    }
                }
            }
        }
    }
}
