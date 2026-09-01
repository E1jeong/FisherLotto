package com.queentech.presentation.component.dialog.wrapper

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.queentech.presentation.component.dialog.model.DialogTitle
import com.queentech.presentation.theme.AccentBlue
import com.queentech.presentation.theme.CardBg
import com.queentech.presentation.theme.Paddings
import com.queentech.presentation.theme.TextPrimary

@Composable
fun DialogTitleWrapper(title: DialogTitle) {
    when (title) {
        is DialogTitle.Default -> DefaultDialogTitle(title)
        is DialogTitle.Header -> HeaderDialogTitle(title)
    }
}

@Composable
fun DefaultDialogTitle(title: DialogTitle.Default) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBg)
            .padding(
                horizontal = Paddings.large,
                vertical = Paddings.extra
            ),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title.text,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displayMedium.copy(
                color = AccentBlue
            )
        )
    }
}

@Composable
fun HeaderDialogTitle(title: DialogTitle.Header) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AccentBlue)
            .padding(Paddings.large),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title.text,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displayMedium.copy(
                color = TextPrimary
            )
        )
    }
}
