package com.li_routi.core.data.network.dto.response

/** GET /api/challenges/{challengeId}/verifications/me 응답의 내 인증 게시글 한 건. */
data class MyVerificationResponse(
    val verificationId: Long,
    val imageUrl: String?,
    val content: String,
    val verifiedDate: String,
    val verifiedAt: String,
    val likeCount: Long,
)
