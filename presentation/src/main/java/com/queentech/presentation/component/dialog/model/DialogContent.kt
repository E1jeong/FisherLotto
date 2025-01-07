package com.queentech.presentation.component.dialog.model

sealed class DialogContent {
    data class Default(
        val dialogText: DialogText.Default
    ) : DialogContent()

    data class Large(
        val dialogText: DialogText.Default
    ) : DialogContent()

    data class Left(
        val dialogText: DialogText.Default
    ) : DialogContent()
}