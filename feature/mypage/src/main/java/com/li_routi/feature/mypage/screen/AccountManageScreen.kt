package com.li_routi.feature.mypage.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.LiroutiDivider
import com.li_routi.core.designsystem.component.LiroutiToast
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.mypage.component.ConfirmActionDialog
import com.li_routi.feature.mypage.component.EditProfileTopBar
import com.li_routi.feature.mypage.component.SettingsListItem
import com.li_routi.feature.mypage.component.SettingsSectionDividerColor
import com.li_routi.feature.mypage.component.SettingsSectionLabel

private const val LogoutToastMessage = "안전하게 로그아웃되었습니다."
private const val WithdrawToastMessage = "탈퇴가 완료되었습니다.\n그동안 이용해주셔서 감사합니다!"

/**
 * 계정 관리 화면. Figma node `205:18330`("계정 관리") 기준 — 마이페이지 "계정 관리" 메뉴로 진입한다.
 *
 * 로그인 정보(읽기 전용) + 기타(로그아웃/회원 탈퇴) 2개 섹션으로 구성된다. "회원 탈퇴"는
 * danger 색으로 강조한다.
 *
 * "로그아웃"/"회원 탈퇴" 탭 시 확인 모달(Figma node `205:18034`/`205:18032`)을 먼저 띄우고,
 * 모달에서 확정하면 [onLogoutClick]/[onWithdrawClick]를 호출함과 동시에 완료 토스트
 * (Figma node `205:18351`/`205:18350`)를 화면 하단에 띄운다. 실제 로그아웃/탈퇴 API 호출은
 * 아직 범위 밖이라 콜백만 노출한다.
 *
 * 토스트 배경은 Figma가 `backdrop-blur(8px)` + 반투명 `dimmer/default`를 쓰지만, 이 앱 minSdk(24)에서
 * 배경 블러를 구현할 방법이 마땅치 않아 기존 공용 [LiroutiToast]의 Black 스타일(불투명
 * `surfaceInverse`)을 그대로 재사용했다 — 블러 없이 살짝 더 진한 단색 배경이 되는 정도의 차이다.
 */
@Composable
fun AccountManageScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    loginInfo: String = "카카오 계정으로 로그인 중",
    onLogoutClick: () -> Unit = {},
    onWithdrawClick: () -> Unit = {},
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var showLogoutToast by remember { mutableStateOf(false) }
    var showWithdrawToast by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LiroutiTheme.colors.backgroundDefault),
        ) {
            EditProfileTopBar(title = "계정 관리", onBackClick = onBackClick)
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                SettingsSectionLabel(title = "로그인 정보")
                SettingsListItem(title = loginInfo)

                LiroutiDivider(color = SettingsSectionDividerColor, modifier = Modifier.padding(vertical = 8.dp))

                SettingsSectionLabel(title = "기타")
                SettingsListItem(title = "로그아웃", onClick = { showLogoutDialog = true })
                SettingsListItem(
                    title = "회원 탈퇴",
                    titleColor = LiroutiTheme.colors.dangerText,
                    onClick = { showWithdrawDialog = true },
                )
            }
        }

        if (showLogoutToast) {
            LiroutiToast(
                message = LogoutToastMessage,
                onCloseClick = { showLogoutToast = false },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            )
        }
        if (showWithdrawToast) {
            LiroutiToast(
                message = WithdrawToastMessage,
                onCloseClick = { showWithdrawToast = false },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            )
        }
    }

    if (showLogoutDialog) {
        ConfirmActionDialog(
            title = "로그아웃 하시겠습니까?",
            confirmText = "로그아웃",
            onCancel = { showLogoutDialog = false },
            onConfirm = {
                showLogoutDialog = false
                showLogoutToast = true
                onLogoutClick()
            },
        )
    }

    if (showWithdrawDialog) {
        ConfirmActionDialog(
            title = "탈퇴 하시겠습니까?",
            confirmText = "탈퇴하기",
            onCancel = { showWithdrawDialog = false },
            onConfirm = {
                showWithdrawDialog = false
                showWithdrawToast = true
                onWithdrawClick()
            },
        )
    }
}

@Preview(showBackground = true, heightDp = 800, name = "1. 기본")
@Composable
private fun AccountManageScreenPreview() {
    LiroutiFrontendTheme {
        AccountManageScreen(onBackClick = {})
    }
}

@Preview(showBackground = true, heightDp = 800, name = "2. 로그아웃 토스트")
@Composable
private fun AccountManageScreenLogoutToastPreview() {
    LiroutiFrontendTheme {
        Box {
            AccountManageScreen(onBackClick = {})
            LiroutiToast(
                message = LogoutToastMessage,
                onCloseClick = {},
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800, name = "3. 탈퇴 토스트")
@Composable
private fun AccountManageScreenWithdrawToastPreview() {
    LiroutiFrontendTheme {
        Box {
            AccountManageScreen(onBackClick = {})
            LiroutiToast(
                message = WithdrawToastMessage,
                onCloseClick = {},
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
            )
        }
    }
}
