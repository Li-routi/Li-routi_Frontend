package com.li_routi.core.data.network.dto.response

/** GET /api/challenges/{challengeId} 응답 result. */
data class ChallengeDetailResponse(
    val challengeId: Long,
    val name: String,
    val description: String,
    val imageUrl: String?,
    val category: String,
    val routineCycle: String,
    val reward: Int,
    val participating: Boolean,
    val participantCount: Long,
    val verificationPostCount: Long,
    val todayCompletionCount: Long,
    /** 조회자(로그인 사용자)가 현재 인증 주기(오늘/이번 주/이번 달 등, routineCycle 기준)에 이미 인증했는지 여부. */
    val verifiedInCurrentPeriod: Boolean = false,
)
