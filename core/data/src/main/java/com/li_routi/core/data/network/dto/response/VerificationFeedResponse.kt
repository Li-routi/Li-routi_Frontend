package com.li_routi.core.data.network.dto.response

/** GET /api/challenges/{challengeId}/verifications 응답 result. 커서 기반 무한 스크롤 페이지네이션. */
data class VerificationFeedResponse(
    val verifications: List<VerificationResponse>,
    val nextCursor: Long?,
    val hasNext: Boolean,
)
