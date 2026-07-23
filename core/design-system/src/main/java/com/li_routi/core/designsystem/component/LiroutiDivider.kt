package com.li_routi.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

enum class LiroutiDividerOrientation {
    Horizontal,
    Vertical,
}

enum class LiroutiDividerThickness(val value: Dp) {
    Thin(1.dp),
    Regular(2.dp),
    Bold(4.dp),
    ExtraBold(8.dp),
}

@Composable
fun LiroutiDivider(
    modifier: Modifier = Modifier,
    orientation: LiroutiDividerOrientation = LiroutiDividerOrientation.Horizontal,
    thickness: LiroutiDividerThickness = LiroutiDividerThickness.Thin,
    color: Color = LiroutiTheme.colors.borderStrong,
) {
    val sizeModifier = when (orientation) {
        LiroutiDividerOrientation.Horizontal -> Modifier
            .fillMaxWidth()
            .height(thickness.value)

        LiroutiDividerOrientation.Vertical -> Modifier
            .fillMaxHeight()
            .width(thickness.value)
    }

    Spacer(
        modifier = modifier
            .then(sizeModifier)
            .background(color = color),
    )
}

@Preview(showBackground = true)
@Composable
private fun LiroutiDividerHorizontalPreview() {
    LiroutiFrontendTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            LiroutiDividerThickness.entries.forEach { thickness ->
                LiroutiDivider(
                    orientation = LiroutiDividerOrientation.Horizontal,
                    thickness = thickness,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiDividerVerticalPreview() {
    LiroutiFrontendTheme {
        Row(modifier = Modifier.padding(16.dp)) {
            LiroutiDividerThickness.entries.forEach { thickness ->
                LiroutiDivider(
                    orientation = LiroutiDividerOrientation.Vertical,
                    thickness = thickness,
                    modifier = Modifier
                        .height(120.dp)
                        .padding(horizontal = 8.dp),
                )
            }
        }
    }
}
