package com.li_routi.core.domain.challenge

/**
 * "챌린지" 메인 화면에서 보여주는, 내가 참여 중인 챌린지 카드 (GET /api/members/me/challenges).
 * 목록/상세 API와 달리 routineCycle·통계는 내려오지 않는다.
 */
data class MyChallenge(
    val id: Long,
    val name: String,
    val description: String,
    val imageUrl: String,
    val category: ChallengeCategory,
)
