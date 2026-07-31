package com.li_routi.feature.home.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.LiroutiSwitch
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.home.component.NotificationTopBar
import com.li_routi.feature.home.navigation.NotificationSettingsScreenActions
import com.li_routi.feature.home.vm.NotificationSettingItems
import com.li_routi.feature.home.vm.NotificationSettingKey
import com.li_routi.feature.home.vm.NotificationSettingUiModel

/**
 * 알림 설정 화면 (스크린샷 / 와이어프레임 `알림설정`).
 */
@Composable
fun NotificationSettingsScreen(
    actions: NotificationSettingsScreenActions,
    toggles: Map<NotificationSettingKey, Boolean>,
    settings: List<NotificationSettingUiModel> = NotificationSettingItems,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = LiroutiTheme.colors.backgroundDefault,
        topBar = {
            NotificationTopBar(
                title = "알림 설정",
                onBackClick = actions::onBackClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(top = 25.dp),
        ) {
            settings.forEach { item ->
                NotificationSettingRow(
                    title = item.title,
                    description = item.description,
                    checked = toggles[item.key] == true,
                    onCheckedChange = { checked ->
                        actions.onSettingToggle(item.key, checked)
                    },
                )
            }
        }
    }
}

@Composable
private fun NotificationSettingRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(LiroutiTheme.colors.backgroundDefault)
            .padding(horizontal = 16.dp, vertical = 15.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = LiroutiTheme.typography.body2,
                color = LiroutiTheme.colors.labelDefault,
            )
            Text(
                text = description,
                style = LiroutiTheme.typography.caption,
                color = LiroutiTheme.colors.labelInfo,
            )
        }
        LiroutiSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

private object PreviewNotificationSettingsScreenActions : NotificationSettingsScreenActions {
    override fun onBackClick() = Unit
    override fun onSettingToggle(key: NotificationSettingKey, checked: Boolean) = Unit
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun NotificationSettingsScreenPreview() {
    LiroutiFrontendTheme {
        NotificationSettingsScreen(
            actions = PreviewNotificationSettingsScreenActions,
            toggles = NotificationSettingKey.entries.associateWith { true },
        )
    }
}
