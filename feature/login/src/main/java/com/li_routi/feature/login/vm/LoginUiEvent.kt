package com.li_routi.feature.login.vm

import com.li_routi.core.domain.auth.AuthToken

sealed interface LoginUiEvent {
    /**
     * 소셜 로그인 + 서버 검증까지 성공.
     *
     * [AuthToken.onboardingCompleted]를 보고 [com.li_routi.feature.login.navigation.LoginRoute]가
     * 프로필 설정 화면 표시 여부를 분기한다.
     */
    data class LoginSucceeded(val token: AuthToken) : LoginUiEvent

    /** 프로필 저장(PATCH /api/members/me/profile) 성공 — 홈 화면으로 이동한다. */
    object ProfileSaveSucceeded : LoginUiEvent

    data class ShowError(val message: String) : LoginUiEvent
}
