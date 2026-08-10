package com.li_routi.feature.home.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.di.NotificationContainer
import com.li_routi.core.domain.notification.AppNotification
import com.li_routi.core.domain.notification.GetNotificationsUseCase
import com.li_routi.core.domain.notification.MarkNotificationReadUseCase
import com.li_routi.core.domain.notification.NotificationCategory
import com.li_routi.feature.home.navigation.NotificationScreenActions
import com.li_routi.feature.home.navigation.NotificationSettingsScreenActions
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
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
 * 목록은 GET /api/notifications 커서 페이지네이션을 사용한다.
 * 설정 토글·삭제는 서버 API가 없어 로컬 state만 갱신한다.
 */
class NotificationViewModel(
    private val getNotificationsUseCase: GetNotificationsUseCase =
        NotificationContainer.getNotificationsUseCase,
    private val markNotificationReadUseCase: MarkNotificationReadUseCase =
        NotificationContainer.markNotificationReadUseCase,
    initialState: NotificationUiState = NotificationUiState(),
) : BaseViewModel(), NotificationScreenActions, NotificationSettingsScreenActions {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<NotificationUiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<NotificationUiEvent> = _uiEvent.asSharedFlow()

    private var loadGeneration = 0

    fun refresh() {
        loadNotifications(reset = true)
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.isLoading || state.isLoadingMore || !state.hasNext) return
        loadNotifications(reset = false)
    }

    override fun onBackClick() {
        emitEvent(NotificationUiEvent.NavigateBack)
    }

    override fun onSettingsClick() {
        emitEvent(NotificationUiEvent.NavigateToSettings)
    }

    override fun onTabSelected(index: Int) {
        if (_uiState.value.selectedTabIndex == index) return
        _uiState.update {
            it.copy(
                selectedTabIndex = index,
                notifications = emptyList(),
                nextCursor = null,
                hasNext = false,
                errorMessage = null,
            )
        }
        loadNotifications(reset = true)
    }

    override fun onNotificationClick(notificationId: String) {
        val id = notificationId.toLongOrNull() ?: return
        val item = _uiState.value.notifications.firstOrNull { it.id == notificationId } ?: return
        val wasUnread = item.isUnread

        _uiState.update { state ->
            state.copy(
                notifications = state.notifications.map { n ->
                    if (n.id == notificationId) n.copy(isUnread = false) else n
                },
            )
        }

        viewModelScope.launch {
            when (val result = markNotificationReadUseCase(id)) {
                is ResultState.Error -> {
                    // 실패 시 읽음 상태 롤백. 404는 목록을 다시 맞춘다.
                    if (wasUnread) {
                        _uiState.update { state ->
                            state.copy(
                                notifications = state.notifications.map { n ->
                                    if (n.id == notificationId) n.copy(isUnread = true) else n
                                },
                            )
                        }
                    }
                    val isNotFound = result.message.contains("찾을 수 없") ||
                        result.message.contains("404")
                    if (isNotFound) refresh()
                }
                is ResultState.Success, ResultState.Loading -> Unit
            }
        }
    }

    override fun onMoreClick(notificationId: String) {
        _uiState.update { it.copy(deleteTargetId = notificationId) }
    }

    override fun onDismissDeleteSheet() {
        _uiState.update { it.copy(deleteTargetId = null) }
    }

    override fun onConfirmDelete() {
        // DELETE 알림 API가 없어 로컬 목록에서만 제거한다.
        _uiState.update { state ->
            val id = state.deleteTargetId ?: return@update state
            state.copy(
                notifications = state.notifications.filterNot { it.id == id },
                deleteTargetId = null,
            )
        }
    }

    override fun onSettingToggle(key: NotificationSettingKey, checked: Boolean) {
        // 알림 설정 preference API가 없어 로컬 토글만 반영한다.
        _uiState.update { state ->
            state.copy(settingToggles = state.settingToggles + (key to checked))
        }
    }

    private fun loadNotifications(reset: Boolean) {
        val generation = ++loadGeneration
        val tab = NotificationTab.entries.getOrNull(_uiState.value.selectedTabIndex)
            ?: NotificationTab.All
        val category = tab.toApiCategory()
        val cursor = if (reset) null else _uiState.value.nextCursor

        _uiState.update {
            it.copy(
                isLoading = reset,
                isLoadingMore = !reset,
                errorMessage = null,
            )
        }

        viewModelScope.launch {
            when (
                val result = getNotificationsUseCase(
                    category = category,
                    cursor = cursor,
                    size = PageSize,
                )
            ) {
                is ResultState.Success -> {
                    if (generation != loadGeneration) return@launch
                    val mapped = result.data.notifications.map { it.toUiModel() }
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            notifications = if (reset) mapped else state.notifications + mapped,
                            nextCursor = result.data.nextCursor,
                            hasNext = result.data.hasNext && result.data.nextCursor != null,
                            errorMessage = null,
                        )
                    }
                }
                is ResultState.Error -> {
                    if (generation != loadGeneration) return@launch
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            errorMessage = result.message,
                        )
                    }
                }
                ResultState.Loading -> Unit
            }
        }
    }

    private fun emitEvent(event: NotificationUiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }

    private companion object {
        const val PageSize = 20
    }
}

