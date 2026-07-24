package com.li_routi.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.DsPlaceholder
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

private data class HomeBottomNavTab(val label: String, val iconName: String)

// 아이콘 이름은 Figma `BottomGnb`의 `Component` variant(`property1`)와 동일하게 맞춘다: home/group/medal/ic_my
private val HomeBottomNavTabs = listOf(
    HomeBottomNavTab(label = "홈", iconName = "Icon/home"),
    HomeBottomNavTab(label = "그룹 루틴", iconName = "Icon/group"),
    HomeBottomNavTab(label = "챌린지", iconName = "Icon/medal"),
    HomeBottomNavTab(label = "마이", iconName = "Icon/ic_my"),
)

/**
 * 홈/그룹 루틴/챌린지/마이 4-tab 하단 GNB.
 *
 * 탭 선택은 화면 이동이 아닌 로컬 UI 상태이므로 이 컴포넌트 내부에서 직접 관리한다.
 * 아이콘은 Design System instance라 [DsPlaceholder]로 대체한다.
 *
 * edge-to-edge에서도 시스템 내비/제스처 바 바로 위에 보이도록 [navigationBarsPadding]을 적용한다.
 */
@Composable
fun HomeBottomNav(modifier: Modifier = Modifier) {
    var selectedIndex by remember { mutableIntStateOf(0) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(LiroutiTheme.colors.backgroundDefault)
            .navigationBarsPadding()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        HomeBottomNavTabs.forEachIndexed { index, tab ->
            HomeBottomNavItem(
                tab = tab,
                selected = index == selectedIndex,
                onClick = { selectedIndex = index },
            )
        }
    }
}

@Composable
private fun HomeBottomNavItem(
    tab: HomeBottomNavTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DsPlaceholder(
            componentName = tab.iconName,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = tab.label,
            style = LiroutiTheme.typography.caption,
            color = if (selected) LiroutiTheme.colors.primaryNormal else LiroutiTheme.colors.labelInfo,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeBottomNavPreview() {
    LiroutiFrontendTheme {
        HomeBottomNav()
    }
}
