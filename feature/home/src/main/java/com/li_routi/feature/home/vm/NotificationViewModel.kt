package com.li_routi.feature.home.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.feature.home.navigation.NotificationScreenActions
import com.li_routi.feature.home.navigation.NotificationSettingsScreenActions
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 알림 목록·설정 ViewModel.
 *
 * API 연동 전: 샘플 목록 필터/읽음/삭제·설정 토글은 로컬 state만 갱신한다.
 */
class NotificationViewModel(
    initialState: NotificationUiState = NotificationUiState(),
) : BaseViewModel(), NotificationScreenActions, NotificationSettingsScreenActions {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<NotificationUiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<NotificationUiEvent> = _uiEvent.asSharedFlow()

    override fun onBackClick() {
        emitEvent(NotificationUiEvent.NavigateBack)
    }

    override fun onSettingsClick() {
        emitEvent(NotificationUiEvent.NavigateToSettings)
    }

    override fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
    }

    override fun onNotificationClick(notificationId: String) {
        val item = _uiState.value.notifications.find { it.id == notificationId } ?: return
        _uiState.update { state ->
            state.copy(
                notifications = state.notifications.map { notification ->
                    if (notification.id == notificationId) {
                        notification.copy(isUnread = false)
                    } else {
                        notification
                    }
                },
            )
        }
        when (item.tab) {
            NotificationTab.MyRoutine -> emitEvent(NotificationUiEvent.NavigateToMyRoutine)
            NotificationTab.GroupRoutine -> emitEvent(NotificationUiEvent.NavigateToGroupRoutine)
            NotificationTab.Challenge -> emitEvent(NotificationUiEvent.NavigateToChallenge)
            // 필터 탭용. 항목에는 쓰이지 않지만 안전 분기.
            NotificationTab.All -> Unit
        }
    }

    override fun onMoreClick(notificationId: String) {
        _uiState.update { it.copy(deleteTargetId = notificationId) }
    }

    override fun onDismissDeleteSheet() {
        _uiState.update { it.copy(deleteTargetId = null) }
    }

    override fun onConfirmDelete() {
        _uiState.update { state ->
            val id = state.deleteTargetId ?: return@update state
            state.copy(
                notifications = state.notifications.filterNot { it.id == id },
                deleteTargetId = null,
            )
        }
    }

    override fun onSettingToggle(key: NotificationSettingKey, checked: Boolean) {
        // API 연동 전: 로컬 토글만 반영
        _uiState.update { state ->
            state.copy(settingToggles = state.settingToggles + (key to checked))
        }
    }

    private fun emitEvent(event: NotificationUiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}
