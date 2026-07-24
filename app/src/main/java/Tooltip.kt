package com.example.ri_routi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


enum class ArrowPosition {
    Top, Bottom, Left, Right
}


class TooltipShape(
    private val cornerRadius: Dp = 8.dp,
    private val arrowWidth: Dp = 10.dp,
    private val arrowHeight: Dp = 5.dp,
    private val arrowOffset: Dp = 10.dp,
    private val arrowPosition: ArrowPosition = ArrowPosition.Top
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val r = with(density) { cornerRadius.toPx() }
        val aW = with(density) { arrowWidth.toPx() }
        val aH = with(density) { arrowHeight.toPx() }
        val aO = with(density) { arrowOffset.toPx() }
        val w = size.width
        val h = size.height

        val path = Path().apply {
            when (arrowPosition) {
                ArrowPosition.Top -> {
                    val bodyTop = aH
                    moveTo(r, bodyTop)
                    lineTo(aO, bodyTop)
                    cubicTo(aO + aW * 0.25f, bodyTop, aO + aW * 0.35f, 0f, aO + aW * 0.5f, 0f)
                    cubicTo(aO + aW * 0.65f, 0f, aO + aW * 0.75f, bodyTop, aO + aW, bodyTop)
                    lineTo(w - r, bodyTop)
                    arcTo(Rect(w - 2 * r, bodyTop, w, bodyTop + 2 * r), -90f, 90f, false)
                    lineTo(w, h - r)
                    arcTo(Rect(w - 2 * r, h - 2 * r, w, h), 0f, 90f, false)
                    lineTo(r, h)
                    arcTo(Rect(0f, h - 2 * r, 2 * r, h), 90f, 90f, false)
                    lineTo(0f, bodyTop + r)
                    arcTo(Rect(0f, bodyTop, 2 * r, bodyTop + 2 * r), 180f, 90f, false)
                }

                ArrowPosition.Bottom -> {
                    val bodyBottom = h - aH
                    moveTo(r, 0f)
                    lineTo(w - r, 0f)
                    arcTo(Rect(w - 2 * r, 0f, w, 2 * r), -90f, 90f, false)
                    lineTo(w, bodyBottom - r)
                    arcTo(Rect(w - 2 * r, bodyBottom - 2 * r, w, bodyBottom), 0f, 90f, false)
                    lineTo(aO + aW, bodyBottom)
                    cubicTo(aO + aW * 0.75f, bodyBottom, aO + aW * 0.65f, h, aO + aW * 0.5f, h)
                    cubicTo(aO + aW * 0.35f, h, aO + aW * 0.25f, bodyBottom, aO, bodyBottom)
                    lineTo(r, bodyBottom)
                    arcTo(Rect(0f, bodyBottom - 2 * r, 2 * r, bodyBottom), 90f, 90f, false)
                    lineTo(0f, r)
                    arcTo(Rect(0f, 0f, 2 * r, 2 * r), 180f, 90f, false)
                }

                ArrowPosition.Left -> {
                    val bodyLeft = aH
                    val startY = aO
                    moveTo(bodyLeft + r, 0f)
                    lineTo(w - r, 0f)
                    arcTo(Rect(w - 2 * r, 0f, w, 2 * r), -90f, 90f, false)
                    lineTo(w, h - r)
                    arcTo(Rect(w - 2 * r, h - 2 * r, w, h), 0f, 90f, false)
                    lineTo(bodyLeft + r, h)
                    arcTo(Rect(bodyLeft, h - 2 * r, bodyLeft + 2 * r, h), 90f, 90f, false)
                    lineTo(bodyLeft, startY + aW)
                    cubicTo(bodyLeft, startY + aW * 0.75f, 0f, startY + aW * 0.65f, 0f, startY + aW * 0.5f)
                    cubicTo(0f, startY + aW * 0.35f, bodyLeft, startY + aW * 0.25f, bodyLeft, startY)
                    lineTo(bodyLeft, r)
                    arcTo(Rect(bodyLeft, 0f, bodyLeft + 2 * r, 2 * r), 180f, 90f, false)
                }

                ArrowPosition.Right -> {
                    val bodyRight = w - aH
                    val startY = aO
                    moveTo(r, 0f)
                    lineTo(bodyRight - r, 0f)
                    arcTo(Rect(bodyRight - 2 * r, 0f, bodyRight, 2 * r), -90f, 90f, false)
                    lineTo(bodyRight, startY)
                    cubicTo(bodyRight, startY + aW * 0.25f, w, startY + aW * 0.35f, w, startY + aW * 0.5f)
                    cubicTo(w, startY + aW * 0.65f, bodyRight, startY + aW * 0.75f, bodyRight, startY + aW)
                    lineTo(bodyRight, h - r)
                    arcTo(Rect(bodyRight - 2 * r, h - 2 * r, bodyRight, h), 0f, 90f, false)
                    lineTo(r, h)
                    arcTo(Rect(0f, h - 2 * r, 2 * r, h), 90f, 90f, false)
                    lineTo(0f, r)
                    arcTo(Rect(0f, 0f, 2 * r, 2 * r), 180f, 90f, false)
                }
            }
            close()
        }

        return Outline.Generic(path)
    }
}


