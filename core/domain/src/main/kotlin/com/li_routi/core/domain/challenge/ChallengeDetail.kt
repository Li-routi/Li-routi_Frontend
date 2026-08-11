package com.li_routi.core.domain.challenge

/** 챌린지 상세 화면에서 보여주는 챌린지 정보. */
data class ChallengeDetail(
    val id: Long,
    val name: String,
    val description: String,
    val imageUrl: String,
    val category: ChallengeCategory,
    val routineCycle: RoutineCycle,
    val reward: Int,
    /** 조회자(로그인 사용자) 참여 여부. 비로그인이면 항상 false. */
    val participating: Boolean,
    val participantCount: Long,
    val verificationPostCount: Long,
    /** 조회자(로그인 사용자)가 현재 인증 주기에 이미 인증했는지 여부. null이면 서버가 값을 내려주지 않은 것(알 수 없음). */
    val verifiedInCurrentPeriod: Boolean?,
)
