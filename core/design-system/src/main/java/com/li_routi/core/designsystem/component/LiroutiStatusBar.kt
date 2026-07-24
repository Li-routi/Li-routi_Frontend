package com.li_routi.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.foundation.typography.Pretendard
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

enum class LiroutiStatusBarStyle {
    IOS,
    AOS,
}

private val IosTimeTextStyle = TextStyle(
    fontFamily = Pretendard,
    fontWeight = FontWeight.SemiBold,
    fontSize = 15.sp,
    lineHeight = 15.sp,
    letterSpacing = (-0.0167f).em,
)

private val AosTimeTextStyle = TextStyle(
    fontFamily = Pretendard,
    fontWeight = FontWeight.SemiBold,
    fontSize = 12.sp,
    lineHeight = 12.sp,
)

@Composable
fun LiroutiStatusBar(
    modifier: Modifier = Modifier,
    style: LiroutiStatusBarStyle = LiroutiStatusBarStyle.IOS,
    time: String = if (style == LiroutiStatusBarStyle.IOS) "5:37" else "12:30",
    color: Color = LiroutiTheme.colors.labelDefault,
) {
    when (style) {
        LiroutiStatusBarStyle.IOS -> LiroutiIosStatusBar(time = time, color = color, modifier = modifier)
        LiroutiStatusBarStyle.AOS -> LiroutiAosStatusBar(time = time, color = color, modifier = modifier)
    }
}

@Composable
private fun LiroutiIosStatusBar(
    time: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp),
    ) {
        Text(
            text = time,
            style = IosTimeTextStyle,
            color = color,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 31.dp),
        )
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            LiroutiIosCellSignalIcon(modifier = Modifier.size(width = 16.5.dp, height = 12.dp), color = color)
            Spacer(modifier = Modifier.width(5.5.dp))
            LiroutiIosWifiIcon(modifier = Modifier.size(width = 16.dp, height = 12.dp), color = color)
            Spacer(modifier = Modifier.width(4.5.dp))
            LiroutiIosBatteryIcon(modifier = Modifier.size(width = 23.5.dp, height = 12.dp), color = color)
        }
    }
}

@Composable
private fun LiroutiAosStatusBar(
    time: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp),
    ) {
        Text(
            text = time,
            style = AosTimeTextStyle,
            color = color,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 22.dp),
        )
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LiroutiAosWifiIcon(modifier = Modifier.size(15.dp), color = color)
            LiroutiAosCellSignalIcon(modifier = Modifier.size(15.dp), color = color)
            LiroutiAosBatteryIcon(modifier = Modifier.size(15.dp), color = color)
        }
    }
}

private const val IosWifiGlyphOriginX = 22.0f
private const val IosWifiGlyphOriginY = -0.7721f
private const val IosWifiGlyphWidth = 15.79f
private const val IosWifiGlyphHeight = 12.2139f

