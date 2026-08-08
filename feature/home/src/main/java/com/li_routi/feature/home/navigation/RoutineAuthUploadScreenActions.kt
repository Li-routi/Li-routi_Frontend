package com.li_routi.feature.home.navigation

/**
 * `RoutineAuthUploadScreen`("촬영 후 메모/선택")에서 발생하는 사용자 이벤트 콜백 계약.
 */
interface RoutineAuthUploadScreenActions {
    /** 상단 뒤로가기 — 이탈 확인 다이얼로그 후 카메라로 */
    fun onBackClick()

    /** 상단 X(닫기) — 이탈 확인 다이얼로그 후 플로우 종료 */
    fun onCloseClick()

    /** 메모 입력 변경 (선택, 최대 [MEMO_MAX_LENGTH]자) */
    fun onMemoChange(memo: String)

    /** 루틴 선택/해제 토글 */
    fun onRoutineToggle(routineId: String)

    /** 업로드 / 완료 버튼 탭 */
    fun onUploadClick()

    /** 하단 토스트(실패/성공) 닫기 */
    fun onDismissUploadFailedToast()

    companion object {
        /** Figma `촬영 후 메모/선택`(3610:26875) Texfield placeholder 기준. */
        const val MEMO_MAX_LENGTH = 30
    }
}
