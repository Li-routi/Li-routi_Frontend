package com.li_routi.core.domain.report

data class ReportDay(
    val date: String,
    val scheduledCount: Int,
    val completedCount: Int,
    val isToday: Boolean,
)

/** "활동 통계" — 주간 조회여도 그 주가 표시되는 달 기준으로 내려온다. */
data class ReportStats(
    val completedRoutineCount: Int,
    val averageCompletionRate: Int,
    val completedChallengeCount: Int,
    val earnedCoinCount: Int,
)

/** GET /api/members/me/reports/weekly 조회 결과. [days]는 일~토 7일. */
data class WeeklyReport(
    val displayMonth: String,
    val weekOfMonth: Int,
    val weekStart: String,
    val weekEnd: String,
    val days: List<ReportDay>,
    val stats: ReportStats,
)

/** GET /api/members/me/reports/monthly 조회 결과. [days]는 해당 월에 속한 날짜만 온다. */
data class MonthlyReport(
    val yearMonth: String,
    val days: List<ReportDay>,
    val stats: ReportStats,
)
