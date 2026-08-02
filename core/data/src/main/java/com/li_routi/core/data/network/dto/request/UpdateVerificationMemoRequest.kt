package com.li_routi.core.data.network.dto.request

/** PATCH /api/challenges/{challengeId}/verifications/{verificationId} 요청 body. */
data class UpdateVerificationMemoRequest(
    val content: String,
)
