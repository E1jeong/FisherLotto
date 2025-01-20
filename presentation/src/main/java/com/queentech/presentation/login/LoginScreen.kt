package com.queentech.presentation.login

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.queentech.presentation.navigation.RouteName
import com.queentech.presentation.theme.FisherLottoTheme
import com.queentech.presentation.theme.Paddings
import com.queentech.presentation.util.NavigationHelper
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    InitScreen(navController = navController, viewModel = viewModel)
    val state by viewModel.container.stateFlow.collectAsState()

    LoginScreen()
}

@Composable
private fun InitScreen(
    navController: NavHostController,
    viewModel: LoginViewModel
) {
    val context = LocalContext.current

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is LoginSideEffect.Toast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            is LoginSideEffect.NavigateToSignUp -> {
                val route = RouteName.SIGNUP
                NavigationHelper.navigate(navController, route)
            }

            is LoginSideEffect.NavigateToInformation -> {
                val route = RouteName.INFORMATION
                NavigationHelper.navigate(navController, route)
            }
        }
    }
}

@Composable
private fun LoginScreen() {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.padding(top = Paddings.xextra),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Fisher Lotto",
                style = MaterialTheme.typography.displaySmall,
            )
            Text(
                text = "Your favorite lotto app",
                style = MaterialTheme.typography.labelSmall
            )
        }
        Column(
            modifier = Modifier
                .padding(top = Paddings.extra)
                .padding(horizontal = Paddings.xlarge)
        ) {
            Text(
                modifier = Modifier.padding(top = Paddings.xextra),
                text = "Log in",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                modifier = Modifier.padding(top = Paddings.xlarge),
                text = "Id",
                style = MaterialTheme.typography.labelLarge
            )


            Text(
                modifier = Modifier.padding(top = Paddings.xlarge),
                text = "Password",
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = Paddings.extra)
                    .clickable(onClick = {})
            ) {
                Text(text = "Don't have an account?")
                Text(text = "Sign up", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
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
