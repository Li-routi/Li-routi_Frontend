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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.foundation.color.Neutral60
import com.li_routi.core.designsystem.foundation.color.Neutral98
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

private val AvatarRingWidth = 1.5.dp
private val PlaceholderBackgroundColor = Neutral98
private val PlaceholderIconColor = Neutral60

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
            .background(PlaceholderBackgroundColor)
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

/**
 * Figma node `6389:25755`("기본 이미지") 실측 기준 — 머리(원)와 몸통(아래로 잘려 돔처럼 보이는 큰 원)
 * 두 개의 원만으로 구성된다. 몸통 원의 중심은 캔버스 아래로 벗어나 있는데, 바깥 [LiroutiAvatar]의
 * `CircleShape` 클립이 그 넘친 부분을 잘라내 돔 모양으로 보이게 한다.
 */
@Composable
private fun LiroutiAvatarPlaceholderIcon(
    modifier: Modifier = Modifier,
    color: Color = PlaceholderIconColor,
) {
    Canvas(modifier = modifier) {
        val headRadius = size.minDimension * 0.2f
        val headCenter = Offset(size.width / 2f, size.height * 0.41f)
        drawCircle(color = color, radius = headRadius, center = headCenter)

        val bodyRadius = size.minDimension * 0.38f
        val bodyCenter = Offset(size.width / 2f, size.height * 1.05f)
        drawCircle(color = color, radius = bodyRadius, center = bodyCenter)
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
