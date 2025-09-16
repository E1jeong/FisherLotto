package com.queentech.presentation

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test

class InformationScreenTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun afterLoadingShowResult() {
        rule.setContent {
            // 예시: 실제 화면 대신 테스트용 최소 UI
            var loading by remember { mutableStateOf(true) }
            LaunchedEffect(Unit) { loading = false } // 테스트를 위해 즉시 로딩 종료
            if (loading) CircularProgressIndicator(Modifier.testTag("loading"))
            else Text("OK", Modifier.testTag("result_item"))
        }

        rule.onNodeWithTag("loading").assertExists()
        rule.onNodeWithTag("result_item").assertExists()
    }
}