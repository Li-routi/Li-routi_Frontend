package com.li_routi.feature.home.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.di.NotificationContainer
import com.li_routi.core.domain.notification.AppNotification
import com.li_routi.core.domain.notification.GetNotificationSettingsUseCase
import com.li_routi.core.domain.notification.GetNotificationsUseCase
import com.li_routi.core.domain.notification.MarkNotificationReadUseCase
import com.li_routi.core.domain.notification.NotificationCategory
import com.li_routi.core.domain.notification.NotificationNavigationTarget
import com.li_routi.core.domain.notification.NotificationSettings
import com.li_routi.core.domain.notification.NotificationSettingsUpdate
import com.li_routi.core.domain.notification.UpdateNotificationSettingsUseCase
import com.li_routi.core.domain.notification.resolveNotificationNavigationTarget
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
 * 설정은 GET/PATCH /api/notifications/settings로 실제 조회·변경한다. 삭제는 서버 API가 없어
 * 로컬 state만 갱신한다.
 */
class NotificationViewModel(
    private val getNotificationsUseCase: GetNotificationsUseCase =
        NotificationContainer.getNotificationsUseCase,
    private val markNotificationReadUseCase: MarkNotificationReadUseCase =
        NotificationContainer.markNotificationReadUseCase,
    private val getNotificationSettingsUseCase: GetNotificationSettingsUseCase =
        NotificationContainer.getNotificationSettingsUseCase,
    private val updateNotificationSettingsUseCase: UpdateNotificationSettingsUseCase =
        NotificationContainer.updateNotificationSettingsUseCase,
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
        emitDeepLinkEvent(item.type, item.referenceId)

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

    private fun emitDeepLinkEvent(type: String, referenceId: Long?) {
        val event = when (val target = resolveNotificationNavigationTarget(type, referenceId)) {
            NotificationNavigationTarget.PersonalRoutine -> NotificationUiEvent.NavigateToPersonalRoutine
            NotificationNavigationTarget.GroupRoutine -> NotificationUiEvent.NavigateToGroupRoutine
            NotificationNavigationTarget.ChallengeHome -> NotificationUiEvent.NavigateToChallengeHome
            is NotificationNavigationTarget.ChallengeDetail ->
                NotificationUiEvent.NavigateToChallengeDetail(target.challengeId)
            null -> return
        }
        emitEvent(event)
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

    /** 알림 설정 화면 진입 시 서버 값으로 토글을 맞춘다([NotificationSettingsRoute]에서 호출). */
    fun loadSettings() {
        viewModelScope.launch {
            when (val result = getNotificationSettingsUseCase()) {
                is ResultState.Success -> _uiState.update { it.copy(settingToggles = result.data.toToggleMap()) }
                is ResultState.Error, ResultState.Loading -> Unit
            }
        }
    }

    /**
     * 토글은 응답을 기다리지 않고 바로 반영한다(낙관적 갱신) — 스위치가 눌러도 잠깐 안 움직이는 것처럼
     * 보이면 안 되기 때문. 실패하면 눌러진 값을 되돌린다.
     */
    override fun onSettingToggle(key: NotificationSettingKey, checked: Boolean) {
        val previous = _uiState.value.settingToggles[key] == true
        if (previous == checked) return
        _uiState.update { state -> state.copy(settingToggles = state.settingToggles + (key to checked)) }
        viewModelScope.launch {
            when (val result = updateNotificationSettingsUseCase(key.toUpdate(checked))) {
                is ResultState.Success -> _uiState.update { it.copy(settingToggles = result.data.toToggleMap()) }
                is ResultState.Error -> _uiState.update { state ->
                    state.copy(settingToggles = state.settingToggles + (key to previous))
                }
                ResultState.Loading -> Unit
            }
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
    type = type,
    referenceId = referenceId,
)

private fun NotificationSettings.toToggleMap(): Map<NotificationSettingKey, Boolean> = mapOf(
    NotificationSettingKey.RoutineDeadline to routineDeadlineEnabled,
    NotificationSettingKey.NewVerification to newVerificationEnabled,
    NotificationSettingKey.MyVerificationReaction to verificationReactionEnabled,
    NotificationSettingKey.Nudge to pokeEnabled,
    NotificationSettingKey.NewChat to newChatEnabled,
    NotificationSettingKey.Like to likeEnabled,
)

/** [key] 하나만 [checked]로 바꾸는 부분 업데이트 요청 — 나머지 필드는 null로 둬서 기존 값을 유지한다. */
private fun NotificationSettingKey.toUpdate(checked: Boolean): NotificationSettingsUpdate = when (this) {
    NotificationSettingKey.RoutineDeadline -> NotificationSettingsUpdate(routineDeadlineEnabled = checked)
    NotificationSettingKey.NewVerification -> NotificationSettingsUpdate(newVerificationEnabled = checked)
    NotificationSettingKey.MyVerificationReaction -> NotificationSettingsUpdate(verificationReactionEnabled = checked)
    NotificationSettingKey.Nudge -> NotificationSettingsUpdate(pokeEnabled = checked)
    NotificationSettingKey.NewChat -> NotificationSettingsUpdate(newChatEnabled = checked)
    NotificationSettingKey.Like -> NotificationSettingsUpdate(likeEnabled = checked)
}

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
    // 고정 24시간이 아니라 캘린더 날짜를 하루씩 올려 DST에서도 맞게 계산한다.
    val cursor = start.clone() as Calendar
    var days = 0
    while (cursor.before(end)) {
        cursor.add(Calendar.DAY_OF_MONTH, 1)
        days++
    }
    return days
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
