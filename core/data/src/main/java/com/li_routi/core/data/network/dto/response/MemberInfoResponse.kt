package com.li_routi.core.data.network.dto.response

data class MemberInfoResponse(
    val memberId: Long,
    val email: String,
    val nickname: String,
    val profileImageUrl: String?,
    val socialProvider: String,
    val onboardingCompleted: Boolean,
)
