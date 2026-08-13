package com.li_routi.feature.login.vm

data class LoginUiState(
    val isLoading: Boolean = false,
    /** 프로필 설정 화면 진입 시 미리 채울 초기 닉네임(서버가 아는 소셜 프로필 기반 닉네임). */
    val nickname: String? = null,
)
