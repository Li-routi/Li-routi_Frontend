package com.li_routi.feature.home.navigation

/**
 * `HomeScreen`에서 발생하는 사용자 이벤트에 대한 콜백 계약(contract).
 *
 * Navigation(NavController/NavHost, Activity Intent 등) 연결은 이번 범위에서 제외되며,
 * 실제 화면 전환 구현은 다른 담당자가 이 인터페이스를 구현해 연결한다.
 *
 * 빠른 인증 진입은 카드 스와이프가 아니라 [HomeRoute]의 HorizontalPager(화면 스와이프)로 처리한다.
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

    /** 상단 바 `+` 탭으로 열리는 바텀시트의 "내 루틴 관리" 메뉴 탭 */
    fun onManageMyRoutineClick()

    /** 상단 바 `+` 탭으로 열리는 바텀시트의 "방 만들기" 메뉴 탭 */
    fun onCreateRoomClick()

    /** 상단 바 `+` 탭으로 열리는 바텀시트의 "초대코드로 참여" 메뉴 탭 */
    fun onJoinRoomWithInviteCodeClick()
}
