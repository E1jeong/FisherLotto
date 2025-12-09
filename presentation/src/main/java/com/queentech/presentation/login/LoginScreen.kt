package com.queentech.presentation.login

import SocialLoginButton
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.queentech.presentation.R
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

    // 화면 최초 진입 시 자동로그인 체크
    LaunchedEffect(Unit) {
        viewModel.checkAutoLogin()
    }

    InitScreen(context = context, navController = navController, viewModel = viewModel)

    // 1. Google SignIn Client 준비 (remember로 재생성 방지)
    val googleSignInClient = remember(context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("534679949408-29ggudnci4tr0g6ir43kbcrh874e90v9.apps.googleusercontent.com")
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    // 2. ActivityResultLauncher 등록 (결과 처리는 ViewModel로 위임)
    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.onGoogleSignInActivityResult(result.data)
    }

    // 3. UI에 내려줄 콜백 정의
    val onGoogleLoginClick: () -> Unit = {
        val signInIntent = googleSignInClient.signInIntent
        googleLauncher.launch(signInIntent)
    }

    val onKakaoLoginClick: () -> Unit = {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            viewModel.onKakaoLoginResult(token, error)
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context, callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }
    }

    if (state.isCheckingAutoLogin) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LottieFishing(
                modifier = Modifier.fillMaxSize()
            )
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    } else {
        LoginContent(
            onGoogleLoginClick = onGoogleLoginClick,
            onKakaoLoginClick = onKakaoLoginClick,
            onSignUpClick = { viewModel.onSignUpClick() }
        )
    }
}

@Composable
private fun InitScreen(
    context: Context,
    navController: NavHostController,
    viewModel: LoginViewModel
) {
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

            else -> Unit
        }
    }
}

/**
 * 순수 UI를 담당하는 Composable
 * - Context, Google/Kakao SDK, NavController, ViewModel 모름
 * - 오직 콜백과 레이아웃만 신경 씀
 */
@Composable
private fun LoginContent(
    onGoogleLoginClick: () -> Unit,
    onKakaoLoginClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단 타이틀
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

        // 애니메이션
        LottieFishing()

        Spacer(modifier = Modifier.weight(1f))

        // 구글 로그인 버튼
        GoogleLoginButton(onClick = onGoogleLoginClick)

        Spacer(Modifier.height(16.dp))

        // 카카오 로그인 버튼
        KakaoLoginButton(onClick = onKakaoLoginClick)

        Spacer(Modifier.height(16.dp))

        // 회원가입 텍스트
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = Paddings.extra)
                .clickable(onClick = { /* Row 전체 클릭 시 별 행동 없으면 비워둬도 됨 */ })
        ) {
            Text(text = "Don't have an account? ")
            Text(
                modifier = Modifier.clickable {
                    onSignUpClick()
                },
                text = "Sign up",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun LottieFishing(
    modifier: Modifier = Modifier
) {
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

@Composable
fun GoogleLoginButton(
    onClick: () -> Unit
) {
    SocialLoginButton(
        text = "Google 계정으로 로그인",
        icon = painterResource(id = R.drawable.google_logo),
        backgroundColor = Color.White,
        textColor = Color(0xFF000000),
        borderColor = Color(0xFFDADCE0),
        onClick = onClick
    )
}

@Composable
fun KakaoLoginButton(
    onClick: () -> Unit
) {
    SocialLoginButton(
        text = "카카오로 로그인",
        icon = painterResource(id = R.drawable.kakao_logo),
        backgroundColor = Color(0xFFFEE500),
        textColor = Color(0xFF191919),
        borderColor = null,
        onClick = onClick
    )
}

@Composable
@Preview
fun LoginScreenPreview() {
    FisherLottoTheme {
        Surface {
            LoginContent(
                onGoogleLoginClick = {},
                onKakaoLoginClick = {},
                onSignUpClick = {}
            )
        }
    }
}
