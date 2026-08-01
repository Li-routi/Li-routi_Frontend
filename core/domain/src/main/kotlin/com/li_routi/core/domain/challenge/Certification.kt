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
