package com.li_routi.core.data.network.dto.response

/** 리포트(주간/월간) 하루치 예정/완료 건수. */
data class ReportDayResponse(
    val date: String,
    val scheduledCount: Int,
    val completedCount: Int,
    val isToday: Boolean,
)
