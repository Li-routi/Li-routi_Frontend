package com.li_routi.core.domain.challenge

/** 인증 게시글 한 건 (GET /api/challenges/{id}/verifications). */
data class Certification(
    val id: Long,
    val authorName: String,
    val content: String,
    val imageUrl: String,
    val verifiedAt: String,
)

/** 인증 피드 커서 기반 페이지네이션 결과. */
data class CertificationPage(
    val certifications: List<Certification>,
    val nextCursor: Long?,
    val hasNext: Boolean,
)
