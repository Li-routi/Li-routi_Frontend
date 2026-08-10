package com.li_routi.core.domain.grouproutine

data class GroupRoutineLike(
    val verificationId: Long,
    val likeCount: Long,
    val liked: Boolean,
)
