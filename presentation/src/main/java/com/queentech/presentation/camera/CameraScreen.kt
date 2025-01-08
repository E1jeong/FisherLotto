package com.queentech.presentation.camera

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.queentech.presentation.theme.FisherLottoTheme

@Composable
fun CameraScreen() {
    Text("CameraScreen")
}

@Composable
@Preview
fun CameraScreenPreview() {
    FisherLottoTheme {
        Surface {
            CameraScreen()
        }
    }
}