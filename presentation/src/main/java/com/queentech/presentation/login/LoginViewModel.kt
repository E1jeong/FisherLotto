package com.queentech.presentation.login

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) : ViewModel(), ContainerHost<LoginState, LoginSideEffect> {

    override val container: Container<LoginState, LoginSideEffect> = container(
        initialState = LoginState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                intent {
                    postSideEffect(LoginSideEffect.Toast(throwable.message.toString()))
                    Log.e(TAG, "error handler: ${throwable.message}", throwable)
                }
            }
        },
        onCreate = {},
    )

    fun onSignUpClick() = intent {
        postSideEffect(LoginSideEffect.NavigateToSignUp)
    }


    //LoginScreen 진입 시 자동 로그인 체크
    fun checkAutoLogin() = intent {
        // 로딩 시작
        reduce { state.copy(isCheckingAutoLogin = true) }

        // 1) Google 자동 로그인 여부
        val googleAccount = GoogleSignIn.getLastSignedInAccount(context)
        if (googleAccount != null) {
            Log.d(TAG, "자동 로그인(Google): ${googleAccount.email}")
            // 공통 성공 처리 재사용
            onGoogleLoginSuccess(googleAccount)
            return@intent
        }

        // 2) Kakao 자동 로그인 여부
        if (AuthApiClient.instance.hasToken()) {
            UserApiClient.instance.accessTokenInfo { _, error ->
                if (error != null) {
                    Log.w(TAG, "카카오 토큰 유효하지 않음 or 에러: $error")
                    intent {
                        reduce { state.copy(isCheckingAutoLogin = false) }
                    }
                } else {
                    UserApiClient.instance.me { user, meError ->
                        if (meError != null || user == null) {
                            Log.e(TAG, "카카오 사용자 정보 조회 실패: $meError")
                            intent {
                                reduce { state.copy(isCheckingAutoLogin = false) }
                            }
                        } else {
                            val email = user.kakaoAccount?.email
                            val kakaoUserId = user.id

                            if (kakaoUserId != null) {
                                onKakaoLoginSuccess(kakaoUserId, email)
                                Log.d(TAG, "자동 로그인(Kakao) 사용자: id=$kakaoUserId, email=$email")
                            }
                        }
                    }
                }
            }
        } else {
            // 둘 다 자동로그인 불가 → 로그인 화면 노출
            reduce { state.copy(isCheckingAutoLogin = false) }
        }
    }

    /**
     * Google ActivityResult 처리: Screen에서는 Intent만 넘겨주고,
     * 실제 account 파싱/에러 처리는 ViewModel이 담당.
     */
    fun onGoogleSignInActivityResult(data: Intent?) = intent {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            Log.d(TAG, "구글 로그인 성공: ${account.email}, 토큰: ${account.idToken}")
            onGoogleLoginSuccess(account)
        } catch (e: ApiException) {
            Log.e(TAG, "구글 로그인 실패: ${e.statusCode}", e)
            postSideEffect(LoginSideEffect.Toast("Google 로그인 실패 (${e.statusCode})"))
        }
    }

    /**
     * Kakao 로그인 결과 처리: Screen에서는 Kakao SDK의 callback만 넘겨줌.
     */
    fun onKakaoLoginResult(token: OAuthToken?, error: Throwable?) {
        if (error != null) {
            Log.e(TAG, "카카오 로그인 실패", error)
            intent {
                postSideEffect(LoginSideEffect.Toast("Kakao 로그인 실패"))
            }
            return
        }

        if (token == null) {
            intent {
                postSideEffect(LoginSideEffect.Toast("Kakao 토큰이 없습니다."))
            }
            return
        }

        Log.d(TAG, "카카오 로그인 성공, 토큰: ${token.accessToken}")

        UserApiClient.instance.me { user, meError ->
            if (meError != null) {
                Log.e(TAG, "카카오 사용자 정보 요청 실패", meError)
                intent {
                    postSideEffect(LoginSideEffect.Toast("Kakao 사용자 정보 요청 실패"))
                }
            } else if (user != null) {
                val email = user.kakaoAccount?.email
                val kakaoUserId = user.id

                if (kakaoUserId != null) {
                    onKakaoLoginSuccess(kakaoUserId, email)
                    Log.d(TAG, "카카오 사용자 정보: id=$kakaoUserId, email=$email")
                }
            }
        }
    }

    // 구글 로그인 공통 성공 처리 (자동로그인 + 버튼 로그인 공용)
    private fun onGoogleLoginSuccess(account: GoogleSignInAccount) = intent {
        reduce {
            state.copy(
                userEmail = account.email,
                loginProvider = LoginProvider.GOOGLE,
                providerUserId = account.id,
                isLoggedIn = true,
                isCheckingAutoLogin = false,
            )
        }
        postSideEffect(LoginSideEffect.NavigateToInformation)
    }

    // 카카오 로그인 공통 성공 처리 (자동로그인 + 버튼 로그인 공용)
    private fun onKakaoLoginSuccess(userId: Long, email: String?) = intent {
        reduce {
            state.copy(
                userEmail = email,
                loginProvider = LoginProvider.KAKAO,
                providerUserId = userId.toString(),
                isLoggedIn = true,
                isCheckingAutoLogin = false,
            )
        }
        postSideEffect(LoginSideEffect.NavigateToInformation)
    }

    companion object {
        const val TAG = "LoginViewModel"
    }
}

enum class LoginProvider {
    GOOGLE,
    KAKAO
}

@Immutable
data class LoginState(
    val userEmail: String? = null,
    val loginProvider: LoginProvider? = null,
    val providerUserId: String? = null, // SNS에서 받은 고유 ID
    val isLoggedIn: Boolean = false,
    val isCheckingAutoLogin: Boolean = true, // 화면 진입 시 자동로그인 체크 중인지
)

sealed interface LoginSideEffect {
    data class Toast(val message: String) : LoginSideEffect
    data object NavigateToSignUp : LoginSideEffect
    data object NavigateToInformation : LoginSideEffect
}
