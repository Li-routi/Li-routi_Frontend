package com.li_routi.core.data.network.dto.response

/** POST/DELETE /api/challenges/{challengeId}/verifications/{verificationId}/likes 응답 result. */
data class LikeResponse(
    val verificationId: Long,
    val likeCount: Long,
    val liked: Boolean,
)
