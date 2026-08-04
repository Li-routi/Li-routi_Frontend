package com.li_routi.feature.mypage.vm

/**
 * 마이페이지 화면 상태.
 *
 * 아직 프로필 조회 API가 없어 [nickname]/[email]은 샘플 값으로 초기화된다 — 실제 사용자 프로필 연동은
 * 이번 범위 밖이며, 연동 시 이 상태를 채우는 use case만 [MyPageViewModel]에 추가하면 된다.
 */
data class MyPageUiState(
    val nickname: String = "잠자는개구리",
    val email: String = "example@gamil.com",
)

/** 마이페이지에서 발생하는 일회성 내비게이션 이벤트. */
sealed interface MyPageUiEvent {
    /** "프로필 수정" 탭 → 닉네임 변경 화면 진입 */
    data object NavigateToEditProfile : MyPageUiEvent

    /** "내 인증" 탭 → 내 인증 화면 진입 */
    data object NavigateToMyVerification : MyPageUiEvent

    /** "업적" 탭 → 업적 화면 진입 */
    data object NavigateToAchievement : MyPageUiEvent

    /** "리포트" 탭 → 리포트 화면 진입 */
    data object NavigateToReport : MyPageUiEvent

    /** "앱 정보" 탭 → 앱 정보 화면 진입 */
    data object NavigateToAppInfo : MyPageUiEvent

    /** "계정 관리" 탭 → 계정 관리 화면 진입 */
    data object NavigateToAccountManage : MyPageUiEvent
}