@Composable
fun TooltipLightGray(
    modifier: Modifier = Modifier,
    arrowPosition: ArrowPosition = ArrowPosition.Top
) {
    val backgroundColor = Color(0xFF8E9299)
    val arrowHeight = 6.dp
    val arrowWidth = 16.dp
    val arrowOffset = 4.dp

    val shape = TooltipShape(
        cornerRadius = 6.dp,
        arrowWidth = arrowWidth,
        arrowHeight = arrowHeight,
        arrowOffset = arrowOffset,
        arrowPosition = arrowPosition
    )

    val (width, height) = when (arrowPosition) {
        ArrowPosition.Top, ArrowPosition.Bottom -> 50.dp to (24.dp + arrowHeight)
        ArrowPosition.Left, ArrowPosition.Right -> (50.dp + arrowHeight) to 24.dp
    }

    val topPadding = if (arrowPosition == ArrowPosition.Top) arrowHeight else 0.dp
    val bottomPadding = if (arrowPosition == ArrowPosition.Bottom) arrowHeight else 0.dp
    val startPadding = if (arrowPosition == ArrowPosition.Left) arrowHeight else 0.dp
    val endPadding = if (arrowPosition == ArrowPosition.Right) arrowHeight else 0.dp

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(width = width, height = height)
            .clip(shape)
            .background(backgroundColor, shape)
            .padding(
                start = startPadding,
                end = endPadding,
                top = topPadding,
                bottom = bottomPadding
            )
    ) {
        Text(
            text = "Tooltip",
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

@Composable
fun TooltipDarkGray(
    modifier: Modifier = Modifier,
    arrowPosition: ArrowPosition = ArrowPosition.Top
) {
    val backgroundColor = Color(0xFF383A3D)
    val arrowHeight = 8.dp
    val arrowWidth = 20.dp
    val arrowOffset = 8.dp

    val shape = TooltipShape(
        cornerRadius = 8.dp,
        arrowWidth = arrowWidth,
        arrowHeight = arrowHeight,
        arrowOffset = arrowOffset,
        arrowPosition = arrowPosition
    )

    val (width, height) = when (arrowPosition) {
        ArrowPosition.Top, ArrowPosition.Bottom -> 65.dp to (38.dp + arrowHeight)
        ArrowPosition.Left, ArrowPosition.Right -> (65.dp + arrowHeight) to 38.dp
    }

    val topPadding = if (arrowPosition == ArrowPosition.Top) arrowHeight else 0.dp
    val bottomPadding = if (arrowPosition == ArrowPosition.Bottom) arrowHeight else 0.dp
    val startPadding = if (arrowPosition == ArrowPosition.Left) arrowHeight else 0.dp
    val endPadding = if (arrowPosition == ArrowPosition.Right) arrowHeight else 0.dp

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(width = width, height = height)
            .clip(shape)
            .background(backgroundColor, shape)
            .padding(
                start = startPadding,
                end = endPadding,
                top = topPadding,
                bottom = bottomPadding
            )
    ) {
        Text(
            text = "Tooltip",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}


@Composable
fun SelectableTooltip(
    isDarkType: Boolean = false,
    modifier: Modifier = Modifier,
    arrowPosition: ArrowPosition = ArrowPosition.Top
) {
    if (isDarkType) {
        TooltipDarkGray(
            modifier = modifier,
            arrowPosition = arrowPosition
        )
    } else {
        TooltipLightGray(
            modifier = modifier,
            arrowPosition = arrowPosition
        )
    }
}


@Composable
fun TooltipShowcase() {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            SelectableTooltip(isDarkType = false, arrowPosition = ArrowPosition.Top)
            SelectableTooltip(isDarkType = false, arrowPosition = ArrowPosition.Bottom)
            SelectableTooltip(isDarkType = false, arrowPosition = ArrowPosition.Left)
            SelectableTooltip(isDarkType = false, arrowPosition = ArrowPosition.Right)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            SelectableTooltip(isDarkType = true, arrowPosition = ArrowPosition.Top)
            SelectableTooltip(isDarkType = true, arrowPosition = ArrowPosition.Bottom)
            SelectableTooltip(isDarkType = true, arrowPosition = ArrowPosition.Left)
            SelectableTooltip(isDarkType = true, arrowPosition = ArrowPosition.Right)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF2F3F5)
@Composable
fun TooltipShowcasePreview() {
    TooltipShowcase()
}