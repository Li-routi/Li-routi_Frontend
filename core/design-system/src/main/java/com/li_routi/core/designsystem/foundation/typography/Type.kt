package com.li_routi.core.designsystem.foundation.typography

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.R

val Pretendard = FontFamily(
    Font(R.font.pretendard_regular, FontWeight.Normal),
    Font(R.font.pretendard_medium, FontWeight.Medium),
    Font(R.font.pretendard_semibold, FontWeight.SemiBold),
    Font(R.font.pretendard_bold, FontWeight.Bold),
)

private const val TrackingEm = -0.025f

private val EvenLineHeight = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.None,
)

private fun riroti(
    size: Int,
    lineHeight: Int,
    weight: FontWeight = FontWeight.Normal,
) = TextStyle(
    fontFamily = Pretendard,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = TrackingEm.em,
    lineHeightStyle = EvenLineHeight,
)

@Immutable
data class RirotiTypography(
    val display1: TextStyle = riroti(52, 72, FontWeight.Bold),
    val display2: TextStyle = riroti(46, 62, FontWeight.Bold),

    val title1: TextStyle = riroti(36, 48, FontWeight.Bold),
    val title2: TextStyle = riroti(28, 38, FontWeight.Bold),
    val title3: TextStyle = riroti(24, 32, FontWeight.Bold),

    val heading1: TextStyle = riroti(22, 30, FontWeight.SemiBold),
    val heading2: TextStyle = riroti(18, 26, FontWeight.SemiBold),

    val body1: TextStyle = riroti(16, 24, FontWeight.Normal),
    val body1Long: TextStyle = riroti(16, 26, FontWeight.Normal),
    val body2: TextStyle = riroti(14, 20, FontWeight.Normal),
    val body2Long: TextStyle = riroti(14, 22, FontWeight.Normal),
    val body3: TextStyle = riroti(13, 15, FontWeight.Normal),

    val caption: TextStyle = riroti(12, 16, FontWeight.Normal),
)

val LocalRirotiTypography = staticCompositionLocalOf { RirotiTypography() }

val Typography = androidx.compose.material3.Typography()
