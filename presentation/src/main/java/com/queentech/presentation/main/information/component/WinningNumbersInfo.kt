package com.queentech.presentation.main.information.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.queentech.presentation.theme.FisherLottoTheme
import com.queentech.presentation.util.ColorHelper

@Composable
fun WinningNumbersInfo(
    modifier: Modifier = Modifier,
    winningNumbers: List<Int>,
    numberSize: Int = 30,
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (i in winningNumbers.indices) {
            if (i == winningNumbers.size - 1) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "plus",
                    tint = Color.White
                )
            }

            Ball(
                number = winningNumbers[i],
                size = numberSize,
            )
        }
    }
}

@Composable
private fun Ball(
    number: Int,
    size: Int,
) {
    val backgroundColor = ColorHelper.selectBallColor(ballNumber = number)

    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
@Preview
private fun WinningNumbersInfoPreview() {
    FisherLottoTheme {
        WinningNumbersInfo(winningNumbers = listOf(1, 2, 3, 4, 5, 6, 7))
    }
}
