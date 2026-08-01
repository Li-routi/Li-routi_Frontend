package com.li_routi.core.data.network.dto.response

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresIn: Long,
    val onboardingCompleted: Boolean,
)