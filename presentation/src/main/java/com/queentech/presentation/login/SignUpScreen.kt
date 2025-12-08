package com.queentech.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.queentech.presentation.theme.FisherLottoTheme

@Composable
fun SignUpScreen(
    navController: NavHostController,
    viewModel: LoginViewModel
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

    }
}

@Composable
@Preview
fun SignUpScreenPreview() {
    FisherLottoTheme {
        Surface {
//            SignUpScreen()
        }
    }
}