package com.li_routi.feature.mypage.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
 * 마이페이지 메뉴 목록 한 줄("내 인증", "업적", "리포트", "계정 관리", "앱 정보").
 * Figma node `205:18091`~`205:18103`("event") 기준 — 아이콘 16dp, 텍스트 14dp Medium, 높이 44dp.
 */
@Composable
fun MyPageMenuItem(
    @DrawableRes iconRes: Int,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(16.dp), contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelSub),
            )
        }
        Text(
            text = title,
            style = LiroutiTheme.typography.body2LongMedium,
            color = LiroutiTheme.colors.labelSub,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageMenuItemPreview() {
    LiroutiFrontendTheme {
        Column {
            MyPageMenuItem(iconRes = R.drawable.camera, title = "내 인증", onClick = {})
            MyPageMenuItem(iconRes = R.drawable.align_box_top_left, title = "업적", onClick = {})
            MyPageMenuItem(iconRes = R.drawable.report_data, title = "리포트", onClick = {})
            MyPageMenuItem(iconRes = R.drawable.user, title = "계정 관리", onClick = {})
            MyPageMenuItem(iconRes = R.drawable.warning, title = "앱 정보", onClick = {})
        }
    }
}
