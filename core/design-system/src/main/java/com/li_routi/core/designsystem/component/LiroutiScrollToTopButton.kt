package com.li_routi.core.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * 목록을 스크롤한 만큼 우하단에 뜨는 "맨 위로" 버튼. [visible]은 스크롤 오프셋 등 호출부가 판단해 넘긴다.
 */
@Composable
fun LiroutiScrollToTopButton(
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .shadow(elevation = 6.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(LiroutiTheme.colors.backgroundDefault)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            LiroutiArrowUpIcon(modifier = Modifier.size(20.dp), color = LiroutiTheme.colors.labelDefault)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiScrollToTopButtonPreview() {
    LiroutiFrontendTheme {
        LiroutiScrollToTopButton(visible = true, onClick = {})
    }
}
