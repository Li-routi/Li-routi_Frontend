package com.li_routi.feature.home.navigation

/**
 * `RoutineAuthUploadScreen`("촬영 후 메모/선택")에서 발생하는 사용자 이벤트 콜백 계약.
 */
interface RoutineAuthUploadScreenActions {
    /** 상단 뒤로가기 — 저장 없이 화면 이탈 */
    fun onBackClick()

    /** 상단 X(닫기) — 저장 없이 플로우 종료 */
    fun onCloseClick()

    /** 메모 입력 변경 (선택, 최대 [MEMO_MAX_LENGTH]자) */
    fun onMemoChange(memo: String)

    /** 루틴 선택/해제 토글 */
    fun onRoutineToggle(routineId: String)

    /** 업로드 버튼 탭 (루틴 1개 이상 선택 시에만 유효) */
    fun onUploadClick()

    /** 업로드 실패 토스트 닫기 */
    fun onDismissUploadFailedToast()

    companion object {
        const val MEMO_MAX_LENGTH = 30
    }
}
