package com.queentech.presentation.information

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.queentech.presentation.theme.FisherLottoTheme

@Composable
fun InformationScreen() {
    Text("InformationScreen")
}

@Composable
@Preview
fun InformationScreenPreview() {
    FisherLottoTheme {
        Surface {
            InformationScreen()
        }
    }
}