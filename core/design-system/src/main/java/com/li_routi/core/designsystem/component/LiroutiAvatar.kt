package com.li_routi.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.foundation.color.Neutral97
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

private val AvatarRingWidth = 1.5.dp

@Composable
fun LiroutiAvatar(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    content: (@Composable BoxScope.() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Neutral97)
            .border(1.dp, LiroutiTheme.colors.borderDefault, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (content != null) {
            content()
        } else {
            LiroutiAvatarPlaceholderIcon(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun LiroutiAvatarGroup(
    count: Int,
    modifier: Modifier = Modifier,
    avatarSize: Dp = 40.dp,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(-8.dp),
    ) {
        repeat(count) {
            Box(
                modifier = Modifier
                    .size(avatarSize + AvatarRingWidth * 2)
                    .clip(CircleShape)
                    .background(LiroutiTheme.colors.backgroundDefault)
                    .border(AvatarRingWidth, LiroutiTheme.colors.borderAlternative, CircleShape)
                    .padding(AvatarRingWidth),
                contentAlignment = Alignment.Center,
            ) {
                LiroutiAvatar(size = avatarSize)
            }
        }
    }
}

@Composable
private fun LiroutiAvatarPlaceholderIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
) {
    Canvas(modifier = modifier) {

        val headRadius = size.minDimension * 0.12f
        val headCenter = Offset(size.width / 2f, size.height * 0.37f)
        drawCircle(color = color, radius = headRadius, center = headCenter)


        val domeWidth = size.width * 0.49f
        val domeHeight = size.height * 0.22f
        val domeBottom = size.height * 0.76f
        val domeLeft = (size.width - domeWidth) / 2f

        val topCornerRadius = CornerRadius(domeHeight * 0.78f)
        val bottomCornerRadius = CornerRadius(domeHeight * 0.22f)
        val path = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(domeLeft, domeBottom - domeHeight, domeLeft + domeWidth, domeBottom),
                    topLeft = topCornerRadius,
                    topRight = topCornerRadius,
                    bottomRight = bottomCornerRadius,
                    bottomLeft = bottomCornerRadius,
                ),
            )
        }
        drawPath(path = path, color = color)
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiAvatarPreview() {
    LiroutiFrontendTheme {
        LiroutiAvatar(modifier = Modifier.padding(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiAvatarGroupPreview() {
    LiroutiFrontendTheme {
        LiroutiAvatarGroup(count = 5, modifier = Modifier.padding(16.dp))
    }
}
