package com.queentech.presentation.main.lottonumber

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.queentech.presentation.theme.FisherLottoTheme

@Composable
fun LottoNumberScreen() {
    Text("LottoNumberScreen")
}

@Composable
@Preview
fun LottoNumberScreenPreview() {
    FisherLottoTheme {
        Surface {
            LottoNumberScreen()
        }
    }
}