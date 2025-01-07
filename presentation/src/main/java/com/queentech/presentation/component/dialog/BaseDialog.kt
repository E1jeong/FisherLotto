package com.queentech.presentation.component.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.queentech.presentation.component.dialog.model.DialogButton
import com.queentech.presentation.component.dialog.model.DialogButtonArrangement
import com.queentech.presentation.component.dialog.model.DialogContent
import com.queentech.presentation.component.dialog.model.DialogText
import com.queentech.presentation.component.dialog.model.DialogTitle
import com.queentech.presentation.component.dialog.wrapper.DialogButtonWrapper
import com.queentech.presentation.component.dialog.wrapper.DialogButtonsColumn
import com.queentech.presentation.component.dialog.wrapper.DialogContentWrapper
import com.queentech.presentation.component.dialog.wrapper.DialogTitleWrapper
import com.queentech.presentation.theme.FisherLottoTheme
import com.queentech.presentation.theme.Paddings

@Composable
fun BaseDialog(
    dialogTitle: DialogTitle? = null,
    dialogContent: DialogContent? = null,
    buttons: List<DialogButton>? = null,
    buttonArrangement: DialogButtonArrangement? = null,
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = screenHeight * 0.8f),
        elevation = CardDefaults.cardElevation(defaultElevation = Paddings.none),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            dialogTitle?.let {
                DialogTitleWrapper(it)
            }
            Column(
                modifier = Modifier
                    .background(Color.Transparent)
                    .fillMaxWidth()
                    .padding(Paddings.xlarge)
                    .verticalScroll(rememberScrollState())
            ) {
                dialogContent?.let { DialogContentWrapper(it) }
                buttons?.let { DialogButtonsColumn(it) }
                buttonArrangement?.let { DialogButtonWrapper(it) }
            }
        }
    }
}

@Preview
@Composable
fun BaseDialogPopupPreview() {
    FisherLottoTheme {
        BaseDialog(
            dialogTitle = DialogTitle.Header("TITLE"),
            dialogContent = DialogContent.Large(
                DialogText.Default("abcde abcde abcde abcde abcde abcde abcde abcde abcde abcde abcde abcde abcde abcde")
            ),
            buttons = listOf(
                DialogButton.Primary("Okay") {}
            ),
            buttonArrangement = DialogButtonArrangement.Row(
                listOf(
                    DialogButton.Primary("Okay") {},
                    DialogButton.Primary("Cancel") {}
                )
            )
        )
    }
}

@Preview
@Composable
fun BaseDialogPopupPreview3() {
    FisherLottoTheme {
        BaseDialog(
            dialogTitle = DialogTitle.Default("TITLE"),
            dialogContent = DialogContent.Default(
                DialogText.Default("title")
            ),
            buttons = listOf(
                DialogButton.Primary("Okay") {},
                DialogButton.Secondary("Submit") {},
                DialogButton.SecondaryBorderless("zz") {},
                DialogButton.UnderlinedText("hh") {},
            )
        )
    }
}
