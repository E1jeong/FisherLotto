package com.queentech.presentation.component.textfield

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.queentech.presentation.theme.CardBg
import com.queentech.presentation.theme.TextPrimary
import com.queentech.presentation.theme.TextSecondary

@Composable
fun DefaultTextField(
    modifier: Modifier,
    enabled: Boolean = true,
    value: String,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChange: (String) -> Unit
) {
    TextField(
        modifier = modifier,
        enabled = enabled,
        value = value,
        placeholder = {
            Text(
                modifier = Modifier.alpha(0.5f),
                text = placeholder,
                color = TextSecondary
            )
        },
        onValueChange = onValueChange,
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            disabledTextColor = TextSecondary,
            focusedContainerColor = CardBg,
            unfocusedContainerColor = CardBg,
            disabledContainerColor = CardBg.copy(alpha = 0.6f),
            cursorColor = TextPrimary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedPlaceholderColor = TextSecondary,
            unfocusedPlaceholderColor = TextSecondary,
        ),
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation
    )
}