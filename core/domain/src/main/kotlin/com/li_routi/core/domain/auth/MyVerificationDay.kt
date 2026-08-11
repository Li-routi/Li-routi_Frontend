package com.li_routi.core.domain.auth

/** GET /api/members/me/verifications 조회 결과 — 챌린지/개인 루틴/그룹 루틴 인증을 날짜 하나로 통합한 목록. */
data class MyVerificationDay(
    val date: String,
    val verifications: List<MyVerificationEntry>,
)

data class MyVerificationEntry(
    val verificationId: Long,
    val sourceType: VerificationSourceType,
    val categoryName: String,
    val title: String,
    val content: String?,
    val imageUrl: String,
    val verifiedAt: String,
    val reviewStatus: VerificationReviewStatus?,
)

/** [UNKNOWN]은 서버가 새 값을 추가했을 때를 대비한 폴백이다 — 화면은 sourceType을 직접 분기하지 않는다. */
enum class VerificationSourceType { CHALLENGE, MEMBER_ROUTINE, GROUP_ROUTINE, UNKNOWN }

/**
 * 챌린지 인증에만 값이 있다(루틴은 심사가 없어 null). PENDING이면 아직 공개되지 않은 인증 — 심사기가
 * 응답하지 못해 보류 중이며, 이때 [MyVerificationEntry.imageUrl]은 공개 주소가 아니라 한시적 서명
 * 주소라 오래 들고 있다가 쓰면 만료된다.
 */
enum class VerificationReviewStatus { PENDING, APPROVED }
