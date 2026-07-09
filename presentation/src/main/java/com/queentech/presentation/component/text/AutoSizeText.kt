package com.queentech.presentation.component.text

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * 주어진 폭에 텍스트가 넘치면(overflow) 한 줄을 유지한 채 [minFontSize]까지 폰트 크기를
 * 단계적으로 줄여서 표시한다. 컬럼 폭이 고정된 표에서 자릿수가 늘어나거나 화면이
 * 좁아져도(다른 기기 등) 줄바꿈/잘림 없이 한 줄로 맞추기 위한 범용 컴포넌트.
 */
@Composable
fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = 13.sp,
    minFontSize: TextUnit = 9.sp,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
) {
    var scaledFontSize by remember(text, fontSize) { mutableStateOf(fontSize) }
    var readyToDraw by remember(text, fontSize) { mutableStateOf(false) }

    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .drawWithContent { if (readyToDraw) drawContent() },
        color = color,
        fontSize = scaledFontSize,
        fontWeight = fontWeight,
        textAlign = textAlign,
        maxLines = 1,
        softWrap = false,
        onTextLayout = { result ->
            if (result.didOverflowWidth && scaledFontSize > minFontSize) {
                scaledFontSize = (scaledFontSize.value - 1).sp
            } else {
                readyToDraw = true
            }
        }
    )
}
