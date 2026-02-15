package com.queentech.presentation.login

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.queentech.presentation.component.textfield.DefaultTextField
import com.queentech.presentation.theme.FisherLottoTheme
import com.queentech.presentation.theme.Paddings
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SignUpScreen(
    navController: NavHostController,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val state by viewModel.container.stateFlow.collectAsState()
    val context = LocalContext.current

    InitSignUpScreen(
        context = context,
        navController = navController,
        viewModel = viewModel
    )

    SignUpContent(
        name = state.name,
        email = state.email,
        birth = state.birth,
        phone = state.phone,
        onNameChanged = viewModel::onSignUpNameChanged,
        onEmailChanged = viewModel::onSignUpEmailChanged,
        onBirthChanged = viewModel::onSignUpBirthChanged,
        onPhoneChanged = viewModel::onSignUpPhoneChanged,
        onSubmitClick = viewModel::onSignUpSubmitClick,
    )
}

@Composable
private fun InitSignUpScreen(
    context: Context,
    navController: NavHostController,
    viewModel: SignUpViewModel
) {
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SignUpSideEffect.Toast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            is SignUpSideEffect.SignUpDoneNavigateToLogin -> {
                navController.popBackStack()
            }
        }
    }
}

@Composable
private fun SignUpContent(
    name: String,
    email: String,
    birth: String,
    phone: String,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onBirthChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onSubmitClick: () -> Unit,
) {
    val contentPadding = 24.dp
    val fullWidth = Modifier
        .fillMaxWidth()
        .padding(horizontal = contentPadding)

    val enableSubmit =
        name.isNotBlank() && email.isNotBlank() && birth.length == 8 && phone.length >= 10

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            SignUpBottomBar(
                fullWidth = fullWidth,
                enableSubmit = enableSubmit,
                onSubmitClick = onSubmitClick,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = Paddings.xextra),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sign Up",
                style = MaterialTheme.typography.displaySmall
            )
            Text(
                text = "Create your account",
                style = MaterialTheme.typography.labelSmall
            )

            Spacer(modifier = Modifier.height(28.dp))

            DefaultTextField(
                modifier = fullWidth,
                value = name,
                placeholder = "Name",
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

            DefaultTextField(
                modifier = fullWidth,
                value = birth,
                placeholder = "Birth (YYYYMMDD)",
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
                placeholder = "Phone (digits only)",
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
private fun SignUpBottomBar(
    fullWidth: Modifier,
    enableSubmit: Boolean,
    onSubmitClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            modifier = fullWidth,
            onClick = onSubmitClick,
            enabled = enableSubmit
        ) {
            Text("가입하기")
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
                name = "",
                email = "",
                birth = "",
                phone = "",
                onNameChanged = {},
                onEmailChanged = {},
                onBirthChanged = {},
                onPhoneChanged = {},
                onSubmitClick = {},
            )
        }
    }
}
