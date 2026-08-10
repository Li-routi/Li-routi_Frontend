package com.li_routi.feature.home.vm

import com.li_routi.core.domain.routine.RoutineCategory
import com.li_routi.feature.home.component.RoutineChecklistItemUiModel
import com.li_routi.feature.home.component.SampleGroupRoomFilters
import com.li_routi.feature.home.component.SampleGroupRoomItems
import com.li_routi.feature.home.component.SampleMyRoutineItems
import com.li_routi.feature.home.component.SampleMyRoutineItemsOnly

/**
 * 홈 화면 UI 상태.
 *
 * [hasActiveRoutine] / [hasGroupRoom] 조합으로 Figma 툴팁·탭이 결정된다.
 * 체크리스트 영역은 [showChecklist] (= 개인 또는 그룹 루틴이 하나라도 있을 때)로 표시한다.
 */
data class HomeUiState(
    val nickname: String = "닉네임",
    val hasActiveRoutine: Boolean = false,
    val hasGroupRoom: Boolean = false,
    val myRoutineItems: List<RoutineChecklistItemUiModel> = emptyList(),
    val myRoutineFilters: List<String> = emptyList(),
    /** 개인 루틴 카테고리(롱프레스 편집/삭제용). GET /api/routines/categories */
    val myCategories: List<RoutineCategory> = emptyList(),
    /** POST 카테고리 가능 잔여 수. 홈 `+` 칩 활성 여부. */
    val addableCategoryCount: Int = 0,
    val groupRoomFilters: List<String> = emptyList(),
    val groupRoomItems: List<RoutineChecklistItemUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val loadError: Boolean = false,
) {
    /** 개인/그룹 중 하나라도 있으면 체크리스트(또는 그룹만)를 보여 준다. */
    val showChecklist: Boolean
        get() = hasActiveRoutine || hasGroupRoom

    val addCategoryEnabled: Boolean
        get() = addableCategoryCount > 0

    companion object {
        /** 처음 진입 (루틴 없음). */
        fun empty() = HomeUiState()

        /** 내 루틴 O / 그룹 루틴방 X. */
        fun routineOnly() = HomeUiState(
            hasActiveRoutine = true,
            hasGroupRoom = false,
            myRoutineItems = SampleMyRoutineItemsOnly,
        )

        /** 내 루틴 O / 그룹 루틴방 O. */
        fun routineAndGroupRoom() = HomeUiState(
            hasActiveRoutine = true,
            hasGroupRoom = true,
            myRoutineItems = SampleMyRoutineItems,
            groupRoomFilters = SampleGroupRoomFilters,
            groupRoomItems = SampleGroupRoomItems,
        )
    }
}

/**
 * 홈 화면에서 발생한 일회성 UI 이벤트.
 *
 * Navigation 연결은 이번 범위에서 제외되며, [HomeRoute]에서 collect 후
 * 다른 담당자가 NavController/Intent로 연결한다.
 */
sealed interface HomeUiEvent {
    data object NavigateToNotification : HomeUiEvent
    data object NavigateToShop : HomeUiEvent
    /** 홈 "내 루틴" 카드 탭 */
    data object NavigateToMyRoutine : HomeUiEvent
    data object NavigateToRoutineAuthCamera : HomeUiEvent
    data class NavigateToRoutineAuthCameraWithId(val routineId: String) : HomeUiEvent
    data object NavigateToManageMyRoutine : HomeUiEvent
    data object NavigateToCreateRoom : HomeUiEvent
    data object NavigateToJoinRoomWithInviteCode : HomeUiEvent
    /** 카테고리 생성 성공 — 시트는 이 이벤트 수신에만 닫는다. */
    data object CategoryCreated : HomeUiEvent
    /** 카테고리 생성 실패 — 시트 유지 + 메시지 표시. */
    data class CategoryCreateFailed(val message: String) : HomeUiEvent
    data object CategoryUpdated : HomeUiEvent
    data class CategoryUpdateFailed(val message: String) : HomeUiEvent
    data object CategoryDeleted : HomeUiEvent
    data class CategoryDeleteFailed(val message: String) : HomeUiEvent
}
