package com.li_routi.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.foundation.color.Neutral96
import com.li_routi.core.designsystem.foundation.typography.Pretendard
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

enum class LiroutiToastStyle {
    Black,
    Gray,
}

private val MessageTextStyle = TextStyle(
    fontFamily = Pretendard,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 22.sp,
    letterSpacing = (-0.025f).em,
)

@Composable
fun LiroutiToast(
    message: String,
    modifier: Modifier = Modifier,
    style: LiroutiToastStyle = LiroutiToastStyle.Black,
    onCloseClick: (() -> Unit)? = null,
) {
    val backgroundColor = when (style) {
        LiroutiToastStyle.Black -> LiroutiTheme.colors.surfaceInverse
        LiroutiToastStyle.Gray -> Neutral96
    }
    val contentColor = when (style) {
        LiroutiToastStyle.Black -> LiroutiTheme.colors.labelReverse
        LiroutiToastStyle.Gray -> LiroutiTheme.colors.labelDefault
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(6.dp))
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = message,
            modifier = Modifier.weight(1f),
            style = MessageTextStyle,
            color = contentColor,
        )
        if (onCloseClick != null) {
            LiroutiToastCloseIcon(
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = onCloseClick),
                color = contentColor,
            )
        }
    }
}

@Composable
private fun LiroutiToastCloseIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
) {
    Canvas(modifier = modifier) {
        val inset = size.width * 0.1875f
        val strokeWidth = size.width * 0.06f
        drawLine(
            color = color,
            start = Offset(size.width - inset, inset),
            end = Offset(inset, size.height - inset),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Square,
        )
        drawLine(
            color = color,
            start = Offset(inset, inset),
            end = Offset(size.width - inset, size.height - inset),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Square,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiToastBlackPreview() {
    LiroutiFrontendTheme {
        LiroutiToast(
            message = "Message",
            style = LiroutiToastStyle.Black,
            onCloseClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiToastGrayPreview() {
    LiroutiFrontendTheme {
        LiroutiToast(
            message = "Message",
            style = LiroutiToastStyle.Gray,
            onCloseClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
