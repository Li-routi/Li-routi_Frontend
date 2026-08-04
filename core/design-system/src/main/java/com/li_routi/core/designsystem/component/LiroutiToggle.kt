package com.li_routi.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme

private val ToggleWidth = 42.dp
private val ToggleHeight = 24.dp
private val ToggleThumbSize = 20.dp
private val ToggleThumbInset = 2.dp
private val ToggleOffColor = Color(0xFF636D74)
private val ToggleOnColor = Color(0xFF338AFF)

@Composable
fun LiroutiToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val trackColor by animateColorAsState(
        targetValue = if (checked) ToggleOnColor else ToggleOffColor,
        label = "LiroutiToggleTrackColor",
    )
    val thumbOffsetX by animateDpAsState(
        targetValue = if (checked) {
            ToggleWidth - ToggleThumbSize - ToggleThumbInset
        } else {
            ToggleThumbInset
        },
        label = "LiroutiToggleThumbOffset",
    )

    Box(
        modifier = modifier
            .size(width = ToggleWidth, height = ToggleHeight)
            .clip(RoundedCornerShape(50))
            .background(trackColor)
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Switch,
            ),
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffsetX, y = ToggleThumbInset)
                .size(ToggleThumbSize)
                .background(color = Color.White, shape = CircleShape),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiTogglePreview() {
    LiroutiFrontendTheme {
        var checked by remember { mutableStateOf(false) }
        LiroutiToggle(checked = checked, onCheckedChange = { checked = it })
    }
}