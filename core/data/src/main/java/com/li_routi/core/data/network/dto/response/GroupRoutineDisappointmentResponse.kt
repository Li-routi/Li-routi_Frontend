package com.li_routi.core.data.network.dto.response

data class GroupRoutineDisappointmentResponse(
    val verificationId: Long,
    val count: Long,
    val disappointed: Boolean,
)
