package com.queentech.presentation.mypage

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.queentech.presentation.theme.FisherLottoTheme

@Composable
fun MyPageScreen() {
    Text("MyPageScreen")
}

@Composable
@Preview
fun MyPageScreenPreview() {
    FisherLottoTheme {
        Surface {
            MyPageScreen()
        }
    }
}