package com.li_routi.core.domain.auth

data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresIn: Long,
    val onboardingCompleted: Boolean,
)
