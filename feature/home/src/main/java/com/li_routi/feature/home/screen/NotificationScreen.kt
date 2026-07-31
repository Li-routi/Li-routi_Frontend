package com.li_routi.feature.home.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.LiroutiLineTab
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.home.component.NotificationDeleteBottomSheet
import com.li_routi.feature.home.component.NotificationListItem
import com.li_routi.feature.home.component.NotificationTopBar
import com.li_routi.feature.home.navigation.NotificationScreenActions
import com.li_routi.feature.home.vm.NotificationItemUiModel
import com.li_routi.feature.home.vm.NotificationTabLabels
import com.li_routi.feature.home.vm.SampleNotifications

/**
 * 알림 목록 화면 (스크린샷 + 와이어프레임 탭/빈 상태).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    actions: NotificationScreenActions,
    tabs: List<String>,
    selectedTabIndex: Int,
    notifications: List<NotificationItemUiModel>,
    showDeleteSheet: Boolean,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = LiroutiTheme.colors.backgroundDefault,
        topBar = {
            Column(modifier = Modifier.background(LiroutiTheme.colors.backgroundDefault)) {
                NotificationTopBar(
                    title = "알림",
                    onBackClick = actions::onBackClick,
                    onSettingsClick = actions::onSettingsClick,
                )
                LiroutiLineTab(
                    tabs = tabs,
                    selectedIndex = selectedTabIndex,
                    onTabSelected = actions::onTabSelected,
                )
            }
        },
    ) { innerPadding ->
        if (notifications.isEmpty()) {
            NotificationEmptyContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) {
                    items(items = notifications, key = { it.id }) { item ->
                        NotificationListItem(
                            item = item,
                            onClick = { actions.onNotificationClick(item.id) },
                            onMoreClick = { actions.onMoreClick(item.id) },
                        )
                    }
                }
                Text(
                    text = "7일 전 알림까지 확인할 수 있어요.",
                    style = LiroutiTheme.typography.body2,
                    color = LiroutiTheme.colors.labelInfo,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(vertical = 16.dp),
                )
            }
        }
    }

    if (showDeleteSheet) {
        NotificationDeleteBottomSheet(
            onDismissRequest = actions::onDismissDeleteSheet,
            onDeleteClick = actions::onConfirmDelete,
        )
    }
}

@Composable
private fun NotificationEmptyContent(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.warning),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelInfo),
            )
            Text(
                text = "알림이 없어요!",
                style = LiroutiTheme.typography.body2,
                color = LiroutiTheme.colors.labelInfo,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private object PreviewNotificationScreenActions : NotificationScreenActions {
    override fun onBackClick() = Unit
    override fun onSettingsClick() = Unit
    override fun onTabSelected(index: Int) = Unit
    override fun onNotificationClick(notificationId: String) = Unit
    override fun onMoreClick(notificationId: String) = Unit
    override fun onDismissDeleteSheet() = Unit
    override fun onConfirmDelete() = Unit
}

@Preview(showBackground = true, heightDp = 800, name = "목록")
@Composable
private fun NotificationScreenListPreview() {
    LiroutiFrontendTheme {
        NotificationScreen(
            actions = PreviewNotificationScreenActions,
            tabs = NotificationTabLabels,
            selectedTabIndex = 0,
            notifications = SampleNotifications,
            showDeleteSheet = false,
        )
    }
}

@Preview(showBackground = true, heightDp = 800, name = "빈 상태")
@Composable
private fun NotificationScreenEmptyPreview() {
    LiroutiFrontendTheme {
        NotificationScreen(
            actions = PreviewNotificationScreenActions,
            tabs = NotificationTabLabels,
            selectedTabIndex = 0,
            notifications = emptyList(),
            showDeleteSheet = false,
        )
    }
}

@Preview(showBackground = true, heightDp = 800, name = "삭제 시트")
@Composable
private fun NotificationScreenDeleteSheetPreview() {
    LiroutiFrontendTheme {
        NotificationScreen(
            actions = PreviewNotificationScreenActions,
            tabs = NotificationTabLabels,
            selectedTabIndex = 0,
            notifications = SampleNotifications.take(3),
            showDeleteSheet = true,
        )
    }
}
