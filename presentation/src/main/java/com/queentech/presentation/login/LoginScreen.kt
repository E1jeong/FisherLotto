package com.queentech.presentation.login

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

    InitLoginScreen(context, navController, viewModel)

    LoginContent(
        email = state.emailInput,
        rememberId = state.rememberId,
        onEmailChanged = viewModel::onEmailChanged,
        onRememberIdChanged = viewModel::onRememberIdChanged,
        onLoginClick = viewModel::onEmailLoginClick,
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

            is LoginSideEffect.SignUpDoneNavigateToLogin ->
                navController.popBackStack()
        }
    }
}

@Composable
private fun LoginContent(
    email: String,
    rememberId: Boolean,
    onEmailChanged: (String) -> Unit,
    onRememberIdChanged: (Boolean) -> Unit,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    // ✅ 화면 폭 통일
    val contentPadding = 24.dp
    val fullWidth = Modifier
        .fillMaxWidth()
        .padding(horizontal = contentPadding)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // ✅ 여기서 “extra bottom padding” 제거하고, 딱 붙게 만든다
            LoginBottomBarTight(
                fullWidth = fullWidth,
                email = email,
                onLoginClick = onLoginClick,
                onSignUpClick = onSignUpClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)           // ✅ bottomBar만큼만 비워줌
                .padding(top = Paddings.xextra), // 상단만
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Fisher Lotto", style = MaterialTheme.typography.displaySmall)
            Text("Your favorite lotto app", style = MaterialTheme.typography.labelSmall)

            LottieFishing(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            DefaultTextField(
                modifier = fullWidth,
                value = email,
                placeholder = "Email (ID)",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                onValueChange = onEmailChanged
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = fullWidth,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text("ID 기억하기", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = rememberId,
                    onCheckedChange = onRememberIdChanged
                )
            }

            // ✅ 여기서 바닥으로 더 밀지 않음
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun LoginBottomBarTight(
    fullWidth: Modifier,
    email: String,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()          // ✅ 제스처바 높이만큼만 띄움(필수)
            .padding(horizontal = 0.dp)       // ✅ 불필요한 padding 제거
            .padding(bottom = 0.dp, top = 0.dp), // ✅ “완전 바닥” 스타일
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            modifier = fullWidth.height(48.dp),
            onClick = onLoginClick,
            enabled = email.isNotBlank()
        ) { Text("로그인") }

        // ✅ 버튼-텍스트 간격 최소화
        Spacer(modifier = Modifier.height(6.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Don't have an account? ")
            Text(
                modifier = Modifier.clickable { onSignUpClick() },
                text = "Sign up",
                color = MaterialTheme.colorScheme.primary
            )
        }

        // ✅ 혹시 너무 바닥에 붙는 게 불편하면 4~6dp 정도만 주면 됨
        Spacer(modifier = Modifier.height(2.dp))
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
                rememberId = true,
                onEmailChanged = {},
                onRememberIdChanged = {},
                onLoginClick = {},
                onSignUpClick = {}
            )
        }
    }
}
