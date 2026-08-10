package com.li_routi.core.data.network.dto.response

data class GroupRoutineLikeResponse(
    val verificationId: Long,
    val likeCount: Long,
    val liked: Boolean,
)
