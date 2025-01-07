package com.queentech.presentation.component.dialog.wrapper

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.em
import com.queentech.presentation.component.dialog.model.DialogContent
import com.queentech.presentation.component.dialog.model.DialogText
import com.queentech.presentation.theme.Paddings

data class DialogContentStyle(
    val textStyle: @Composable () -> TextStyle = { MaterialTheme.typography.bodySmall },
    val contentTopPadding: Dp = Paddings.xlarge,
    val contentBottomPadding: Dp = Paddings.extra
)

val LocalDialogContentStyle = compositionLocalOf { DialogContentStyle() }

@Composable
fun ColumnScope.DialogContentWrapper(content: DialogContent) {
    when (content) {
        is DialogContent.Default -> {
            CompositionLocalProvider(
                LocalDialogContentStyle provides DialogContentStyle(
                    textStyle = {
                        MaterialTheme.typography.titleMedium.copy(
                            lineHeight = 1.6.em
                        )
                    },
                    contentTopPadding = Paddings.small,
                    contentBottomPadding = Paddings.extra
                )
            ) {
                NormalTextContent(content.dialogText)
            }
        }

        is DialogContent.Large -> {
            CompositionLocalProvider(
                LocalDialogContentStyle provides DialogContentStyle(
                    textStyle = {
                        MaterialTheme.typography.headlineSmall.copy(
                            lineHeight = 1.6.em
                        )
                    },
                    contentTopPadding = Paddings.extra,
                    contentBottomPadding = Paddings.extra
                )
            ) {
                NormalTextContent(content.dialogText)
            }
        }

        is DialogContent.Left -> {
            CompositionLocalProvider(
                LocalDialogContentStyle provides DialogContentStyle(
                    textStyle = { MaterialTheme.typography.titleMedium.copy(lineHeight = 1.6.em) },
                    contentTopPadding = Paddings.small,
                    contentBottomPadding = Paddings.extra
                )
            ) {
                LeftTextContent(content.dialogText)
            }
        }
    }
}

@Composable
fun ColumnScope.NormalTextContent(text: DialogText.Default) {
    Text(
        text = text.text ?: "",
        modifier = Modifier
            .padding(
                top = LocalDialogContentStyle.current.contentTopPadding,
                bottom = LocalDialogContentStyle.current.contentBottomPadding
            )
            .align(Alignment.CenterHorizontally),
        textAlign = TextAlign.Center,
        style = LocalDialogContentStyle.current.textStyle.invoke()
    )
}

@Composable
fun ColumnScope.LeftTextContent(text: DialogText.Default) {
    Text(
        text = text.text ?: "",
        modifier = Modifier
            .padding(
                top = LocalDialogContentStyle.current.contentTopPadding,
                bottom = LocalDialogContentStyle.current.contentBottomPadding
            )
            .align(Alignment.CenterHorizontally),
        textAlign = TextAlign.Start,
        style = LocalDialogContentStyle.current.textStyle.invoke()
    )
}