package com.li_routi.core.common.ui.nav

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.LiroutiDivider
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

private data class AppBottomNavItem(
    val tab: AppBottomTab,
    val label: String,
    @DrawableRes val defaultIcon: Int,
    @DrawableRes val activeIcon: Int,
)

private val AppBottomNavItems = listOf(
    AppBottomNavItem(AppBottomTab.Home, "홈", R.drawable.home__default, R.drawable.home__active),
    AppBottomNavItem(AppBottomTab.GroupRoutine, "그룹 루틴", R.drawable.group__default, R.drawable.group__active),
    AppBottomNavItem(AppBottomTab.Challenge, "챌린지", R.drawable.medal__default, R.drawable.medal__active),
    AppBottomNavItem(AppBottomTab.My, "마이", R.drawable.ic_my__default, R.drawable.ic_my__active),
)

/**
 * 홈/그룹 루틴/챌린지/마이 4-tab 공용 하단 GNB.
 *
 * 각 feature의 탭 루트 화면에서만 노출한다 — 상세처럼 쌓인 화면(예: 챌린지 상세, 모임방 상세)에는
 * 노출하지 않는다. 예전에는 feature마다 똑같이 생긴 바를 따로 그렸는데, 실제 탭 전환이 되도록
 * 이 컴포넌트 하나로 합쳤다.
 */
@Composable
fun AppBottomNavBar(
    selectedTab: AppBottomTab,
    onTabSelected: (AppBottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        LiroutiDivider(color = LiroutiTheme.colors.borderDefault)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LiroutiTheme.colors.backgroundDefault)
                .navigationBarsPadding()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            AppBottomNavItems.forEach { item ->
                val selected = item.tab == selectedTab
                Column(
                    modifier = Modifier.clickable(onClick = { onTabSelected(item.tab) }),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(id = if (selected) item.activeIcon else item.defaultIcon),
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp),
                    )
                    Text(
                        text = item.label,
                        style = LiroutiTheme.typography.caption,
                        color = if (selected) LiroutiTheme.colors.primaryNormal else LiroutiTheme.colors.labelInfo,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppBottomNavBarPreview() {
    LiroutiFrontendTheme {
        AppBottomNavBar(selectedTab = AppBottomTab.Home, onTabSelected = {})
    }
}
