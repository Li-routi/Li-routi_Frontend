package com.li_routi.core.data.mapper

import com.li_routi.core.data.network.dto.response.GoogleNonceResponse
import com.li_routi.core.data.network.dto.response.MyInfoResponse
import com.li_routi.core.data.network.dto.response.TokenResponse
import com.li_routi.core.domain.auth.AuthToken
import com.li_routi.core.domain.auth.MyInfo
import com.li_routi.core.domain.auth.SocialProvider

fun TokenResponse.toDomain(): AuthToken = AuthToken(
    accessToken = accessToken,
    refreshToken = refreshToken,
    accessTokenExpiresIn = accessTokenExpiresIn,
    onboardingCompleted = onboardingCompleted,
)

fun GoogleNonceResponse.toDomain(): String = nonce

fun MyInfoResponse.toDomain(): MyInfo = MyInfo(
    memberId = memberId,
    email = email,
    nickname = nickname,
    profileImageUrl = profileImageUrl,
    socialProvider = socialProvider.toSocialProvider(),
    onboardingCompleted = onboardingCompleted,
)

private fun String.toSocialProvider(): SocialProvider =
    runCatching { SocialProvider.valueOf(this) }.getOrDefault(SocialProvider.Unknown)
