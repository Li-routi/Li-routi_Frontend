package com.li_routi.core.data.network.dto.response

/** GET /api/challenges 응답 result. 커서 기반 무한 스크롤 페이지네이션. */
data class ChallengeListingResponse(
    val challenges: List<ChallengeSummaryResponse>,
    val nextCursor: Long?,
    val hasNext: Boolean,
)
