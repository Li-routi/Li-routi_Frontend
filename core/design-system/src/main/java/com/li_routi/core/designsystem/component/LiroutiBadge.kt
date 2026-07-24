package com.li_routi.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.foundation.color.Neutral97
import com.li_routi.core.designsystem.foundation.typography.Pretendard
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

enum class LiroutiBadgeColor {
    Neutral,
    Blue,
    Orange,
    Green,
}

enum class LiroutiBadgeSize {
    XSmall,
    Small,
    Medium,
}

private val BlueBadgeBackground = Color(0xFFF4F7FB)
private val OrangeBadgeBackground = Color(0xFFFFDDB8)
private val OrangeBadgeText = Color(0xFFD26D00)
private val GreenBadgeBackground = Color(0xFFE0F8E9)
private val GreenBadgeText = Color(0xFF008C51)

@Composable
fun LiroutiBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: LiroutiBadgeColor = LiroutiBadgeColor.Neutral,
    size: LiroutiBadgeSize = LiroutiBadgeSize.XSmall,
) {
    val backgroundColor = when (color) {
        LiroutiBadgeColor.Neutral -> Neutral97
        LiroutiBadgeColor.Blue -> BlueBadgeBackground
        LiroutiBadgeColor.Orange -> OrangeBadgeBackground
        LiroutiBadgeColor.Green -> GreenBadgeBackground
    }
    val textColor = when (color) {
        LiroutiBadgeColor.Neutral -> LiroutiTheme.colors.labelInfo
        LiroutiBadgeColor.Blue -> LiroutiTheme.colors.secondaryNormal
        LiroutiBadgeColor.Orange -> OrangeBadgeText
        LiroutiBadgeColor.Green -> GreenBadgeText
    }
    val horizontalPadding = when (size) {
        LiroutiBadgeSize.Medium -> 8.dp
        LiroutiBadgeSize.Small, LiroutiBadgeSize.XSmall -> 6.dp
    }
    val verticalPadding = when (size) {
        LiroutiBadgeSize.Medium -> 5.dp
        LiroutiBadgeSize.Small -> 4.dp
        LiroutiBadgeSize.XSmall -> 3.dp
    }
    val cornerRadius = when (size) {
        LiroutiBadgeSize.Medium -> 8.dp
        LiroutiBadgeSize.Small, LiroutiBadgeSize.XSmall -> 6.dp
    }
    val textStyle = when (size) {
        LiroutiBadgeSize.Medium -> TextStyle(
            fontFamily = Pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            lineHeight = 16.sp,
            letterSpacing = (-0.025f).em,
        )

        LiroutiBadgeSize.Small -> TextStyle(
            fontFamily = Pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 14.sp,
        )

        LiroutiBadgeSize.XSmall -> TextStyle(
            fontFamily = Pretendard,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            lineHeight = 14.sp,
        )
    }

    Box(
        modifier = modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(cornerRadius))
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            softWrap = false,
            style = textStyle,
            color = textColor,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiBadgePreview() {
    LiroutiFrontendTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LiroutiBadgeColor.entries.forEach { color ->
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    LiroutiBadgeSize.entries.forEach { size ->
                        LiroutiBadge(text = "텍스트", color = color, size = size)
                    }
                }
            }
        }
    }
}
