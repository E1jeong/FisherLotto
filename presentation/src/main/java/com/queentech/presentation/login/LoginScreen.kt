package com.queentech.presentation.login

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.queentech.presentation.R
import com.queentech.presentation.component.textfield.DefaultTextField
import com.queentech.presentation.navigation.RouteName
import com.queentech.presentation.theme.FisherLottoTheme
import com.queentech.presentation.theme.Paddings
import com.queentech.presentation.util.NavigationHelper
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel
) {
    val state by viewModel.container.stateFlow.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadCachedUser()
    }

    InitLoginScreen(context, navController, viewModel)

    LoginContent(
        email = state.userEmail.ifEmpty { state.emailInput },
        enabledTextInput = state.userEmail.isEmpty(),
        isVisibleSignUp = state.userEmail.isEmpty(),
        onEmailChanged = viewModel::onEmailChanged,
        onLoginClick = viewModel::onLoginClick,
        onSignUpClick = viewModel::onSignUpClick
    )
}

@Composable
private fun InitLoginScreen(
    context: Context,
    navController: NavHostController,
    viewModel: LoginViewModel
) {
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is LoginSideEffect.Toast ->
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()

            is LoginSideEffect.NavigateToSignUp ->
                NavigationHelper.navigate(navController, RouteName.SIGNUP)

            is LoginSideEffect.NavigateToInformation ->
                NavigationHelper.navigate(navController, RouteName.INFORMATION)
        }
    }
}

@Composable
private fun LoginContent(
    email: String,
    enabledTextInput: Boolean,
    isVisibleSignUp: Boolean,
    onEmailChanged: (String) -> Unit,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    val contentPadding = 24.dp
    val fullWidth = Modifier
        .fillMaxWidth()
        .padding(horizontal = contentPadding)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            LoginBottomBar(
                fullWidth = fullWidth,
                email = email,
                isVisibleSignUp = isVisibleSignUp,
                onLoginClick = onLoginClick,
                onSignUpClick = onSignUpClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(top = Paddings.xextra),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Fisher Lotto",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Your favorite lotto app",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            LottieFishing(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            DefaultTextField(
                modifier = fullWidth,
                enabled = enabledTextInput,
                value = email,
                placeholder = "Email (ID)",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                onValueChange = onEmailChanged
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun LoginBottomBar(
    fullWidth: Modifier,
    isVisibleSignUp: Boolean,
    email: String,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            modifier = fullWidth.height(48.dp),
            onClick = onLoginClick,
            enabled = email.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Text("로그인")
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isVisibleSignUp) {
                Text(
                    text = "Don't have an account? ",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    modifier = Modifier.clickable { onSignUpClick() },
                    text = "Sign up",
                    color = MaterialTheme.colorScheme.secondary
                )
            } else {
                Text(
                    text = "Already sign up account",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
    }
}

@Composable
fun LottieFishing(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.fishing)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}

@Preview
@Composable
fun LoginScreenPreview() {
    FisherLottoTheme {
        Surface {
            LoginContent(
                email = "",
                enabledTextInput = true,
                isVisibleSignUp = true,
                onEmailChanged = {},
                onLoginClick = {},
                onSignUpClick = {}
            )
        }
    }
}
