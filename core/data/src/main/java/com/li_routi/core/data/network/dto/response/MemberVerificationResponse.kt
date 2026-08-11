package com.li_routi.core.data.network.dto.response

/**
 * 챌린지/개인 루틴/그룹 루틴 인증 한 건. [reviewStatus]는 챌린지 인증에만 값이 있고(루틴은 심사가 없어
 * null), PENDING이면 [imageUrl]이 한시적 서명 주소라 오래 들고 있다가 쓰면 만료된다.
 */
data class MemberVerificationResponse(
    val verificationId: Long,
    val sourceType: String,
    val categoryName: String,
    val title: String,
    val content: String?,
    val imageUrl: String,
    val verifiedAt: String,
    val reviewStatus: String?,
)
