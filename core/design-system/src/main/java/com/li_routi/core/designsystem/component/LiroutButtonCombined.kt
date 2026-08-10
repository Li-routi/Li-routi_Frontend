package com.li_routi.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.theme.LiroutiTheme

@Composable
fun HorizontalDoubleButton(
    modifier: Modifier = Modifier,
    leftLabel: String = "Label",
    rightLabel: String = "Label",
    leftWidth: Dp? = null, // null: weight(1f) 사용 (Segment 스타일), 값이 있으면 고정 너비 (FlexibleAsymmetric 스타일)
    containerWidth: Dp = 345.dp,
    containerHeight: Dp = 44.dp,
    spacing: Dp = 6.dp,
    cornerRadius: Dp = 8.dp,

    leftBackgroundColor: Color = LiroutiTheme.colors.backgroundAlternative,
    leftTextColor: Color = LiroutiTheme.colors.labelDefault,

    rightBackgroundColor: Color = LiroutiTheme.colors.primaryNormal,
    rightTextColor: Color = LiroutiTheme.colors.backgroundAlternative,

    fontSize: TextUnit = 14.sp,
    fontWeight: FontWeight = FontWeight(500),

    onLeftClick: () -> Unit = {},
    onRightClick: () -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.size(width = containerWidth, height = containerHeight)
    ) {

        val leftModifier = if (leftWidth != null) {
            Modifier.size(width = leftWidth, height = containerHeight)
        } else {
            Modifier
                .weight(1f)
                .fillMaxHeight()
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = leftModifier
                .clip(RoundedCornerShape(cornerRadius))
                .background(leftBackgroundColor)
                .clickable { onLeftClick() }
        ) {
            Text(
                text = leftLabel,
                fontSize = fontSize,
                fontWeight = fontWeight,
                color = leftTextColor
            )
        }


        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(cornerRadius))
                .background(rightBackgroundColor)
                .clickable { onRightClick() }
        ) {
            Text(
                text = rightLabel,
                fontSize = fontSize,
                fontWeight = fontWeight,
                color = rightTextColor
            )
        }
    }
}



@Composable
fun HorizontalFlexibleAsymmetric(
    modifier: Modifier = Modifier,
    leftLabel: String = "Label",
    rightLabel: String = "Label",
    onLeftClick: () -> Unit = {},
    onRightClick: () -> Unit = {}
) {
    HorizontalDoubleButton(
        modifier = modifier,
        leftLabel = leftLabel,
        rightLabel = rightLabel,
        leftWidth = 100.dp,
        onLeftClick = onLeftClick,
        onRightClick = onRightClick
    )
}

@Composable
fun HorizontalSegment(
    modifier: Modifier = Modifier,
    leftLabel: String = "Label",
    rightLabel: String = "Label",
    onLeftClick: () -> Unit = {},
    onRightClick: () -> Unit = {}
) {
    HorizontalDoubleButton(
        modifier = modifier,
        leftLabel = leftLabel,
        rightLabel = rightLabel,
        leftWidth = null,
        onLeftClick = onLeftClick,
        onRightClick = onRightClick
    )
}



@Preview(showBackground = false, name = "Asymmetric (Left Fixed 100dp)")
@Composable
private fun HorizontalFlexibleAsymmetricPreview() {
    HorizontalFlexibleAsymmetric()
}

@Preview(showBackground = false, name = "Segment (1:1 Equal Ratio)")
@Composable
private fun HorizontalSegmentPreview() {
    HorizontalSegment()
}