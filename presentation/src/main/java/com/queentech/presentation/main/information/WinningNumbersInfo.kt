package com.queentech.presentation.main.information

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
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (i in 0..winningNumbers.size - 2) {
            Ball(number = winningNumbers[i])
        }
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "plus",
            tint = Color.White
        )
        Ball(number = winningNumbers[winningNumbers.size - 1])
    }
}

@Composable
private fun Ball(number: Int) {
    val backgroundColor = ColorHelper.selectBallColor(ballNumber = number)

    Box(
        modifier = Modifier
            .size(30.dp)
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
