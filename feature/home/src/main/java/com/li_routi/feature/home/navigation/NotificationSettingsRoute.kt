package com.li_routi.feature.home.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.li_routi.feature.home.screen.NotificationSettingsScreen
import com.li_routi.feature.home.vm.NotificationSettingKey
import com.li_routi.feature.home.vm.NotificationViewModel

/**
 * 알림 설정 화면 진입점.
 *
 * API 연동 전: 설정 토글은 이 화면 ViewModel 로컬 state로만 유지한다.
 */
@Composable
fun NotificationSettingsRoute(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = viewModel(key = "notification_settings") {
        NotificationViewModel()
    },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NotificationSettingsScreen(
        actions = object : NotificationSettingsScreenActions {
            override fun onBackClick() = onNavigateBack()
            override fun onSettingToggle(key: NotificationSettingKey, checked: Boolean) {
                viewModel.onSettingToggle(key, checked)
            }
        },
        toggles = uiState.settingToggles,
        modifier = modifier,
    )
}
