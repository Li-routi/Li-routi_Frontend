package com.li_routi.feature.home.navigation

import android.net.Uri

/**
 * `RoutineAuthCameraScreen`("루틴 인증하기")에서 발생하는 사용자 이벤트 콜백 계약.
 */
interface RoutineAuthCameraScreenActions {
    /** 상단 뒤로가기 아이콘 탭 */
    fun onBackClick()

    /** 촬영 성공 후 업로드 화면으로 이동 */
    fun onCaptureSuccess(photoUri: Uri)
}
