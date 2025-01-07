package com.queentech.presentation.component.dialog.model

sealed class DialogText {
    abstract var text: String?

    class Default() : DialogText() {
        override var text: String? = null

        constructor(text: String) : this() {
            this.text = text
        }
    }
}