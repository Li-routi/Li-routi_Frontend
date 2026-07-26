package com.li_routi.feature.home.component

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * 홈 화면 상단 바. Figma node `2187:20859`("nav") 기준.
 *
 * 로고 + "LI-ROUTI" 타이틀 + 루틴 추가(`add__alt`)/알림(`notification`) 아이콘(각 20dp).
 */
@Composable
fun HomeTopBar(
    onAddRoutineClick: () -> Unit,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "LI-ROUTI",
            modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "LI-ROUTI",
            style = LiroutiTheme.typography.heading2,
            color = LiroutiTheme.colors.labelStrong,
            modifier = Modifier.weight(1f),
        )
        Image(
            painter = painterResource(id = R.drawable.add__alt),
            contentDescription = "루틴 추가",
            modifier = Modifier
                .size(20.dp)
                .clickable(onClick = onAddRoutineClick),
            colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelStrong),
        )
        Spacer(modifier = Modifier.width(14.dp))
        Image(
            painter = painterResource(id = R.drawable.notification),
            contentDescription = "알림",
            modifier = Modifier
                .size(20.dp)
                .clickable(onClick = onNotificationClick),
            colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelStrong),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeTopBarPreview() {
    LiroutiFrontendTheme {
        HomeTopBar(onAddRoutineClick = {}, onNotificationClick = {})
    }
}
