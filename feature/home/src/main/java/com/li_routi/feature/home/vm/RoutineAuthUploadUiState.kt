package com.li_routi.feature.home.vm

import android.net.Uri
import com.li_routi.core.common.ui.routine.CategoryColor
import com.li_routi.core.domain.challenge.MyChallenge
import com.li_routi.feature.home.component.RoutineChecklistItemUiModel
import com.li_routi.feature.home.component.RoutineChecklistKind
import com.li_routi.feature.home.component.SampleGroupRoomItems
import com.li_routi.feature.home.component.SampleMyRoutineItems
import com.li_routi.feature.home.component.SampleMyRoutineItemsOnly

/**
 * 촬영 후 메모/루틴 선택(업로드) 화면 UI 상태.
 *
 * Figma `촬영 후 메모/선택` (Design Page 1.1, node `3610:26875`).
 */
data class RoutineAuthUploadUiState(
    val photoUri: Uri? = null,
    val memo: String = "",
    val routines: List<RoutineAuthSelectableUiModel> = SampleRoutineAuthSelectables,
    val selectedRoutineIds: Set<String> = emptySet(),
    val isUploading: Boolean = false,
    /** 업로드 성공 후 「완료」 버튼·성공 토스트 상태. */
    val isUploadCompleted: Boolean = false,
    /** null이 아니면 하단 버튼 위 토스트 표시 (실패/성공). */
    val toastMessage: String? = null,
    val showExitConfirmDialog: Boolean = false,
) {
    val isUploadEnabled: Boolean
        get() = when {
            isUploadCompleted -> true
            else -> photoUri != null && selectedRoutineIds.isNotEmpty() && !isUploading
        }

    val showUploadFailedToast: Boolean
        get() = toastMessage != null && !isUploadCompleted
}

/**
 * 인증할 루틴 선택 리스트 한 항목.
 *
 * Figma List(3610:26875): 제목 앞 category-dot, 메타 `카테고리 | 마감`, Badge는 방 이름/챌린지.
 *
 * @param subtitle 그룹 방 이름. Badge에 표시. null이면 Secondary Badge 숨김.
 * @param categoryLabel 메타 첫 칸(카테고리) 또는 챌린지 Badge 문구.
 * @param badgeTone Challenge면 Orange Badge, Secondary면 방 이름 Blue Badge.
 * @param categoryColor 제목 앞 category-dot. null이면 미표시(챌린지 등).
 * @param memberRoutineId 개인 루틴 서버 id. 있으면 문자열 id 파싱보다 우선한다.
 * @param memberStartTime 개인 루틴의 nullable 인증 시작 시각.
 * @param memberEndTime 개인 루틴의 인증 종료 시각. 이전 응답에서는 null일 수 있다.
 * @param memberCompletedToday 개인 루틴의 오늘 인증 완료 여부.
 * @param groupId 그룹방 id (그룹 루틴만).
 * @param groupRoutineId 그룹 루틴 서버 id (그룹 루틴만).
 * @param challengeId 챌린지 서버 id. 있으면 문자열 id 파싱보다 우선한다.
 */
data class RoutineAuthSelectableUiModel(
    val id: String,
    val title: String,
    val dueLabel: String? = null,
    val subtitle: String? = null,
    val categoryLabel: String,
    val badgeTone: RoutineAuthBadgeTone = RoutineAuthBadgeTone.Secondary,
    val categoryColor: CategoryColor? = null,
    val memberRoutineId: Long? = null,
    val memberStartTime: String? = null,
    val memberEndTime: String? = null,
    val memberCompletedToday: Boolean = false,
    val groupId: Long? = null,
    val groupRoutineId: Long? = null,
    val groupRoutineVerificationId: Long? = null,
    val challengeId: Long? = null,
)

enum class RoutineAuthBadgeTone {
    Secondary,
    Challenge,
}

private fun RoutineChecklistItemUiModel.toAuthSelectable(
    badgeTone: RoutineAuthBadgeTone = RoutineAuthBadgeTone.Secondary,
) = RoutineAuthSelectableUiModel(
    id = id,
    title = title,
    dueLabel = dueLabel,
    subtitle = roomLabel,
    categoryLabel = categoryLabel,
    badgeTone = badgeTone,
    categoryColor = categoryColor,
    memberRoutineId = if (kind == RoutineChecklistKind.Member) routineId else null,
    memberStartTime = if (kind == RoutineChecklistKind.Member) memberStartTime else null,
    memberEndTime = if (kind == RoutineChecklistKind.Member) memberEndTime else null,
    memberCompletedToday = kind == RoutineChecklistKind.Member && completedToday,
    groupId = groupId,
    groupRoutineId = if (kind == RoutineChecklistKind.Group) routineId else null,
)

/**
 * 홈 체크리스트 미완료 샘플과 **같은 id**를 쓴다.
 * 카메라 아이콘 → 업로드 미리 선택이 체크박스에 반영되도록 한다.
 *
 * 마지막 챌린지 행은 홈에 대응 id가 없어 미리 선택 대상이 아니다.
 */
val SampleRoutineAuthSelectables: List<RoutineAuthSelectableUiModel> =
    (SampleMyRoutineItemsOnly + SampleMyRoutineItems + SampleGroupRoomItems)
        .filter { !it.isDone }
        .distinctBy { it.id }
        .map { it.toAuthSelectable() } +
        RoutineAuthSelectableUiModel(
            id = "challenge_0",
            title = "물 1L 마시기 챌린지",
            categoryLabel = "챌린지",
            badgeTone = RoutineAuthBadgeTone.Challenge,
            challengeId = 0L,
        )

/**
 * 참여 중인 챌린지 목록 → "인증할 루틴 선택" 항목.
 * id 규칙: `challenge_{challengeId}` (인증 업로드 선택 키와 공유, [ChallengeIdPrefix] 참고).
 * 챌린지는 개인/그룹 루틴과 달리 마감 시각 개념이 없어 [RoutineAuthSelectableUiModel.dueLabel]은 null.
 */
fun List<MyChallenge>.toAuthSelectables(): List<RoutineAuthSelectableUiModel> = map { challenge ->
    RoutineAuthSelectableUiModel(
        id = "$ChallengeIdPrefix${challenge.id}",
        title = challenge.name,
        categoryLabel = "챌린지",
        badgeTone = RoutineAuthBadgeTone.Challenge,
        challengeId = challenge.id,
    )
}

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

    /** 개인 루틴 인증 시간이 아니어서 서버 또는 업로드 직전 검사에서 거절됨. */
    data object RoutineTimeRangeRejected : RoutineAuthUploadUiEvent

    /** 업로드 성공 → 홈 화면으로 */
    data class NavigateToHome(
        val selectedRoutineIds: Set<String>,
    ) : RoutineAuthUploadUiEvent
}
