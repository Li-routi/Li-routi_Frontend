package com.li_routi.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
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
import kotlinx.coroutines.delay

enum class LiroutiToastStyle {
    Black,
    Gray,
    Dimmer,
}

private val MessageTextStyle = TextStyle(
    fontFamily = Pretendard,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 22.sp,
    letterSpacing = (-0.025f).em,
)

private const val AutoDismissMillis = 3000L

@Composable
fun LiroutiToast(
    message: String,
    modifier: Modifier = Modifier,
    style: LiroutiToastStyle = LiroutiToastStyle.Black,
    onCloseClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(20.dp),
) {
    // 앱 전역 규칙: 토스트는 X를 안 눌러도 3초 뒤 저절로 사라진다. onCloseClick을 그대로
    // 재사용해서(호출부는 전부 이 콜백으로 메시지 상태를 null로 지움) 호출부마다 따로
    // 타이머를 만들 필요가 없다. rememberUpdatedState로 감싸 콜백이 매 리컴포지션마다
    // 새 람다로 바뀌어도 LaunchedEffect가 불필요하게 재시작되지 않게 한다.
    val currentOnCloseClick by rememberUpdatedState(onCloseClick)
    LaunchedEffect(message) {
        delay(AutoDismissMillis)
        currentOnCloseClick?.invoke()
    }

    val backgroundColor = when (style) {
        LiroutiToastStyle.Black -> LiroutiTheme.colors.surfaceInverse
        LiroutiToastStyle.Gray -> Neutral96
        LiroutiToastStyle.Dimmer -> LiroutiTheme.colors.dimmerDefault
    }
    val contentColor = when (style) {
        LiroutiToastStyle.Black -> LiroutiTheme.colors.labelReverse
        LiroutiToastStyle.Gray -> LiroutiTheme.colors.labelDefault
        LiroutiToastStyle.Dimmer -> LiroutiTheme.colors.labelReverse
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(6.dp))
            .padding(contentPadding),
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
