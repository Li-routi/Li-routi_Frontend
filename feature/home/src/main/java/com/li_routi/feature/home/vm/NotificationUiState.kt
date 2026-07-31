package com.li_routi.feature.home.vm

/**
 * 알림 목록/설정 화면 UI 상태.
 *
 * API 연동 전: 샘플 목록·로컬 토글만 사용한다.
 */
data class NotificationUiState(
    val selectedTabIndex: Int = 0,
    val notifications: List<NotificationItemUiModel> = SampleNotifications,
    val deleteTargetId: String? = null,
    val settingToggles: Map<NotificationSettingKey, Boolean> = NotificationSettingKey.entries
        .associateWith { true },
) {
    val tabs: List<String> = NotificationTabLabels

    val filteredNotifications: List<NotificationItemUiModel>
        get() {
            val tab = NotificationTab.entries.getOrNull(selectedTabIndex) ?: NotificationTab.All
            return when (tab) {
                NotificationTab.All -> notifications
                NotificationTab.MyRoutine -> notifications.filter { it.tab == NotificationTab.MyRoutine }
                NotificationTab.GroupRoutine -> notifications.filter { it.tab == NotificationTab.GroupRoutine }
                NotificationTab.Challenge -> notifications.filter { it.tab == NotificationTab.Challenge }
            }
        }

    val isEmpty: Boolean
        get() = filteredNotifications.isEmpty()
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
        title = "좋아요 알림",
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
        isUnread = true,
    ),
    NotificationItemUiModel(
        id = "n2",
        tab = NotificationTab.MyRoutine,
        categoryLabel = "내루틴 · 건강",
        title = "물 마시기",
        timeLabel = "오전 9:00",
        isUnread = true,
    ),
    NotificationItemUiModel(
        id = "n3",
        tab = NotificationTab.MyRoutine,
        categoryLabel = "내루틴 · 건강",
        title = "물 마시기",
        timeLabel = "오전 9:00",
        isUnread = false,
    ),
    NotificationItemUiModel(
        id = "n4",
        tab = NotificationTab.GroupRoutine,
        categoryLabel = "그룹 루틴 · 바디프로필",
        title = "스트레칭하기",
        timeLabel = "어제",
        isUnread = false,
    ),
    NotificationItemUiModel(
        id = "n5",
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
}
