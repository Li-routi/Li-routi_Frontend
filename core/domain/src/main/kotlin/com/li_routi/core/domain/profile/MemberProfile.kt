package com.li_routi.core.domain.profile

data class MemberProfile(
    val memberId: Long,
    val email: String,
    val nickname: String,
    val profileImageUrl: String?,
    val socialProvider: String,
    val onboardingCompleted: Boolean,
)
