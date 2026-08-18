package com.li_routi.feature.home.vm

/**
 * 알림 목록/설정 화면 UI 상태.
 */
data class NotificationUiState(
    val selectedTabIndex: Int = 0,
    val notifications: List<NotificationItemUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasNext: Boolean = false,
    val nextCursor: Long? = null,
    val errorMessage: String? = null,
    val settingToggles: Map<NotificationSettingKey, Boolean> = NotificationSettingKey.entries
        .associateWith { true },
) {
    val tabs: List<String> = NotificationTabLabels

    /** 탭별 필터는 서버 category로 처리하므로 목록을 그대로 노출한다. */
    val filteredNotifications: List<NotificationItemUiModel>
        get() = notifications

    val isEmpty: Boolean
        get() = filteredNotifications.isEmpty() && !isLoading
}

enum class NotificationTab {
    All,
    MyRoutine,
    GroupRoutine,
    Challenge,
}

val NotificationTabLabels: List<String> = listOf("전체", "내루틴", "그룹 루틴", "챌린지")

data class NotificationItemUiModel(
    val id: String,
    val tab: NotificationTab,
    val categoryLabel: String,
    val title: String,
    val timeLabel: String,
    val isUnread: Boolean = false,
    /** 딥링크 판단용 원본 이벤트 타입([resolveNotificationNavigationTarget]에 그대로 넘긴다). */
    val type: String = "",
    val referenceId: Long? = null,
)

enum class NotificationSettingKey {
    RoutineDeadline,
    NewVerification,
    MyVerificationReaction,
    Nudge,
    NewChat,
    Like,
}

data class NotificationSettingUiModel(
    val key: NotificationSettingKey,
    val title: String,
    val description: String,
)

val NotificationSettingItems: List<NotificationSettingUiModel> = listOf(
    NotificationSettingUiModel(
        key = NotificationSettingKey.RoutineDeadline,
        title = "루틴 마감 알림",
        description = "마감 알림과 다시 알림을 받아요",
    ),
    NotificationSettingUiModel(
        key = NotificationSettingKey.NewVerification,
        title = "새 인증 알림",
        description = "친구가 인증을 올리면 알려드려요",
    ),
    NotificationSettingUiModel(
        key = NotificationSettingKey.MyVerificationReaction,
        title = "내 인증 반응 알림",
        description = "내 인증에 좋아요·아쉬워요가 오면 알려드려요",
    ),
    NotificationSettingUiModel(
        key = NotificationSettingKey.Nudge,
        title = "쿡쿡 알림",
        description = "친구가 쿡쿡 찌르면 알려드려요",
    ),
    NotificationSettingUiModel(
        key = NotificationSettingKey.NewChat,
        title = "새 채팅 알림",
        description = "새 채팅이 오면 알려드려요",
    ),
    NotificationSettingUiModel(
        key = NotificationSettingKey.Like,
        title = "챌린지 좋아요 알림",
        description = "좋아요를 받으면 알려드려요",
    ),
)

val SampleNotifications: List<NotificationItemUiModel> = listOf(
    NotificationItemUiModel(
        id = "n0",
        tab = NotificationTab.MyRoutine,
        categoryLabel = "내루틴 · 건강",
        title = "물 마시기",
        timeLabel = "오전 9:00",
        isUnread = true,
    ),
    NotificationItemUiModel(
        id = "n1",
        tab = NotificationTab.MyRoutine,
        categoryLabel = "내루틴 · 건강",
        title = "물 마시기",
        timeLabel = "오전 9:00",
        isUnread = false,
    ),
    NotificationItemUiModel(
        id = "n2",
        tab = NotificationTab.MyRoutine,
        categoryLabel = "내루틴 · 건강",
        title = "물 마시기",
        timeLabel = "오전 9:00",
        isUnread = false,
    ),
    NotificationItemUiModel(
        id = "n3",
        tab = NotificationTab.GroupRoutine,
        categoryLabel = "그룹 루틴 · 바디프로필",
        title = "스트레칭하기",
        timeLabel = "어제",
        isUnread = false,
    ),
    NotificationItemUiModel(
        id = "n4",
        tab = NotificationTab.Challenge,
        categoryLabel = "챌린지",
        title = "물 1L 마시기 챌린지",
        timeLabel = "2일 전",
        isUnread = false,
    ),
)

sealed interface NotificationUiEvent {
    data object NavigateBack : NotificationUiEvent
    data object NavigateToSettings : NotificationUiEvent
    data object NavigateToPersonalRoutine : NotificationUiEvent
    data object NavigateToGroupRoutine : NotificationUiEvent
    data object NavigateToChallengeHome : NotificationUiEvent
    data class NavigateToChallengeDetail(val challengeId: Long) : NotificationUiEvent
}
