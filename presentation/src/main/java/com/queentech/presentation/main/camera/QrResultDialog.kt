package com.queentech.presentation.main.camera

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.queentech.domain.model.GetLottoNumber

@Composable
fun QrResultDialog(
    visible: Boolean,
    result: LottoQrResult?,
    winning: GetLottoNumber?,
    onDismissRequest: () -> Unit,
) {
    if (visible && result != null && winning != null) {
        val winningNumbers = listOf(
            winning.drwtNo1,
            winning.drwtNo2,
            winning.drwtNo3,
            winning.drwtNo4,
            winning.drwtNo5,
            winning.drwtNo6,
            winning.bnusNo,
        )
        Dialog(onDismissRequest = onDismissRequest) {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "${result.drawNo}회차", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    result.games.forEachIndexed { index, game ->
                        Row(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text(text = "${'A' + index} : ")
                            game.forEach { number ->
                                val color = if (winningNumbers.contains(number)) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    Color.Unspecified
                                }
                                Text(
                                    text = number.toString().padStart(2, '0') + " ",
                                    color = color,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}