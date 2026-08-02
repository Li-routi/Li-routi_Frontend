package com.li_routi.core.data.network.dto.response

/** PATCH /api/challenges/{challengeId}/verifications/{verificationId} 응답 result. */
data class UpdateVerificationMemoResponse(
    val verificationId: Long,
    val content: String,
)
