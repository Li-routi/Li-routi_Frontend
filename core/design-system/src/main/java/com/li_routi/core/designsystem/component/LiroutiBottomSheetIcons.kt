package com.li_routi.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

internal fun DrawScope.drawCloseIcon(color: Color) {
    val inset = size.width / 4f
    drawLine(
        color = color,
        start = Offset(inset, inset),
        end = Offset(size.width - inset, size.height - inset),
        strokeWidth = 1.5.dp.toPx(),
        cap = StrokeCap.Round,
    )
    drawLine(
        color = color,
        start = Offset(size.width - inset, inset),
        end = Offset(inset, size.height - inset),
        strokeWidth = 1.5.dp.toPx(),
        cap = StrokeCap.Round,
    )
}

@Composable
internal fun LiroutiCheckmarkIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
) {
    Canvas(modifier = modifier) {
        val strokeColor = if (color == Color.Unspecified) Color.Black else color
        val path = Path().apply {
            moveTo(size.width * 0.125f, size.height * 0.55f)
            lineTo(size.width * 0.4f, size.height * 0.78f)
            lineTo(size.width * 0.875f, size.height * 0.25f)
        }
        drawPath(
            path = path,
            color = strokeColor,
            style = Stroke(
                width = 1.6.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            ),
        )
    }
}

@Composable
fun LiroutiChevronRightIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
) {
    Canvas(modifier = modifier) {
        val strokeColor = if (color == Color.Unspecified) Color.Black else color
        val path = Path().apply {
            moveTo(size.width * 0.375f, size.height * 0.1875f)
            lineTo(size.width * 0.6875f, size.height * 0.5f)
            lineTo(size.width * 0.375f, size.height * 0.8125f)
        }
        drawPath(
            path = path,
            color = strokeColor,
            style = Stroke(
                width = 1.6.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            ),
        )
    }
}

@Composable
internal fun LiroutiDotMarkIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
) {
    Canvas(modifier = modifier) {
        val fillColor = if (color == Color.Unspecified) Color.Black else color

        drawCircle(
            color = fillColor,
            radius = size.minDimension * 0.09375f,
            center = Offset(size.width / 2f, size.height / 2f),
        )
    }
}
