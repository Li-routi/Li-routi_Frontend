package com.li_routi.core.data.network.dto.response

/** POST /api/challenges/{challengeId}/verifications/{verificationId}/reports 응답 result. */
data class ReportResponse(
    val reportId: Long,
    val verificationId: Long,
)
