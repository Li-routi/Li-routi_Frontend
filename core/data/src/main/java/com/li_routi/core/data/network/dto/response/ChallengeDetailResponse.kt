package com.li_routi.core.data.network.dto.response

/** GET /api/challenges/{challengeId} 응답 result. */
data class ChallengeDetailResponse(
    val challengeId: Long,
    val name: String,
    val description: String,
    val imageUrl: String,
    val category: String,
    val routineCycle: String,
    val reward: Int,
    val participating: Boolean,
    val participantCount: Long,
    val verificationPostCount: Long,
    val todayCompletionCount: Long,
)
