package com.li_routi.feature.home.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.li_routi.feature.home.screen.NotificationSettingsScreen
import com.li_routi.feature.home.vm.NotificationSettingKey
import com.li_routi.feature.home.vm.NotificationViewModel

/** 알림 설정 화면 진입점. GET /api/notifications/settings로 서버 값을 불러와 보여준다. */
@Composable
fun NotificationSettingsRoute(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = viewModel(key = "notification_settings") {
        NotificationViewModel()
    },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.loadSettings()
    }

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