private fun NotificationTab.toApiCategory(): NotificationCategory? = when (this) {
    NotificationTab.All -> null
    NotificationTab.MyRoutine -> NotificationCategory.PERSONAL_ROUTINE
    NotificationTab.GroupRoutine -> NotificationCategory.GROUP_ROUTINE
    NotificationTab.Challenge -> NotificationCategory.CHALLENGE
}

private fun AppNotification.toUiModel(): NotificationItemUiModel = NotificationItemUiModel(
    id = id.toString(),
    tab = category.toTab(),
    categoryLabel = category.toCategoryLabel(),
    title = title.ifBlank { body },
    timeLabel = createdAt.toRelativeTimeLabel(),
    isUnread = !read,
)

private fun NotificationCategory.toTab(): NotificationTab = when (this) {
    NotificationCategory.PERSONAL_ROUTINE -> NotificationTab.MyRoutine
    NotificationCategory.GROUP_ROUTINE -> NotificationTab.GroupRoutine
    NotificationCategory.CHALLENGE -> NotificationTab.Challenge
    NotificationCategory.CHAT, NotificationCategory.UNKNOWN -> NotificationTab.All
}

private fun NotificationCategory.toCategoryLabel(): String = when (this) {
    NotificationCategory.PERSONAL_ROUTINE -> "내루틴"
    NotificationCategory.GROUP_ROUTINE -> "그룹 루틴"
    NotificationCategory.CHALLENGE -> "챌린지"
    NotificationCategory.CHAT -> "채팅"
    NotificationCategory.UNKNOWN -> "알림"
}

// java.time은 minSdk 24에서 데스슈가링 없이 API 26+로 잡혀 IDE 오류가 난다.
// Calendar/SimpleDateFormat으로 상대 시각만 계산한다.
private fun String.toRelativeTimeLabel(): String {
    val millis = toEpochMillisOrNull() ?: return this
    val zone = TimeZone.getDefault()
    val created = Calendar.getInstance(zone).apply { timeInMillis = millis }
    val today = Calendar.getInstance(zone)
    val days = daysBetween(startOfDay(created), startOfDay(today))
    return when {
        days <= 0 -> {
            SimpleDateFormat("a h:mm", Locale.KOREAN).apply { timeZone = zone }
                .format(Date(millis))
        }
        days == 1 -> "어제"
        else -> "${days}일 전"
    }
}

private fun startOfDay(calendar: Calendar): Calendar =
    (calendar.clone() as Calendar).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

private fun daysBetween(start: Calendar, end: Calendar): Int {
    val diff = end.timeInMillis - start.timeInMillis
    return (diff / (24L * 60L * 60L * 1000L)).toInt()
}

private fun String.toEpochMillisOrNull(): Long? {
    val normalized = trim()
        .replace("Z", "+0000")
        .replace(Regex("([+-]\\d{2}):(\\d{2})$"), "$1$2")
    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
        "yyyy-MM-dd'T'HH:mm:ssZ",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd'T'HH:mm:ss",
    )
    for (pattern in patterns) {
        val millis = runCatching {
            SimpleDateFormat(pattern, Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
                isLenient = false
            }.parse(normalized)?.time
        }.getOrNull()
        if (millis != null) return millis
    }
    return null
}
