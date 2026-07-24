package com.li_routi.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.foundation.typography.Pretendard
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

private val LabelTextStyle = TextStyle(
    fontFamily = Pretendard,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 22.sp,
    letterSpacing = (-0.025f).em,
)


@Composable
fun LiroutiLabel(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val backgroundColor: Color
    val border: BorderStroke?
    val textColor: Color
    when {
        selected -> {
            backgroundColor = LiroutiTheme.colors.primaryNormal
            border = null
            textColor = LiroutiTheme.colors.labelReverse
        }
        !enabled -> {
            backgroundColor = LiroutiTheme.colors.backgroundDefault
            border = BorderStroke(1.dp, LiroutiTheme.colors.outlinedMid)
            textColor = LiroutiTheme.colors.labelInfo
        }
        pressed -> {
            backgroundColor = LiroutiTheme.colors.backgroundAlternative
            border = BorderStroke(1.dp, LiroutiTheme.colors.outlinedMid)
            textColor = LiroutiTheme.colors.labelDefault
        }
        else -> {
            backgroundColor = LiroutiTheme.colors.backgroundDefault
            border = BorderStroke(1.dp, LiroutiTheme.colors.outlinedMid)
            textColor = LiroutiTheme.colors.labelDefault
        }
    }

    Row(
        modifier = modifier
            .height(38.dp)
            .background(backgroundColor, RoundedCornerShape(percent = 50))
            .then(if (border != null) Modifier.border(border, RoundedCornerShape(percent = 50)) else Modifier)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .padding(
                start = if (selected) 12.dp else 16.dp,
                end = 16.dp,
            ),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (selected) {
            LiroutiCheckmarkIcon(
                modifier = Modifier.size(16.dp),
                color = LiroutiTheme.colors.labelReverse,
            )
        }
        Text(text = text, style = LabelTextStyle, color = textColor)
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiLabelPreview() {
    LiroutiFrontendTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            LiroutiLabel(text = "Label", selected = true, onClick = {})
            LiroutiLabel(text = "Label", selected = false, onClick = {})
            LiroutiLabel(text = "Label", selected = false, onClick = {}, enabled = false)
        }
    }
}
