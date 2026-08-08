package com.li_routi.core.domain.auth

data class MyInfo(
    val memberId: Long,
    val email: String,
    val nickname: String,
    val profileImageUrl: String?,
    val socialProvider: SocialProvider,
    val onboardingCompleted: Boolean,
)
