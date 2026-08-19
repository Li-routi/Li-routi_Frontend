package com.li_routi.feature.mypage.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.LiroutiDivider
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.mypage.component.EditProfileTopBar
import com.li_routi.feature.mypage.component.SettingsListItem
import com.li_routi.feature.mypage.component.SettingsSectionDividerColor
import com.li_routi.feature.mypage.component.SettingsSectionLabel

/**
 * 앱 정보 화면. Figma node `205:18308`("앱 정보") 기준 — 마이페이지 "앱 정보" 메뉴로 진입한다.
 *
 * 고객 지원/기타/앱 버전 3개 섹션의 목록 화면. 각 항목의 실제 이동 대상 화면은 아직 범위 밖이라
 * 콜백만 노출한다.
 */
@Composable
fun AppInfoScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    appVersion: String = "v1.0.0",
    onNoticeClick: () -> Unit = {},
    onSuggestionClick: () -> Unit = {},
    onTermsOfServiceClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
    onCoinRefundPolicyClick: () -> Unit = {},
    onReportPolicyClick: () -> Unit = {},
    onOpenSourceLicenseClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundDefault),
    ) {
        EditProfileTopBar(title = "앱 정보", onBackClick = onBackClick)
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            SettingsSectionLabel(title = "고객 지원")
            SettingsListItem(title = "공지사항", onClick = onNoticeClick)
            SettingsListItem(title = "건의하기", onClick = onSuggestionClick)

            LiroutiDivider(color = SettingsSectionDividerColor, modifier = Modifier.padding(vertical = 8.dp))

            SettingsSectionLabel(title = "기타")
            SettingsListItem(title = "이용약관", onClick = onTermsOfServiceClick)
            SettingsListItem(title = "개인정보 처리방침", onClick = onPrivacyPolicyClick)
            SettingsListItem(title = "코인 및 환불 정책", onClick = onCoinRefundPolicyClick)
            SettingsListItem(title = "신고 및 운영 정책", onClick = onReportPolicyClick)
            SettingsListItem(title = "오픈소스 라이선스", onClick = onOpenSourceLicenseClick)

            LiroutiDivider(color = SettingsSectionDividerColor, modifier = Modifier.padding(vertical = 8.dp))

            SettingsSectionLabel(title = "앱 버전")
            SettingsListItem(title = appVersion)
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun AppInfoScreenPreview() {
    LiroutiFrontendTheme {
        AppInfoScreen(onBackClick = {})
    }
}