@Composable
private fun LiroutiIosWifiIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
) {
    Canvas(modifier = modifier) {
        fun px(x: Float) = (x - IosWifiGlyphOriginX) / IosWifiGlyphWidth * size.width
        fun py(y: Float) = (y - IosWifiGlyphOriginY) / IosWifiGlyphHeight * size.height

        val path = Path().apply {
            moveTo(px(27.6816f), py(8.73376f))
            cubicTo(px(29.0006f), py(7.59522f), px(30.932f), py(7.59516f), px(32.251f), py(8.73376f))
            cubicTo(px(32.3173f), py(8.79501f), px(32.3566f), py(8.88171f), px(32.3584f), py(8.97302f))
            cubicTo(px(32.3602f), py(9.06427f), px(32.3246f), py(9.15224f), px(32.2607f), py(9.21618f))
            lineTo(px(30.1953f), py(11.3441f))
            cubicTo(px(30.1348f), py(11.4065f), px(30.0518f), py(11.4418f), px(29.9658f), py(11.4418f))
            cubicTo(px(29.88f), py(11.4417f), px(29.7977f), py(11.4063f), px(29.7373f), py(11.3441f))
            lineTo(px(27.6709f), py(9.21618f))
            cubicTo(px(27.6073f), py(9.15223f), px(27.5724f), py(9.06414f), px(27.5742f), py(8.97302f))
            cubicTo(px(27.5761f), py(8.88171f), px(27.6152f), py(8.79496f), px(27.6816f), py(8.73376f))
            close()

            moveTo(px(24.8887f), py(6.11071f))
            cubicTo(px(27.7303f), py(3.41253f), px(32.1309f), py(3.41264f), px(34.9726f), py(6.11071f))
            cubicTo(px(35.0368f), py(6.17398f), px(35.0743f), py(6.26087f), px(35.0752f), py(6.35192f))
            cubicTo(px(35.0761f), py(6.44293f), px(35.0404f), py(6.53052f), px(34.9775f), py(6.59509f))
            lineTo(px(33.7842f), py(7.82653f))
            cubicTo(px(33.6611f), py(7.95227f), px(33.4621f), py(7.95481f), px(33.3359f), py(7.83239f))
            cubicTo(px(32.4027f), py(6.96983f), px(31.1886f), py(6.49247f), px(29.9297f), py(6.49255f))
            cubicTo(px(28.6717f), py(6.49309f), px(27.4589f), py(6.97054f), px(26.5264f), py(7.83239f))
            cubicTo(px(26.4002f), py(7.95477f), px(26.2012f), py(7.95216f), px(26.0781f), py(7.82653f))
            lineTo(px(24.8848f), py(6.59509f))
            cubicTo(px(24.8218f), py(6.5306f), px(24.7863f), py(6.44299f), px(24.7871f), py(6.35192f))
            cubicTo(px(24.7879f), py(6.2609f), px(24.8245f), py(6.17397f), px(24.8887f), py(6.11071f))
            close()

            moveTo(px(22.0996f), py(3.49157f))
            cubicTo(px(26.4582f), py(-0.77204f), px(33.3348f), py(-0.772107f), px(37.6933f), py(3.49157f))
            cubicTo(px(37.7563f), py(3.5549f), px(37.7915f), py(3.64154f), px(37.792f), py(3.7318f))
            cubicTo(px(37.7925f), py(3.82213f), px(37.7577f), py(3.90893f), px(37.6953f), py(3.97302f))
            lineTo(px(36.5f), py(5.20446f))
            cubicTo(px(36.3768f), py(5.33066f), px(36.1767f), py(5.33273f), px(36.0517f), py(5.20837f))
            cubicTo(px(34.3911f), py(3.59687f), px(32.1878f), py(2.69773f), px(29.8965f), py(2.69762f))
            cubicTo(px(27.605f), py(2.69764f), px(25.401f), py(3.59676f), px(23.7402f), py(5.20837f))
            cubicTo(px(23.6152f), py(5.33277f), px(23.415f), py(5.33095f), px(23.292f), py(5.20446f))
            lineTo(px(22.0967f), py(3.97302f))
            cubicTo(px(22.0344f), py(3.9089f), px(21.9994f), py(3.8221f), px(22f), py(3.7318f))
            cubicTo(px(22.0006f), py(3.64148f), px(22.0365f), py(3.55487f), px(22.0996f), py(3.49157f))
            close()
        }
        drawPath(path = path, color = color)
    }
}

@Composable
private fun LiroutiIosCellSignalIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
) {
    Canvas(modifier = modifier) {
        val barCount = 4
        val gap = size.width * 0.08f
        val barWidth = (size.width - gap * (barCount - 1)) / barCount
        repeat(barCount) { index ->
            val heightFraction = 0.4f + 0.2f * index
            val barHeight = size.height * heightFraction
            drawRoundRect(
                color = color,
                topLeft = Offset(index * (barWidth + gap), size.height - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth * 0.2f),
            )
        }
    }
}

