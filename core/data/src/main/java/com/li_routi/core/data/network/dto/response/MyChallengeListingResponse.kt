package com.li_routi.core.data.network.dto.response

/** GET /api/members/me/challenges 응답 result. */
data class MyChallengeListingResponse(
    val challenges: List<MyChallengeSummaryResponse>,
)
