package com.li_routi.core.data.network.dto.request

/** POST /api/challenges/{challengeId}/verifications/{verificationId}/reports 요청 body. */
data class ReportRequest(
    val reportType: String,
    val reason: String?,
)
