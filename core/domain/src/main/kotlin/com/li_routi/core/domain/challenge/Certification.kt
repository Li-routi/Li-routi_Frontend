package com.li_routi.core.domain.challenge

/** 인증 게시글 한 건 (GET /api/challenges/{id}/verifications). */
data class Certification(
    val id: Long,
    val authorName: String,
    val content: String,
    val imageUrl: String,
    val verifiedAt: String,
    val likeCount: Long,
    val liked: Boolean,
    /** 서버가 토큰의 회원과 작성자를 대조해 내려주는, 본인 게시글 여부. */
    val isMine: Boolean,
)

/** 인증 피드 커서 기반 페이지네이션 결과. */
data class CertificationPage(
    val certifications: List<Certification>,
    val nextCursor: Long?,
    val hasNext: Boolean,
)

/** 내 인증 게시글 한 건 (GET /api/challenges/{id}/verifications/me). */
data class MyCertification(
    val id: Long,
    val content: String,
    val imageUrl: String,
    val verifiedDate: String,
    val verifiedAt: String,
    val likeCount: Long,
)

/** 내 인증 피드 커서 기반 페이지네이션 결과. */
data class MyCertificationPage(
    val certifications: List<MyCertification>,
    val currentStreak: Int,
    val nextCursor: Long?,
    val hasNext: Boolean,
)

/** 좋아요/좋아요 취소 결과 (POST, DELETE .../likes). */
data class LikeResult(
    val verificationId: Long,
    val likeCount: Long,
    val liked: Boolean,
)

/** 인증 게시글 작성 결과 (POST .../verifications). */
data class CreatedVerification(
    val verificationId: Long,
    val imageUrl: String?,
    val content: String,
    val currentStreak: Int,
    val reverified: Boolean,
)
