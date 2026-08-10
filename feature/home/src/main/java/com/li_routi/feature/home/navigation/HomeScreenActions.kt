package com.li_routi.feature.home.navigation

import com.li_routi.core.common.ui.routine.CategoryColor

/**
 * `HomeScreen`에서 발생하는 사용자 이벤트에 대한 콜백 계약(contract).
 *
 * Navigation(NavController/NavHost, Activity Intent 등) 연결은 이번 범위에서 제외되며,
 * 실제 화면 전환 구현은 다른 담당자가 이 인터페이스를 구현해 연결한다.
 *
 * 빠른 인증 진입은 화면 전체를 왼쪽에서 오른쪽으로 스와이프하는 제스처로도 가능하다
 * ([onSwipeToVerification]) — `app`이 소유한 공유 인증 플로우(개인/그룹/챌린지 공용 카메라)를 연다.
 */
interface HomeScreenActions {
    /** 상단 바 알림벨 아이콘 탭 */
    fun onNotificationClick()

    /** "닉네임" 카드의 상점가기 버튼 탭 */
    fun onNavigateToShop()

    /** "내 루틴" 카드 탭 (chevron 포함 카드 전체) */
    fun onMyRoutineClick()

    /** 오늘의 루틴/그룹 루틴 리스트 항목의 카메라 아이콘 탭 (해당 루틴 인증하기로 이동) */
    fun onRoutineCameraClick(routineId: String)

    /** 홈 화면을 왼쪽→오른쪽으로 스와이프 — 특정 루틴 미리 선택 없이 인증 카메라를 연다. */
    fun onSwipeToVerification()

    /** 상단 바 `+` 탭으로 열리는 바텀시트의 "내 루틴 관리" 메뉴 탭 */
    fun onManageMyRoutineClick()

    /** 상단 바 `+` 탭으로 열리는 바텀시트의 "방 만들기" 메뉴 탭 */
    fun onCreateRoomClick()

    /** 상단 바 `+` 탭으로 열리는 바텀시트의 "초대코드로 참여" 메뉴 탭 */
    fun onJoinRoomWithInviteCodeClick()

    /** 홈 요약 로드 실패 후 재시도 */
    fun onRetryLoadClick()

    /**
     * 카테고리 필터 `+` → 카테고리 추가 시트 확인.
     * 성공 시 개인 루틴 필터 chip에 새 카테고리명을 붙인다.
     */
    fun onCreateCategory(name: String, color: CategoryColor?)

    /** 카테고리 칩 롱프레스 편집 후 확인. */
    fun onUpdateCategory(categoryId: Long, name: String, color: CategoryColor?)

    /** 카테고리 시트에서 삭제. */
    fun onDeleteCategory(categoryId: Long)
}
