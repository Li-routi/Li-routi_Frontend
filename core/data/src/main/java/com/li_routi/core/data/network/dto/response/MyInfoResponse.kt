package com.li_routi.core.data.network.dto.response

data class MyInfoResponse(
    val memberId: Long,
    val email: String,
    val nickname: String,
    val socialProvider: String,
    val onboardingCompleted: Boolean,
)
