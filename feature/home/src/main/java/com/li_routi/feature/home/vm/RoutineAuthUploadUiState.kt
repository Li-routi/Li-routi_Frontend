package com.li_routi.feature.home.vm

import android.net.Uri

/**
 * 촬영 후 메모/루틴 선택(업로드) 화면 UI 상태.
 *
 * Figma `촬영 후 메모/선택` (예: node `2176:20314`).
 */
data class RoutineAuthUploadUiState(
    val photoUri: Uri? = null,
    val memo: String = "",
    val routines: List<RoutineAuthSelectableUiModel> = SampleRoutineAuthSelectables,
    val selectedRoutineIds: Set<String> = emptySet(),
    val isUploading: Boolean = false,
    val showUploadFailedToast: Boolean = false,
) {
    val isUploadEnabled: Boolean
        get() = selectedRoutineIds.isNotEmpty() && !isUploading
}

/**
 * 인증할 루틴 선택 리스트 한 항목.
 *
 * @param subtitle 마감 옆 보조 텍스트. null이면 마감만 표시.
 * @param badgeTone 카테고리 Badge 색 톤. DS Badge 완성 전까지 화면에서 placeholder/톤만 구분한다.
 */
data class RoutineAuthSelectableUiModel(
    val id: String,
    val title: String,
    val dueLabel: String? = null,
    val subtitle: String? = null,
    val categoryLabel: String,
    val badgeTone: RoutineAuthBadgeTone = RoutineAuthBadgeTone.Secondary,
)

enum class RoutineAuthBadgeTone {
    Secondary,
    Challenge,
}

val SampleRoutineAuthSelectables: List<RoutineAuthSelectableUiModel> = listOf(
    RoutineAuthSelectableUiModel(
        id = "auth_0",
        title = "물 마시기",
        dueLabel = "마감 22:00",
        subtitle = "Sub tit",
        categoryLabel = "갓생살자",
        badgeTone = RoutineAuthBadgeTone.Secondary,
    ),
    RoutineAuthSelectableUiModel(
        id = "auth_1",
        title = "스쿼트하기",
        dueLabel = "마감 22:00",
        subtitle = "Sub tit",
        categoryLabel = "운동부",
        badgeTone = RoutineAuthBadgeTone.Secondary,
    ),
    RoutineAuthSelectableUiModel(
        id = "auth_2",
        title = "물 1L 마시기 챌린지",
        categoryLabel = "챌린지",
        badgeTone = RoutineAuthBadgeTone.Challenge,
    ),
)

/**
 * 촬영 후 업로드 화면의 일회성 UI 이벤트.
 *
 * Navigation 연결은 [com.li_routi.feature.home.navigation.RoutineAuthUploadRoute]의
 * onEvent에서 다른 담당자가 구현한다.
 */
sealed interface RoutineAuthUploadUiEvent {
    /** 상단 뒤로가기 → 카메라 화면으로 */
    data object NavigateBack : RoutineAuthUploadUiEvent

    /** 상단 X → 인증 플로우 종료(홈 등) */
    data object NavigateClose : RoutineAuthUploadUiEvent

    /** 업로드 성공 → 홈 화면으로 */
    data object NavigateToHome : RoutineAuthUploadUiEvent
}
