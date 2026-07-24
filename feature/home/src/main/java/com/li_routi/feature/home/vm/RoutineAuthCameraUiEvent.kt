package com.li_routi.feature.home.vm

import android.net.Uri

/**
 * 루틴 인증 카메라 화면의 일회성 UI 이벤트.
 *
 * Navigation 연결은 [com.li_routi.feature.home.navigation.RoutineAuthCameraRoute] /
 * [com.li_routi.feature.home.navigation.HomeRoute]의 onEvent에서 처리한다.
 */
sealed interface RoutineAuthCameraUiEvent {
    data object NavigateBack : RoutineAuthCameraUiEvent

    /** 셔터 촬영 성공 → "촬영 후 메모/선택" 업로드 화면으로 이동 */
    data class NavigateToRoutineAuthUpload(val photoUri: Uri) : RoutineAuthCameraUiEvent
}
