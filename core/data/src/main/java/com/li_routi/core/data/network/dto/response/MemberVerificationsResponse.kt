package com.li_routi.core.data.network.dto.response

/** GET /api/members/me/verifications 응답. */
data class MemberVerificationsResponse(
    val date: String,
    val verifications: List<MemberVerificationResponse>,
)
