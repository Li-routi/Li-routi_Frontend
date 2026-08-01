package com.li_routi.core.data.network.dto.response

/** POST /api/challenges/{challengeId}/verifications 응답. */
data class CreateVerificationResponse(
    val verificationId: Long,
    val challengeId: Long,
    val verifiedDate: String,
    val verifiedAt: String,
    val imageUrl: String?,
    val content: String,
    val currentStreak: Int,
    val reverified: Boolean,
)
