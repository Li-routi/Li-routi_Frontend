package com.li_routi.feature.mypage.navigation

/**
 * `MyPageScreen`에서 발생하는 사용자 이벤트에 대한 콜백 계약(contract).
 *
 * "프로필 수정"/"업적"/"리포트"/"앱 정보"/"계정 관리"는 [com.li_routi.feature.mypage.vm.MyPageViewModel]이
 * [com.li_routi.feature.mypage.vm.MyPageUiEvent]를 통해 실제 화면 전환까지 연결한다.
 * 나머지(알림벨/설정/내 인증)는 목적지 화면이 아직 없어 미구현 상태다 — 아래 각 항목에 표시.
 */
interface MyPageScreenActions {
    /** 상단 바 알림벨 아이콘 탭. 미구현 — 알림 화면이 아직 없다. */
    fun onNotificationClick()

    /** 상단 바 설정 아이콘 탭. 미구현 — 목적지 화면이 아직 없다. */
    fun onSettingsClick()

    /** "프로필 수정" 버튼 탭. [com.li_routi.feature.mypage.screen.EditProfileScreen]으로 연결됨. */
    fun onEditProfileClick()

    /** "내 인증" 메뉴 탭. 미구현 — 목적지 화면이 아직 없다. */
    fun onMyVerificationClick()

    /** "업적" 메뉴 탭. [com.li_routi.feature.mypage.screen.AchievementScreen]으로 연결됨. */
    fun onAchievementClick()

    /** "리포트" 메뉴 탭. [com.li_routi.feature.mypage.screen.ReportScreen]으로 연결됨. */
    fun onReportClick()

    /** "계정 관리" 메뉴 탭. [com.li_routi.feature.mypage.screen.AccountManageScreen]으로 연결됨. */
    fun onAccountManageClick()

    /** "앱 정보" 메뉴 탭. [com.li_routi.feature.mypage.screen.AppInfoScreen]으로 연결됨. */
    fun onAppInfoClick()
}
