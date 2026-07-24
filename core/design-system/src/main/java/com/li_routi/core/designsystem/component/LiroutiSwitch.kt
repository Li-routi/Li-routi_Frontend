package com.li_routi.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

private val TrackWidth = 42.dp
private val TrackHeight = 24.dp
private val ThumbSize = 20.dp
private val ThumbPadding = 2.dp
private const val DisabledAlpha = 0.4f


@Composable
fun LiroutiSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val trackColor by animateColorAsState(
        targetValue = if (checked) LiroutiTheme.colors.primaryNormal else LiroutiTheme.colors.surfaceContainerMid,
        label = "LiroutiSwitchTrackColor",
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) TrackWidth - ThumbSize - ThumbPadding else ThumbPadding,
        label = "LiroutiSwitchThumbOffset",
    )

    Box(
        modifier = modifier
            .alpha(if (enabled) 1f else DisabledAlpha)
            .size(width = TrackWidth, height = TrackHeight)
            .clip(RoundedCornerShape(percent = 50))
            .background(trackColor)
            .clickable(enabled = enabled) { onCheckedChange(!checked) },
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = thumbOffset)
                .size(ThumbSize)
                .clip(CircleShape)
                .background(LiroutiTheme.colors.labelReverse),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiSwitchPreview() {
    LiroutiFrontendTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            LiroutiSwitch(checked = true, onCheckedChange = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiSwitchAllStatesPreview() {
    LiroutiFrontendTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LiroutiSwitch(checked = false, onCheckedChange = {})
                LiroutiSwitch(checked = true, onCheckedChange = {})
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LiroutiSwitch(checked = false, onCheckedChange = {}, enabled = false)
                LiroutiSwitch(checked = true, onCheckedChange = {}, enabled = false)
            }
        }
    }
}
