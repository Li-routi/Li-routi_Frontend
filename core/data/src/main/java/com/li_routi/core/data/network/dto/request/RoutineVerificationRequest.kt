package com.li_routi.core.data.network.dto.request

/** POST .../verifications 요청 body (개인·그룹 공통). */
data class RoutineVerificationRequest(
    val mediaKey: String,
    val content: String?,
)
