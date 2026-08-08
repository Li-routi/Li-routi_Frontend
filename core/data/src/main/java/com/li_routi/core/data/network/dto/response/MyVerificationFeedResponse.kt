package com.li_routi.core.data.network.dto.response

/** GET /api/challenges/{challengeId}/verifications/me 응답 result. 커서 기반 무한 스크롤 페이지네이션. */
data class MyVerificationFeedResponse(
    val verifications: List<MyVerificationResponse>,
    val currentStreak: Int,
    val nextCursor: Long?,
    val hasNext: Boolean,
)
