package com.queentech.presentation.login

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.queentech.presentation.theme.FisherLottoTheme

@Composable
fun SignUpScreen(
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // 1. Google SignIn Client 준비
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("534679949408-29ggudnci4tr0g6ir43kbcrh874e90v9.apps.googleusercontent.com") // 👈 원정님이 직접 입력
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)


    // 2. ActivityResultLauncher 등록
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken   // ✅ 여기서 토큰을 꺼내옴
            Log.d("GoogleLogin", "구글 로그인 성공: ${account.email}, 토큰: $idToken")
        } catch (e: ApiException) {
            Log.e("GoogleLogin", "구글 로그인 실패: ${e.statusCode}")
        }
    }

    // 3. UI
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val signInIntent = googleSignInClient.signInIntent
                launcher.launch(signInIntent)
            }
        ) {
            Text("구글 로그인")
        }
    }
}

@Composable
@Preview
fun SignUpScreenPreview() {
    FisherLottoTheme {
        Surface {
            SignUpScreen()
        }
    }
}