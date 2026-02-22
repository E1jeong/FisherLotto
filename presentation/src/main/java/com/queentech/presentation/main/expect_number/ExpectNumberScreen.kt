package com.queentech.presentation.main.expect_number

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.queentech.presentation.theme.FisherLottoTheme
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ExpectNumberScreen(
    navController: NavHostController,
    viewModel: ExpectNumberViewModel = hiltViewModel()
) {
    val state by viewModel.container.stateFlow.collectAsState()
    val context = LocalContext.current

    InitExpectNumberScreen(
        context = context,
        navController = navController,
        viewModel = viewModel
    )

    ExpectNumberContent(
        expectNumber = state.expectNumber,
        onNumberIssueClick = viewModel::onExpectNumberClick
    )
}

@Composable
private fun InitExpectNumberScreen(
    context: Context,
    navController: NavHostController,
    viewModel: ExpectNumberViewModel
) {
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ExpectNumberSideEffect.Toast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
private fun ExpectNumberContent(
    expectNumber: List<String>,
    onNumberIssueClick: () -> Unit,
) {
    Text("ExpectNumber Screen $expectNumber")
    Button(
        onClick = onNumberIssueClick
    ) {
        Text("예상번호 발급")
    }
}

@Composable
@Preview
fun ExpectNumberScreenPreview() {
    FisherLottoTheme {
        Surface {
            // ExpectNumberContent()
        }
    }
}