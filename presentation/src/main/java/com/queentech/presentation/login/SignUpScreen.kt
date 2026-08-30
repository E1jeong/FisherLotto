package com.queentech.presentation.login

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.queentech.presentation.component.textfield.DefaultTextField
import com.queentech.presentation.theme.FisherLottoTheme
import com.queentech.presentation.theme.Paddings
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SignUpScreen(
    popBackStack: () -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val state by viewModel.container.stateFlow.collectAsState()
    val context = LocalContext.current

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var birth by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var verificationCode by rememberSaveable { mutableStateOf("") }

    BackHandler(enabled = state.isSignUpComplete) {}

    InitSignUpScreen(
        context = context,
        popBackStack = popBackStack,
        viewModel = viewModel
    )

    Box(modifier = Modifier.fillMaxSize()) {
        SignUpContent(
            state = state,
            name = name,
            email = email,
            birth = birth,
            phone = phone,
            verificationCode = verificationCode,
            onNameChanged = { name = it },
            onEmailChanged = {
                email = it
                verificationCode = ""
                viewModel.onEmailChanged(it)
            },
            onBirthChanged = { birth = it },
            onPhoneChanged = { phone = it },
            onVerificationCodeChanged = { verificationCode = it },
            onSendVerificationCodeClick = {
                verificationCode = ""
                viewModel.onSendVerificationCode(email)
            },
            onVerifyEmailCodeClick = {
                viewModel.onVerifyEmailCode(email, verificationCode)
            },
            onSubmitClick = { viewModel.onSignUpSubmitClick(name, email, birth, phone) },
        )

        // 회원가입 완료 로딩 오버레이
        if (state.isSignUpComplete) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "회원가입 완료! 로그인 화면으로 이동합니다...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Composable
private fun InitSignUpScreen(
    context: Context,
    popBackStack: () -> Unit,
    viewModel: SignUpViewModel
) {
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SignUpSideEffect.Toast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            is SignUpSideEffect.SignUpDoneNavigateToLogin -> popBackStack()
        }
    }
}

@Composable
private fun SignUpContent(
    state: SignUpState,
    name: String,
    email: String,
    birth: String,
    phone: String,
    verificationCode: String,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onBirthChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onVerificationCodeChanged: (String) -> Unit,
    onSendVerificationCodeClick: () -> Unit,
    onVerifyEmailCodeClick: () -> Unit,
    onSubmitClick: () -> Unit,
) {
    val contentPadding = 24.dp
    val fullWidth = Modifier
        .fillMaxWidth()
        .padding(horizontal = contentPadding)

    val enableSubmit =
        name.isNotBlank() &&
            email.isNotBlank() &&
            birth.length == 8 &&
            phone.length >= 10 &&
            state.emailVerificationStep == EmailVerificationStep.VERIFIED &&
            !state.isSigningUp

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            SignUpBottomBar(
                modifier = fullWidth,
                enableSubmit = enableSubmit,
                isSigningUp = state.isSigningUp,
                onSubmitClick = onSubmitClick,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(top = Paddings.xextra)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "회원가입",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "계정을 생성하세요",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(28.dp))

            DefaultTextField(
                modifier = fullWidth,
                value = name,
                placeholder = "이름",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                onValueChange = onNameChanged
            )

            Spacer(modifier = Modifier.height(10.dp))

            DefaultTextField(
                modifier = fullWidth,
                value = email,
                placeholder = "Email",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                onValueChange = onEmailChanged
            )

            Spacer(modifier = Modifier.height(10.dp))

            EmailVerificationContent(
                modifier = fullWidth,
                state = state,
                verificationCode = verificationCode,
                onVerificationCodeChanged = onVerificationCodeChanged,
                onSendVerificationCodeClick = onSendVerificationCodeClick,
                onVerifyEmailCodeClick = onVerifyEmailCodeClick,
            )

            Spacer(modifier = Modifier.height(10.dp))

            DefaultTextField(
                modifier = fullWidth,
                value = birth,
                placeholder = "생일 (19700101)",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                onValueChange = { input ->
                    onBirthChanged(input.filter { it.isDigit() }.take(8))
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            DefaultTextField(
                modifier = fullWidth,
                value = phone,
                placeholder = "핸드폰번호 (01012124545)",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                onValueChange = { input ->
                    onPhoneChanged(input.filter { it.isDigit() }.take(11))
                }
            )
        }
    }
}

@Composable
private fun EmailVerificationContent(
    modifier: Modifier,
    state: SignUpState,
    verificationCode: String,
    onVerificationCodeChanged: (String) -> Unit,
    onSendVerificationCodeClick: () -> Unit,
    onVerifyEmailCodeClick: () -> Unit,
) {
    Column(modifier = modifier) {
        when (state.emailVerificationStep) {
            EmailVerificationStep.IDLE,
            EmailVerificationStep.SENDING,
            -> Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = state.emailVerificationStep != EmailVerificationStep.SENDING,
                onClick = onSendVerificationCodeClick,
            ) {
                Text(
                    if (state.emailVerificationStep == EmailVerificationStep.SENDING) {
                        "발송 중..."
                    } else {
                        "인증번호 받기"
                    }
                )
            }

            EmailVerificationStep.CODE_SENT,
            EmailVerificationStep.VERIFYING,
            EmailVerificationStep.EXPIRED,
            -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DefaultTextField(
                        modifier = Modifier.weight(1f),
                        enabled = state.emailVerificationStep != EmailVerificationStep.EXPIRED,
                        value = verificationCode,
                        placeholder = "인증번호 6자리",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done,
                        ),
                        onValueChange = { input ->
                            onVerificationCodeChanged(input.filter { it.isDigit() }.take(6))
                        },
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        modifier = Modifier.height(48.dp),
                        enabled = verificationCode.length == 6 &&
                            state.emailVerificationStep == EmailVerificationStep.CODE_SENT,
                        onClick = onVerifyEmailCodeClick,
                    ) {
                        Text(
                            if (state.emailVerificationStep == EmailVerificationStep.VERIFYING) {
                                "확인 중..."
                            } else {
                                "확인"
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = formatRemainingTime(state.remainingSeconds),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    TextButton(
                        enabled = state.emailVerificationStep != EmailVerificationStep.VERIFYING,
                        onClick = onSendVerificationCodeClick,
                    ) {
                        Text("재발송")
                    }
                }
            }

            EmailVerificationStep.VERIFIED -> Text(
                text = "✓ 이메일 인증이 완료되었습니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
        }

        state.verificationError?.let { error ->
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

private fun formatRemainingTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

@Composable
private fun SignUpBottomBar(
    modifier: Modifier,
    enableSubmit: Boolean,
    isSigningUp: Boolean,
    onSubmitClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            modifier = modifier.height(48.dp),
            onClick = onSubmitClick,
            enabled = enableSubmit,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Text(if (isSigningUp) "가입 중..." else "가입하기")
        }

        Spacer(modifier = Modifier.height(6.dp))
    }
}

@Preview
@Composable
fun SignUpScreenPreview() {
    FisherLottoTheme {
        Surface {
            SignUpContent(
                state = SignUpState(),
                name = "",
                email = "",
                birth = "",
                phone = "",
                verificationCode = "",
                onNameChanged = {},
                onEmailChanged = {},
                onBirthChanged = {},
                onPhoneChanged = {},
                onVerificationCodeChanged = {},
                onSendVerificationCodeClick = {},
                onVerifyEmailCodeClick = {},
                onSubmitClick = {},
            )
        }
    }
}
