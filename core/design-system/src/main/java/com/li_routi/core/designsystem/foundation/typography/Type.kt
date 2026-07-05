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

private fun lirouti(
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
data class LiroutiTypography(
    val display1: TextStyle = lirouti(52, 72, FontWeight.Bold),
    val display2: TextStyle = lirouti(46, 62, FontWeight.Bold),

    val title1: TextStyle = lirouti(36, 48, FontWeight.Bold),
    val title2: TextStyle = lirouti(28, 38, FontWeight.Bold),
    val title3: TextStyle = lirouti(24, 32, FontWeight.Bold),

    val heading1: TextStyle = lirouti(22, 30, FontWeight.SemiBold),
    val heading2: TextStyle = lirouti(18, 26, FontWeight.SemiBold),

    val body1: TextStyle = lirouti(16, 24, FontWeight.Normal),
    val body1Long: TextStyle = lirouti(16, 26, FontWeight.Normal),
    val body2: TextStyle = lirouti(14, 20, FontWeight.Normal),
    val body2Long: TextStyle = lirouti(14, 22, FontWeight.Normal),
    val body3: TextStyle = lirouti(13, 15, FontWeight.Normal),

    val caption: TextStyle = lirouti(12, 16, FontWeight.Normal),
)

val LocalLiroutiTypography = staticCompositionLocalOf { LiroutiTypography() }

val Typography = androidx.compose.material3.Typography()
