package com.li_routi.core.data.network.dto.response

/** GET /api/members/me/reports/weekly 응답. [days]는 일~토 7일. */
data class WeeklyReportResponse(
    val displayMonth: String,
    val weekOfMonth: Int,
    val weekStart: String,
    val weekEnd: String,
    val days: List<ReportDayResponse>,
    val stats: ReportStatsResponse,
)
