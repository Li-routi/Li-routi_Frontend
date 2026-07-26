package com.li_routi.core.domain.challenge

/** 챌린지 분류. 필터 칩("전체"는 null)과 목록 조회 쿼리에 사용된다. */
enum class ChallengeCategory {
    HEALTH,
    EXERCISE,
    STUDY,
    LIFE,
    HOBBY,
}

/** 챌린지 인증 주기. */
enum class RoutineCycle {
    DAILY,
    WEEKLY,
    MONTHLY,
}

/** "챌린지 찾아보기" 목록/상세에서 공통으로 쓰이는 챌린지 도메인 모델. */
data class Challenge(
    val id: Long,
    val name: String,
    val description: String,
    val imageUrl: String,
    val category: ChallengeCategory,
    val routineCycle: RoutineCycle,
    val participantCount: Long,
    val verificationPostCount: Long,
)
