package com.li_routi.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.DsPlaceholder
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * 홈 화면 상단 바. Figma node `2187:20859`("nav") 기준.
 *
 * 로고(16dp, 비-DS 브랜드 이미지) + "LI-ROUTI" 타이틀 + 루틴 추가(`add--alt`)/알림(`notification`)
 * 아이콘(각 20dp)으로 구성한다. 아이콘은 Design System `Icon` instance라 [DsPlaceholder]로 대체한다.
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
        // 브랜드 로고 (비-DS 이미지 자산) — 실제 에셋 반영은 이번 범위 제외
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(LiroutiTheme.colors.backgroundStrong),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "LI-ROUTI",
            style = LiroutiTheme.typography.heading2,
            color = LiroutiTheme.colors.labelStrong,
            modifier = Modifier.weight(1f),
        )
        DsPlaceholder(
            componentName = "Icon/add--alt",
            modifier = Modifier
                .size(20.dp)
                .clickable(onClick = onAddRoutineClick),
        )
        Spacer(modifier = Modifier.width(14.dp))
        DsPlaceholder(
            componentName = "Icon/notification",
            modifier = Modifier
                .size(20.dp)
                .clickable(onClick = onNotificationClick),
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
