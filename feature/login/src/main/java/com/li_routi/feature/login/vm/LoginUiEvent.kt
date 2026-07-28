package com.li_routi.feature.login.vm

import com.li_routi.core.domain.auth.AuthToken

sealed interface LoginUiEvent {
    /**
     * 소셜 로그인 + 서버 검증까지 성공.
     *
     * [AuthToken.onboardingCompleted]는 이후 온보딩/홈 라우팅을 담당할 화면에서 사용한다 —
     * 실제 화면 전환은 이번 범위 밖(다른 담당자가 이 이벤트를 구독하는 쪽에서 연결한다).
     */
    data class LoginSucceeded(val token: AuthToken) : LoginUiEvent

    data class ShowError(val message: String) : LoginUiEvent
}
