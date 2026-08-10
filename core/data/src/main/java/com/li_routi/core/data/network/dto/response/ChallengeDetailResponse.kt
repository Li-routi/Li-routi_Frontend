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
    /**
     * 조회자(로그인 사용자)가 현재 인증 주기(오늘/이번 주/이번 달 등, routineCycle 기준)에 이미 인증했는지 여부.
     * Gson은 Kotlin data class를 리플렉션으로 생성해 기본값을 적용하지 않으므로, 원시 Boolean이었다면 필드
     * 누락 시 조용히 false가 되어 이미 인증한 사용자에게 "인증하기"가 다시 활성화되는 문제가 있었다. null로
     * 남겨 "서버가 값을 안 줬음"과 "false"를 구분한다(처리는 ChallengeDetailViewModel.applyDetail 참고).
     */
    val verifiedInCurrentPeriod: Boolean?,
)
