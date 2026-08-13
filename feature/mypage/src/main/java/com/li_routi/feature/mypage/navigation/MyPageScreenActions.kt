package com.li_routi.feature.mypage.navigation

/**
 * `MyPageScreen`에서 발생하는 사용자 이벤트에 대한 콜백 계약(contract).
 *
 * "프로필 수정"/"내 인증"/"업적"/"리포트"/"앱 정보"/"계정 관리"는
 * [com.li_routi.feature.mypage.vm.MyPageViewModel]이 [com.li_routi.feature.mypage.vm.MyPageUiEvent]를
 * 통해 실제 화면 전환까지 연결한다. 알림벨/설정의 목적지(알림 목록/알림 설정)는 `feature/home`에 있어
 * 이 모듈이 직접 전환할 수 없으므로, [com.li_routi.feature.mypage.navigation.MyPageRoute]가 app 모듈로부터
 * 받은 외부 콜백(`onNotificationClick`/`onSettingsClick`)에 위임한다.
 */
interface MyPageScreenActions {
    /** 상단 바 알림벨 아이콘 탭. [com.li_routi.feature.mypage.navigation.MyPageRoute]가 외부 콜백으로 위임. */
    fun onNotificationClick()

    /** 상단 바 설정 아이콘 탭. [com.li_routi.feature.mypage.navigation.MyPageRoute]가 외부 콜백으로 위임. */
    fun onSettingsClick()

    /** "프로필 수정" 버튼 탭. [com.li_routi.feature.mypage.screen.EditProfileScreen]으로 연결됨. */
    fun onEditProfileClick()

    /** "내 인증" 메뉴 탭. [com.li_routi.feature.mypage.screen.MyVerificationScreen]으로 연결됨. */
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
