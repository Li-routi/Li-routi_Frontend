package com.li_routi.core.data.mapper

import com.li_routi.core.data.network.dto.response.GoogleNonceResponse
import com.li_routi.core.data.network.dto.response.TokenResponse
import com.li_routi.core.domain.auth.AuthToken

fun TokenResponse.toDomain(): AuthToken = AuthToken(
    accessToken = accessToken,
    refreshToken = refreshToken,
    accessTokenExpiresIn = accessTokenExpiresIn,
    onboardingCompleted = onboardingCompleted,
)

fun GoogleNonceResponse.toDomain(): String = nonce
