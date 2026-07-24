package com.li_routi.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

enum class LiroutiCheckboxShape {
    Rect,
    Circle,
}

@Composable
fun LiroutiCheckbox(
    checked: Boolean,
    modifier: Modifier = Modifier,
    shape: LiroutiCheckboxShape = LiroutiCheckboxShape.Rect,
    onCheckedChange: ((Boolean) -> Unit)? = null,
) {
    val boxShape: Shape = if (shape == LiroutiCheckboxShape.Circle) CircleShape else RoundedCornerShape(2.dp)

    Box(
        modifier = modifier
            .size(16.dp)
            .then(
                if (checked) {
                    Modifier.background(LiroutiTheme.colors.primaryNormal, boxShape)
                } else {
                    Modifier.border(1.dp, LiroutiTheme.colors.borderStrong, boxShape)
                },
            )
            .then(
                if (onCheckedChange != null) {
                    Modifier.clickable { onCheckedChange(!checked) }
                } else {
                    Modifier
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            LiroutiCheckmarkIcon(
                modifier = Modifier.size(12.dp),
                color = LiroutiTheme.colors.labelReverse,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiCheckboxPreview() {
    LiroutiFrontendTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LiroutiCheckbox(checked = false)
            LiroutiCheckbox(checked = true)
            LiroutiCheckbox(checked = false, shape = LiroutiCheckboxShape.Circle)
            LiroutiCheckbox(checked = true, shape = LiroutiCheckboxShape.Circle)
        }
    }
}
