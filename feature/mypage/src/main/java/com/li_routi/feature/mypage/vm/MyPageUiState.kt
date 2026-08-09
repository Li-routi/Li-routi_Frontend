package com.li_routi.feature.mypage.vm

/**
 * 마이페이지 화면 상태. [nickname]/[email]은 `GET /api/members/me` 조회 결과로 채워진다.
 *
 * [isProfileLoaded]가 true가 되기 전에는 프로필 수정 화면 진입을 막는다 — 조회 응답이 오기 전에
 * 진입하면 [nickname]이 빈 값으로 초기화됐다가 응답 도착 시 바뀌면서 입력 중이던 값이 날아가기 때문.
 */
data class MyPageUiState(
    val nickname: String = "",
    val email: String = "",
    val profileImageUrl: String? = null,
    val isProfileLoaded: Boolean = false,
    val isSavingProfile: Boolean = false,
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

    /** 프로필(닉네임) 저장 성공 → 마이페이지로 복귀 */
    data object ProfileSaved : MyPageUiEvent

    /** 프로필 저장 실패 → 에러 메시지 노출 */
    data class ShowError(val message: String) : MyPageUiEvent
}
