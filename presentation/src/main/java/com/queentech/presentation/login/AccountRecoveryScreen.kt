package com.queentech.presentation.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.queentech.presentation.component.textfield.DefaultTextField
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun AccountRecoveryScreen(
    moveToHome: () -> Unit,
    viewModel: AccountRecoveryViewModel = hiltViewModel(),
) {
    val state by viewModel.container.stateFlow.collectAsState()
    val context = LocalContext.current
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var verificationCode by rememberSaveable { mutableStateOf("") }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is AccountRecoverySideEffect.Toast -> Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            AccountRecoverySideEffect.NavigateToHome -> moveToHome()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Button(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp).height(48.dp),
                enabled = !state.isRecovering && state.verificationStep == EmailVerificationStep.VERIFIED && phone.length >= 10,
                onClick = { viewModel.onRecoverClick(email, phone) },
                colors = ButtonDefaults.buttonColors(),
            ) {
                Text(if (state.isRecovering) "복구 중..." else "계정 복구")
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
                .padding(innerPadding).padding(horizontal = 24.dp, vertical = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("계정 복구", style = MaterialTheme.typography.displayMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("이메일 인증과 전화번호 확인 후 기존 계정을 복구합니다.", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(28.dp))
            DefaultTextField(
                modifier = Modifier.fillMaxWidth(), value = phone, placeholder = "핸드폰번호 (01012124545)",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                onValueChange = { phone = it.filter(Char::isDigit).take(11) },
            )
            Spacer(modifier = Modifier.height(10.dp))
            DefaultTextField(
                modifier = Modifier.fillMaxWidth(), value = email, placeholder = "Email",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                onValueChange = { email = it; verificationCode = ""; viewModel.onEmailChanged(it) },
            )
            Spacer(modifier = Modifier.height(10.dp))
            RecoveryVerificationContent(
                state = state,
                verificationCode = verificationCode,
                onVerificationCodeChanged = { verificationCode = it },
                onSendClick = { verificationCode = ""; viewModel.onSendVerificationCode(email) },
                onVerifyClick = { viewModel.onVerifyEmailCode(email, verificationCode) },
            )
        }
    }
}

@Composable
private fun RecoveryVerificationContent(
    state: AccountRecoveryState,
    verificationCode: String,
    onVerificationCodeChanged: (String) -> Unit,
    onSendClick: () -> Unit,
    onVerifyClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        when (state.verificationStep) {
            EmailVerificationStep.IDLE, EmailVerificationStep.SENDING -> Button(
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = state.verificationStep != EmailVerificationStep.SENDING,
                onClick = onSendClick,
            ) { Text(if (state.verificationStep == EmailVerificationStep.SENDING) "발송 중..." else "인증번호 받기") }
            EmailVerificationStep.CODE_SENT, EmailVerificationStep.VERIFYING, EmailVerificationStep.EXPIRED -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DefaultTextField(
                        modifier = Modifier.weight(1f),
                        enabled = state.verificationStep != EmailVerificationStep.EXPIRED,
                        value = verificationCode, placeholder = "인증번호 6자리",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        onValueChange = { onVerificationCodeChanged(it.filter(Char::isDigit).take(6)) },
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        modifier = Modifier.height(48.dp),
                        enabled = verificationCode.length == 6 && state.verificationStep == EmailVerificationStep.CODE_SENT,
                        onClick = onVerifyClick,
                    ) { Text(if (state.verificationStep == EmailVerificationStep.VERIFYING) "확인 중..." else "확인") }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(formatRecoveryRemainingTime(state.remainingSeconds), style = MaterialTheme.typography.bodySmall)
                    TextButton(enabled = state.verificationStep != EmailVerificationStep.VERIFYING, onClick = onSendClick) { Text("재발송") }
                }
            }
            EmailVerificationStep.VERIFIED -> Text("✓ 이메일 인증이 완료되었습니다.", color = MaterialTheme.colorScheme.secondary)
        }
        state.verificationError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
    }
}

private fun formatRecoveryRemainingTime(totalSeconds: Int): String = "%02d:%02d".format(totalSeconds / 60, totalSeconds % 60)
