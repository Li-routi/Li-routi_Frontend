package com.li_routi.feature.home.vm

import com.li_routi.feature.home.component.RoutineChecklistItemUiModel
import com.li_routi.feature.home.component.SampleGroupRoomFilters
import com.li_routi.feature.home.component.SampleGroupRoomItems
import com.li_routi.feature.home.component.SampleMyRoutineItems
import com.li_routi.feature.home.component.SampleMyRoutineItemsOnly

/**
 * 홈 화면 UI 상태.
 *
 * [hasActiveRoutine] / [hasGroupRoom] 조합으로 Figma 3가지 상태가 결정된다.
 * 실제 API 연동 전까지는 샘플 데이터로 채운다.
 */
data class HomeUiState(
    val nickname: String = "닉네임",
    val hasActiveRoutine: Boolean = false,
    val hasGroupRoom: Boolean = false,
    val myRoutineItems: List<RoutineChecklistItemUiModel> = emptyList(),
    val groupRoomFilters: List<String> = emptyList(),
    val groupRoomItems: List<RoutineChecklistItemUiModel> = emptyList(),
) {
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
}
