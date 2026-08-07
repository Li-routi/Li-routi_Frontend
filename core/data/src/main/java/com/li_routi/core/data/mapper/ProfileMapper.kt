package com.li_routi.core.data.mapper

import com.li_routi.core.data.network.dto.response.MemberInfoResponse
import com.li_routi.core.domain.profile.MemberProfile

fun MemberInfoResponse.toDomain(): MemberProfile = MemberProfile(
    memberId = memberId,
    email = email,
    nickname = nickname,
    profileImageUrl = profileImageUrl,
    socialProvider = socialProvider,
    onboardingCompleted = onboardingCompleted,
)
