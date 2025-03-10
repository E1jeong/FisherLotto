package com.queentech.presentation.util

import androidx.compose.ui.graphics.Color
import com.queentech.presentation.theme.Ball1Color
import com.queentech.presentation.theme.Ball2Color
import com.queentech.presentation.theme.Ball3Color
import com.queentech.presentation.theme.Ball4Color
import com.queentech.presentation.theme.Ball5Color

object ColorHelper {

    fun selectBallColor(ballNumber: Int): Color {
        return when (ballNumber) {
            in 1..10 -> Ball1Color
            in 11..20 -> Ball2Color
            in 21..30 -> Ball3Color
            in 31..40 -> Ball4Color
            in 41..45 -> Ball5Color
            else -> Color.White
        }
    }
}