package com.li_routi.core.data.network.dto.response

/** GET /api/challenges/{challengeId}/verifications 응답의 인증 게시글 한 건. */
data class VerificationResponse(
    val verificationId: Long,
    val nickname: String,
    val imageUrl: String?,
    val content: String,
    val verifiedAt: String,
    val likeCount: Long,
    val liked: Boolean,
    /** 토큰의 회원과 작성자를 서버가 대조해 내려주는 값. 닉네임은 유니크하지 않아 대신 이 값으로만 판별한다. */
    val mine: Boolean,
)
