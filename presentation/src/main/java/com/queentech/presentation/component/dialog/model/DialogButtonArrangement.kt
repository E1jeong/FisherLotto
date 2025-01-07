package com.queentech.presentation.component.dialog.model

sealed class DialogButtonArrangement {
    data class Column(
        val buttons: List<DialogButton>? = null
    ) : DialogButtonArrangement()

    data class Row(
        val buttons: List<DialogButton>? = null
    ) : DialogButtonArrangement()
}