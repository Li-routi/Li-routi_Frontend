package com.li_routi.core.data.network.dto.response

/** GET /api/members/me/reports/monthly 응답. [days]는 해당 월에 속한 날짜만(선행/후행 패딩 없이) 온다. */
data class MonthlyReportResponse(
    val yearMonth: String,
    val days: List<ReportDayResponse>,
    val stats: ReportStatsResponse,
)
