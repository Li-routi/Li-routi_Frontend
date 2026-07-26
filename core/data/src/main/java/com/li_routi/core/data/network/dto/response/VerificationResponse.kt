package com.li_routi.core.data.network.dto.response

/** GET /api/challenges/{challengeId}/verifications 응답의 인증 게시글 한 건. */
data class VerificationResponse(
    val verificationId: Long,
    val nickname: String,
    val imageUrl: String,
    val content: String,
    val verifiedAt: String,
)