@Composable
private fun LiroutiIosBatteryIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
) {
    Canvas(modifier = modifier) {
        val nubWidth = size.width * 0.08f
        val bodyWidth = size.width - nubWidth
        val strokeWidth = size.height * 0.1f
        val cornerRadius = CornerRadius(size.height * 0.25f)

        drawRoundRect(
            color = color,
            topLeft = Offset(0f, 0f),
            size = Size(bodyWidth, size.height),
            cornerRadius = cornerRadius,
            style = Stroke(width = strokeWidth),
        )
        val fillInset = strokeWidth * 1.6f
        drawRoundRect(
            color = color,
            topLeft = Offset(fillInset, fillInset),
            size = Size(bodyWidth - fillInset * 2f, size.height - fillInset * 2f),
            cornerRadius = CornerRadius(size.height * 0.15f),
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(bodyWidth, size.height * 0.3f),
            size = Size(nubWidth, size.height * 0.4f),
            cornerRadius = CornerRadius(nubWidth * 0.3f),
        )
    }
}


private fun DrawScope.drawWedge(
    apex: Offset,
    radius: Float,
    sweepDegrees: Float,
    color: Color,
    alpha: Float,
) {
    drawArc(
        color = color,
        startAngle = 270f - sweepDegrees / 2f,
        sweepAngle = sweepDegrees,
        useCenter = true,
        topLeft = Offset(apex.x - radius, apex.y - radius),
        size = Size(radius * 2f, radius * 2f),
        alpha = alpha,
    )
}

@Composable
private fun LiroutiAosWifiIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
) {
    Canvas(modifier = modifier) {
        val apex = Offset(size.width * 0.5f, size.height * 0.89f)
        drawWedge(apex = apex, radius = size.minDimension * 0.95f, sweepDegrees = 100f, color = color, alpha = 0.2f)
        drawWedge(apex = apex, radius = size.minDimension * 0.68f, sweepDegrees = 100f, color = color, alpha = 1f)
    }
}

@Composable
private fun LiroutiAosCellSignalIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
) {
    Canvas(modifier = modifier) {
        fun rampTriangle(rightFraction: Float, topFraction: Float): Path = Path().apply {
            moveTo(size.width * 0.111f, size.height * 0.889f)
            lineTo(size.width * rightFraction, size.height * 0.889f)
            lineTo(size.width * rightFraction, size.height * topFraction)
            close()
        }
        drawPath(path = rampTriangle(0.889f, 0.111f), color = color, alpha = 0.2f)
        drawPath(path = rampTriangle(0.722f, 0.278f), color = color, alpha = 1f)
    }
}

@Composable
private fun LiroutiAosBatteryIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
) {
    Canvas(modifier = modifier) {
        val bodyLeft = size.width * 0.278f
        val bodyTop = size.height * 0.167f
        val bodyWidth = size.width * 0.444f
        val bodyHeight = size.height * 0.722f
        val bodyCorner = CornerRadius(size.width * 0.12f)

        drawRoundRect(
            color = color,
            topLeft = Offset(bodyLeft, bodyTop),
            size = Size(bodyWidth, bodyHeight),
            cornerRadius = bodyCorner,
            alpha = 0.2f,
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * 0.415f, size.height * 0.111f),
            size = Size(size.width * 0.169f, size.height * 0.056f),
            cornerRadius = CornerRadius(size.width * 0.04f),
            alpha = 0.2f,
        )

        val fillTop = size.height * 0.444f
        drawRoundRect(
            color = color,
            topLeft = Offset(bodyLeft, fillTop),
            size = Size(bodyWidth, bodyTop + bodyHeight - fillTop),
            cornerRadius = CornerRadius(size.width * 0.1f),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiStatusBarIosPreview() {
    LiroutiFrontendTheme {
        LiroutiStatusBar(style = LiroutiStatusBarStyle.IOS)
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiStatusBarAosPreview() {
    LiroutiFrontendTheme {
        LiroutiStatusBar(style = LiroutiStatusBarStyle.AOS)
    }
}
