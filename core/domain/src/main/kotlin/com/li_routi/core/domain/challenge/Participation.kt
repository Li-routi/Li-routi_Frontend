package com.li_routi.core.domain.challenge

/** 챌린지 참여/이탈(POST·DELETE .../participation) 결과. */
data class Participation(
    val challengeId: Long,
    val participating: Boolean,
    val participationRound: Int,
)
