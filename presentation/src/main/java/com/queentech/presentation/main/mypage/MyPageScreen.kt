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
    loginViewModel: LoginViewModel
) {
    val state by loginViewModel.container.stateFlow.collectAsState()
    val context = LocalContext.current

    InitScreen(
        context = context,
        navController = navController,
        loginViewModel = loginViewModel
    )

    MyPageContent(
        userEmail = state.userEmail,
        loginProvider = state.loginProvider,
        onLogoutClick = { loginViewModel.logout() }
    )
}

@Composable
private fun InitScreen(
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
private fun MyPageContent(
    userEmail: String?,
    loginProvider: LoginProvider?,
    onLogoutClick: () -> Unit
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
                onLogoutClick = {}
            )
        }
    }
}