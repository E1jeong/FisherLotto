package com.queentech.presentation.component.dialog.wrapper

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.queentech.presentation.component.button.PrimaryButton
import com.queentech.presentation.component.button.SecondaryBorderlessButton
import com.queentech.presentation.component.button.SecondaryButton
import com.queentech.presentation.component.button.UnderlinedTextButton
import com.queentech.presentation.component.dialog.model.DialogButton
import com.queentech.presentation.component.dialog.model.DialogButtonArrangement
import com.queentech.presentation.component.icon.LeadingIcon
import com.queentech.presentation.theme.Paddings

@Composable
fun DialogButtonWrapper(arrangement: DialogButtonArrangement) {
    when (arrangement) {
        is DialogButtonArrangement.Column -> DialogButtonsColumn(arrangement.buttons)
        is DialogButtonArrangement.Row -> DialogButtonsRow(arrangement.buttons)
    }
}


@Composable
fun DialogButtonsColumn(
    buttons: List<DialogButton>?
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        buttons?.forEachIndexed { index, dialogButton ->
            if (index > 0) {
                Spacer(modifier = Modifier.height(Paddings.large))
            }
            when (dialogButton) {
                is DialogButton.Primary -> {
                    PrimaryButton(
                        text = dialogButton.title,
                        leadingIcon = dialogButton.leadingIcon?.let {
                            LeadingIcon(
                                icon = it.icon,
                                iconContentDescription = it.iconContentDescription ?: ""
                            )
                        }
                    ) { dialogButton.action?.invoke() }
                }

                is DialogButton.Secondary -> {
                    SecondaryButton(
                        text = dialogButton.title
                    ) { dialogButton.action?.invoke() }
                }

                is DialogButton.SecondaryBorderless -> {
                    SecondaryBorderlessButton(
                        text = dialogButton.title
                    ) { dialogButton.action?.invoke() }
                }

                is DialogButton.UnderlinedText -> {
                    UnderlinedTextButton(
                        text = dialogButton.title
                    ) { dialogButton.action?.invoke() }
                }
            }
        }
    }
}

@Composable
fun DialogButtonsRow(
    buttons: List<DialogButton>?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        buttons?.forEachIndexed { index, dialogButton ->
            if (index > 0) {
                Spacer(modifier = Modifier.width(Paddings.large))
            }
            when (dialogButton) {
                is DialogButton.Primary -> {
                    PrimaryButton(
                        modifier = Modifier.weight(1f),
                        text = dialogButton.title,
                        leadingIcon = dialogButton.leadingIcon?.let {
                            LeadingIcon(
                                icon = it.icon,
                                iconContentDescription = it.iconContentDescription ?: ""
                            )
                        }
                    ) { dialogButton.action?.invoke() }
                }

                is DialogButton.Secondary -> {
                    SecondaryButton(
                        modifier = Modifier.weight(1f),
                        text = dialogButton.title
                    ) { dialogButton.action?.invoke() }
                }

                is DialogButton.SecondaryBorderless -> {
                    SecondaryBorderlessButton(
                        modifier = Modifier.weight(1f),
                        text = dialogButton.title
                    ) { dialogButton.action?.invoke() }
                }

                is DialogButton.UnderlinedText -> {
                    UnderlinedTextButton(
                        modifier = Modifier.weight(1f),
                        text = dialogButton.title
                    ) { dialogButton.action?.invoke() }
                }
            }
        }
    }
}
