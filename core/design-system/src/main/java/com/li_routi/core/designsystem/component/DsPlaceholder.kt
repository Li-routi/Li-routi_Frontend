package com.li_routi.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * Figma "Design System [1.0 완료]" 페이지의 컴포넌트가 아직 Compose로 구현되지 않은 자리에 쓰는
 * 임시 placeholder.
 *
 * 실제 컴포넌트가 완성되면 이 호출부를 실제 컴포넌트 호출로 한 줄 교체하면 된다.
 *
 * TODO(design-system): Figma "Design System [1.0 완료] > {componentName}" 완성되면 실제 컴포넌트로 교체
 *
 * @param componentName Figma Design System 컴포넌트 이름 (예: "Icon/notification", "Button_Filled", "Tab")
 */
@Composable
fun DsPlaceholder(
    componentName: String,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
) {
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 24.dp, minHeight = 24.dp)
            .clip(shape)
            .background(LiroutiTheme.colors.backgroundStrong)
            .dashedBorder(color = LiroutiTheme.colors.borderStrong, shape = shape)
            .padding(4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = componentName,
            style = LiroutiTheme.typography.caption,
            color = LiroutiTheme.colors.labelInfo,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}

private fun Modifier.dashedBorder(
    color: Color,
    shape: Shape,
    strokeWidth: Dp = 1.dp,
    dashLength: Dp = 4.dp,
    gapLength: Dp = 4.dp,
): Modifier = drawWithContent {
    drawContent()
    val stroke = Stroke(
        width = strokeWidth.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength.toPx(), gapLength.toPx()), 0f),
    )
    if (shape == RectangleShape) {
        drawRoundRect(
            color = color,
            style = stroke,
            cornerRadius = CornerRadius.Zero,
        )
    } else {
        drawRoundRect(
            color = color,
            style = stroke,
            cornerRadius = CornerRadius(strokeWidth.toPx() * 4),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DsPlaceholderPreview() {
    LiroutiFrontendTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            DsPlaceholder(componentName = "Icon/notification")
        }
    }
}
