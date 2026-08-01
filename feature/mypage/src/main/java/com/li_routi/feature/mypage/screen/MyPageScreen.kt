package com.li_routi.feature.mypage.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.common.ui.nav.AppBottomNavBar
import com.li_routi.core.common.ui.nav.AppBottomTab
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.LiroutiDivider
import com.li_routi.core.designsystem.component.LiroutiDividerThickness
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.mypage.component.MyPageMenuItem
import com.li_routi.feature.mypage.component.MyPageProfileHeader
import com.li_routi.feature.mypage.component.MyPageTopBar
import com.li_routi.feature.mypage.navigation.MyPageScreenActions

/**
 * 마이페이지 화면. Figma node `205:18077`("마이") 기준.
 *
 * 프로필(아바타/닉네임/이메일/프로필 수정) + 메뉴 목록(내 인증/업적/리포트/계정 관리/앱 정보) +
 * 공용 하단 GNB로 구성된다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageScreen(
    actions: MyPageScreenActions,
    onTabSelected: (AppBottomTab) -> Unit,
    nickname: String,
    email: String,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = LiroutiTheme.colors.backgroundDefault,
        topBar = {
            MyPageTopBar(
                onNotificationClick = actions::onNotificationClick,
                onSettingsClick = actions::onSettingsClick,
            )
        },
        bottomBar = { AppBottomNavBar(selectedTab = AppBottomTab.My, onTabSelected = onTabSelected) },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            MyPageProfileHeader(
                nickname = nickname,
                email = email,
                onEditProfileClick = actions::onEditProfileClick,
            )
            LiroutiDivider(
                thickness = LiroutiDividerThickness.ExtraBold,
                color = LiroutiTheme.colors.backgroundAlternative,
            )
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
                MyPageMenuItem(iconRes = R.drawable.camera, title = "내 인증", onClick = actions::onMyVerificationClick)
                MyPageMenuItem(iconRes = R.drawable.align_box_top_left, title = "업적", onClick = actions::onAchievementClick)
                MyPageMenuItem(iconRes = R.drawable.report_data, title = "리포트", onClick = actions::onReportClick)
                MyPageMenuItem(iconRes = R.drawable.user, title = "계정 관리", onClick = actions::onAccountManageClick)
                MyPageMenuItem(iconRes = R.drawable.warning, title = "앱 정보", onClick = actions::onAppInfoClick)
            }
        }
    }
}

private object PreviewMyPageScreenActions : MyPageScreenActions {
    override fun onNotificationClick() = Unit
    override fun onSettingsClick() = Unit
    override fun onEditProfileClick() = Unit
    override fun onMyVerificationClick() = Unit
    override fun onAchievementClick() = Unit
    override fun onReportClick() = Unit
    override fun onAccountManageClick() = Unit
    override fun onAppInfoClick() = Unit
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun MyPageScreenPreview() {
    LiroutiFrontendTheme {
        MyPageScreen(
            actions = PreviewMyPageScreenActions,
            onTabSelected = {},
            nickname = "잠자는개구리",
            email = "example@gamil.com",
        )
    }
}
