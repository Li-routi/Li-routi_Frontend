package com.li_routi.core.data.network.dto.response

/** GET /api/routines 응답 result. */
data class MemberRoutineListResponse(
    val routines: List<CreatedRoutineResponse> = emptyList(),
)
