package com.queentech.presentation.login

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.queentech.presentation.theme.FisherLottoTheme

@Composable
fun LoginScreen() {
    Text("LoginScreen")
}

@Composable
@Preview
fun LoginScreenPreview() {
    FisherLottoTheme {
        Surface {
            LoginScreen()
        }
    }
}