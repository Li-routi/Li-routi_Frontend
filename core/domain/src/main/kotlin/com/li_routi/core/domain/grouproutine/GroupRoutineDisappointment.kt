package com.li_routi.core.domain.grouproutine

data class GroupRoutineDisappointment(
    val verificationId: Long,
    val count: Long,
    val disappointed: Boolean,
)
