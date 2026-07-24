package com.li_routi.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

private val TrackWidth = 42.dp
private val TrackHeight = 24.dp
private val ThumbSize = 20.dp
private val ThumbPadding = 2.dp

@Composable
fun LiroutiSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val trackColor by animateColorAsState(
        targetValue = if (checked) LiroutiTheme.colors.primaryNormal else LiroutiTheme.colors.backgroundStrong,
        label = "LiroutiSwitchTrackColor",
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) TrackWidth - ThumbSize - ThumbPadding else ThumbPadding,
        label = "LiroutiSwitchThumbOffset",
    )

    Box(
        modifier = modifier
            .size(width = TrackWidth, height = TrackHeight)
            .clip(RoundedCornerShape(percent = 50))
            .background(trackColor)
            .clickable { onCheckedChange(!checked) },
    ) {
        Box(
            modifier = Modifier
                .padding(start = thumbOffset)
                .align(Alignment.CenterStart)
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
