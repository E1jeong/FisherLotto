package com.queentech.presentation.main.camera

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import com.queentech.presentation.component.dialog.BaseDialog
import com.queentech.presentation.component.dialog.model.DialogContent
import com.queentech.presentation.component.dialog.model.DialogText
import com.queentech.presentation.component.dialog.model.DialogTitle

@Composable
fun QrResultDialog(
    visible: Boolean = false,
    qrCodeValue: String,
    onDismissRequest: () -> Unit,
) {
    if (visible) {
        Dialog(onDismissRequest = onDismissRequest) {
            BaseDialog(
                dialogTitle = DialogTitle.Header(text = "당첨 확인"),
                dialogContent = DialogContent.Default(DialogText.Default(qrCodeValue)),
            )
        }
    }
}