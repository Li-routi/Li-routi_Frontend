package com.li_routi.core.data.network.dto.request

/** POST /api/groups/join 요청 body. */
data class JoinGroupRequest(
    val inviteCode: String,
)
