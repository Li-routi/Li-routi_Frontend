package com.li_routi.feature.mypage.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
 * 마이페이지 상단 바. Figma node `205:18080`("nav/my") 기준.
 *
 * "마이페이지" 타이틀 + 알림(`notification`)/설정(`settings`) 아이콘(각 20dp).
 */
@Composable
fun MyPageTopBar(
    onNotificationClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "마이페이지",
            style = LiroutiTheme.typography.heading2Bold,
            color = LiroutiTheme.colors.labelDefault,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.notification),
                contentDescription = "알림",
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = onNotificationClick),
                colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelDefault),
            )
            Image(
                painter = painterResource(id = R.drawable.settings),
                contentDescription = "설정",
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = onSettingsClick),
                colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelDefault),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageTopBarPreview() {
    LiroutiFrontendTheme {
        MyPageTopBar(onNotificationClick = {}, onSettingsClick = {})
    }
}
