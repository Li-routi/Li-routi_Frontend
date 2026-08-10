package com.li_routi.feature.home.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.li_routi.feature.home.screen.NotificationScreen
import com.li_routi.feature.home.vm.NotificationUiEvent
import com.li_routi.feature.home.vm.NotificationViewModel

/**
 * 알림 목록 화면 진입점.
 */
@Composable
fun NotificationRoute(
    onEvent: (NotificationUiEvent) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = viewModel { NotificationViewModel() },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.refresh()
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            onEvent(event)
        }
    }

    NotificationScreen(
        actions = viewModel,
        tabs = uiState.tabs,
        selectedTabIndex = uiState.selectedTabIndex,
        notifications = uiState.filteredNotifications,
        showDeleteSheet = uiState.deleteTargetId != null,
        isLoading = uiState.isLoading,
        hasNext = uiState.hasNext,
        errorMessage = uiState.errorMessage,
        onLoadMore = viewModel::loadMore,
        onRetryClick = viewModel::refresh,
        modifier = modifier,
    )
}
