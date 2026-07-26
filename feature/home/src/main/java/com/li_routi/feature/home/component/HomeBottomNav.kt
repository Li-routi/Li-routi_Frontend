package com.li_routi.feature.home.component

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

private data class HomeBottomNavTab(
    val label: String,
    @DrawableRes val defaultIcon: Int,
    @DrawableRes val activeIcon: Int,
)

private val HomeBottomNavTabs = listOf(
    HomeBottomNavTab("홈", R.drawable.home__default, R.drawable.home__active),
    HomeBottomNavTab("그룹 루틴", R.drawable.group__default, R.drawable.group__active),
    HomeBottomNavTab("챌린지", R.drawable.medal__default, R.drawable.medal__active),
    HomeBottomNavTab("마이", R.drawable.ic_my__default, R.drawable.ic_my__active),
)

/**
 * 홈/그룹 루틴/챌린지/마이 4-tab 하단 GNB.
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
        Image(
            painter = painterResource(id = if (selected) tab.activeIcon else tab.defaultIcon),
            contentDescription = tab.label,
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
