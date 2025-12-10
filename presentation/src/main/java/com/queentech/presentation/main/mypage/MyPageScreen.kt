package com.queentech.presentation.main.mypage

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import com.queentech.presentation.login.LoginProvider
import com.queentech.presentation.login.LoginSideEffect
import com.queentech.presentation.login.LoginViewModel
import com.queentech.presentation.navigation.RouteName
import com.queentech.presentation.theme.FisherLottoTheme
import com.queentech.presentation.util.NavigationHelper
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun MyPageScreen(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    myPageViewModel: MyPageViewModel = hiltViewModel()
) {
    val loginState by loginViewModel.container.stateFlow.collectAsState()
    val myPageState by myPageViewModel.container.stateFlow.collectAsState()
    val context = LocalContext.current

    InitLoginScreen(
        context = context,
        navController = navController,
        loginViewModel = loginViewModel
    )

    InitMyPageScreen(
        context = context,
        myPageViewModel = myPageViewModel
    )

    MyPageContent(
        userEmail = loginState.userEmail,
        loginProvider = loginState.loginProvider,
        isLoading = myPageState.isLoading,
        paymentResultText = myPageState.result?.let { result ->
            // 결제 결과가 있을 때 보여줄 문자열
            "결제 결과: ${result.status} / 금액: ${result.amount}"
        } ?: "아직 결제 결과가 없습니다.",
        onLogoutClick = { loginViewModel.logout() },
        onTestPaymentClick = { myPageViewModel.onTestPaymentButtonClick() }, // 🔹 버튼 클릭
    )
}

@Composable
private fun InitLoginScreen(
    context: Context,
    navController: NavHostController,
    loginViewModel: LoginViewModel
) {
    loginViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is LoginSideEffect.Toast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            is LoginSideEffect.NavigateToLogin -> {
                NavigationHelper.navigateToLoginAfterLogout(navController, RouteName.LOGIN)
            }

            else -> Unit
        }
    }
}

@Composable
private fun InitMyPageScreen(
    context: Context,
    myPageViewModel: MyPageViewModel
) {
    myPageViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MyPageSideEffect.Toast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
private fun MyPageContent(
    userEmail: String?,
    loginProvider: LoginProvider?,
    isLoading: Boolean,
    paymentResultText: String,
    onLogoutClick: () -> Unit,
    onTestPaymentClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "My Page",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Login Provider: ${loginProvider?.name ?: "Unknown"}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Email: ${userEmail ?: "-"}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(24.dp))

        // 🔹 결제 테스트 버튼
        Button(
            onClick = onTestPaymentClick,
            enabled = !isLoading,
        ) {
            Text(text = if (isLoading) "결제 진행중..." else "테스트 결제 요청")
        }

        Spacer(Modifier.height(16.dp))

        // 🔹 결제 결과 표시
        Text(
            text = paymentResultText,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onLogoutClick,
        ) {
            Text(text = "로그아웃")
        }
    }
}

@Composable
@Preview
fun MyPageScreenPreview() {
    FisherLottoTheme {
        Surface {
            MyPageContent(
                userEmail = "user@example.com",
                loginProvider = LoginProvider.GOOGLE,
                isLoading = false,
                paymentResultText = "아직 결제 결과가 없습니다.",
                onLogoutClick = {},
                onTestPaymentClick = {},
            )
        }
    }
}