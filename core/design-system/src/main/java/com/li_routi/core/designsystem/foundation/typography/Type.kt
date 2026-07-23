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
    val display1Bold: TextStyle = lirouti(52, 72, FontWeight.Bold),
    val display1Medium: TextStyle = lirouti(52, 72, FontWeight.Medium),
    val display2Bold: TextStyle = lirouti(46, 62, FontWeight.Bold),
    val display2Medium: TextStyle = lirouti(46, 62, FontWeight.Medium),

    val title1Bold: TextStyle = lirouti(36, 48, FontWeight.Bold),
    val title1Medium: TextStyle = lirouti(36, 48, FontWeight.Medium),
    val title2Bold: TextStyle = lirouti(28, 38, FontWeight.Bold),
    val title2Medium: TextStyle = lirouti(28, 38, FontWeight.Medium),
    val title3Bold: TextStyle = lirouti(24, 32, FontWeight.Bold),
    val title3Medium: TextStyle = lirouti(24, 32, FontWeight.Medium),

    val heading1SemiBold: TextStyle = lirouti(22, 30, FontWeight.SemiBold),
    val heading1Medium: TextStyle = lirouti(22, 30, FontWeight.Medium),
    val heading1Regular: TextStyle = lirouti(22, 30, FontWeight.Normal),
    val heading2SemiBold: TextStyle = lirouti(18, 26, FontWeight.SemiBold),
    val heading2Medium: TextStyle = lirouti(18, 26, FontWeight.Medium),
    val heading2Regular: TextStyle = lirouti(18, 26, FontWeight.Normal),

    val body1SemiBold: TextStyle = lirouti(16, 24, FontWeight.SemiBold),
    val body1Medium: TextStyle = lirouti(16, 24, FontWeight.Medium),
    val body1Regular: TextStyle = lirouti(16, 24, FontWeight.Normal),
    val body1LongSemiBold: TextStyle = lirouti(16, 26, FontWeight.SemiBold),
    val body1LongMedium: TextStyle = lirouti(16, 26, FontWeight.Medium),
    val body1LongRegular: TextStyle = lirouti(16, 26, FontWeight.Normal),
    val body2SemiBold: TextStyle = lirouti(14, 20, FontWeight.SemiBold),
    val body2Medium: TextStyle = lirouti(14, 20, FontWeight.Medium),
    val body2Regular: TextStyle = lirouti(14, 20, FontWeight.Normal),
    val body2LongSemiBold: TextStyle = lirouti(14, 22, FontWeight.SemiBold),
    val body2LongMedium: TextStyle = lirouti(14, 22, FontWeight.Medium),
    val body2LongRegular: TextStyle = lirouti(14, 22, FontWeight.Normal),
    val body3SemiBold: TextStyle = lirouti(13, 15, FontWeight.SemiBold),
    val body3Medium: TextStyle = lirouti(13, 15, FontWeight.Medium),
    val body3Regular: TextStyle = lirouti(13, 15, FontWeight.Normal),

    val captionSemiBold: TextStyle = lirouti(12, 16, FontWeight.SemiBold),
    val captionMedium: TextStyle = lirouti(12, 16, FontWeight.Medium),
    val captionRegular: TextStyle = lirouti(12, 16, FontWeight.Normal),
)

val LocalLiroutiTypography = staticCompositionLocalOf { LiroutiTypography() }

val Typography = androidx.compose.material3.Typography()
