package com.li_routi.feature.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/** 리포트 화면 기간 전환("주간"/"월간"). Figma node `205:18361`("Tab") 기준 — 알약 모양 세그먼트 컨트롤. */
@Composable
fun ReportPeriodTabs(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .width(200.dp)
            .height(40.dp)
            .background(LiroutiTheme.colors.backgroundAlternative, RoundedCornerShape(percent = 50))
            .padding(3.dp),
    ) {
        tabs.forEachIndexed { index, title ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .then(
                        if (selected) {
                            Modifier.shadow(elevation = 2.dp, shape = RoundedCornerShape(percent = 50))
                        } else {
                            Modifier
                        },
                    )
                    .clip(RoundedCornerShape(percent = 50))
                    .then(
                        if (selected) {
                            Modifier.background(LiroutiTheme.colors.backgroundDefault)
                        } else {
                            Modifier
                        },
                    )
                    .clickable(onClick = { onTabSelected(index) }),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = title,
                    style = if (selected) LiroutiTheme.typography.body3SemiBold else LiroutiTheme.typography.body3Medium,
                    color = if (selected) LiroutiTheme.colors.labelDefault else LiroutiTheme.colors.labelInfo,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReportPeriodTabsPreview() {
    LiroutiFrontendTheme {
        var selectedIndex by remember { mutableIntStateOf(0) }
        ReportPeriodTabs(
            tabs = listOf("주간", "월간"),
            selectedIndex = selectedIndex,
            onTabSelected = { selectedIndex = it },
            modifier = Modifier.padding(16.dp),
        )
    }
}
