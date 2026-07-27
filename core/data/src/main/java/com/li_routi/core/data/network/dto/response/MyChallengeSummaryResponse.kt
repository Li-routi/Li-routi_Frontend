package com.li_routi.core.data.network.dto.response

/** GET /api/members/me/challenges 응답의 챌린지 카드 한 건. */
data class MyChallengeSummaryResponse(
    val challengeId: Long,
    val name: String,
    val description: String,
    val imageUrl: String?,
    val category: String,
)
