package com.li_routi.feature.mypage.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * "프로필 수정" 화면 상단 바. Figma node `205:18110`("nav") 기준.
 *
 * 왼쪽 뒤로가기 chevron + 화면 중앙 정렬 타이틀 + (선택) 오른쪽 [trailingContent].
 */
@Composable
fun EditProfileTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable BoxScope.() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Image(
            painter = painterResource(id = R.drawable.chevron__left),
            contentDescription = "뒤로가기",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(20.dp)
                .clickable(onClick = onBackClick),
            colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelDefault),
        )
        Text(
            text = title,
            style = LiroutiTheme.typography.heading2Bold,
            color = LiroutiTheme.colors.labelDefault,
            modifier = Modifier.align(Alignment.Center),
        )
        if (trailingContent != null) {
            Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                trailingContent()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditProfileTopBarPreview() {
    LiroutiFrontendTheme {
        EditProfileTopBar(title = "프로필 수정", onBackClick = {})
    }
}
