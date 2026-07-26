package com.li_routi.core.domain.challenge

/** 챌린지 목록 커서 기반 페이지네이션 결과. */
data class ChallengePage(
    val challenges: List<Challenge>,
    val nextCursor: Long?,
    val hasNext: Boolean,
)
