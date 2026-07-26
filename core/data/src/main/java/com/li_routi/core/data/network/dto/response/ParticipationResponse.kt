package com.li_routi.core.data.network.dto.response

/** POST/DELETE /api/challenges/{challengeId}/participation 응답 result. */
data class ParticipationResponse(
    val challengeId: Long,
    val participating: Boolean,
    val participationRound: Int,
)
