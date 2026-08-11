package com.li_routi.core.domain.challenge

/** 챌린지 분류. 필터 칩("전체"는 null)과 목록 조회 쿼리에 사용된다. */
enum class ChallengeCategory {
    HEALTH,
    EXERCISE,
    STUDY,
    LIFE,
    HOBBY,

    /**
     * 서버가 내려준 값이 위 5개 중 어디에도 해당하지 않을 때의 방어적 폴백(예: 서버에 새 카테고리가
     * 추가됐는데 앱이 아직 대응하지 못한 경우). 필터 칩 목록([filterOptions] 등)에는 노출하지 않는다 —
     * 사용자가 직접 고를 수 있는 값이 아니라, 매핑 실패를 조용히 삼키지 않기 위한 표시 전용 값이다.
     */
    UNKNOWN,
}

/** 챌린지 인증 주기. */
enum class RoutineCycle {
    DAILY,
    WEEKLY,
    MONTHLY,

    /** [ChallengeCategory.UNKNOWN]과 같은 이유의 방어적 폴백. */
    UNKNOWN,
}

/** "챌린지 찾아보기" 목록/상세에서 공통으로 쓰이는 챌린지 도메인 모델. */
data class Challenge(
    val id: Long,
    val name: String,
    val description: String,
    val imageUrl: String,
    val category: ChallengeCategory,
    val routineCycle: RoutineCycle,
    /** 챌린지 달성 시 부여되는 재화 수량. */
    val reward: Int,
    val participantCount: Long,
    val verificationPostCount: Long,
)
