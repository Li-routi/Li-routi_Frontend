package com.li_routi.core.data.mapper

import com.li_routi.core.data.network.dto.response.MonthlyReportResponse
import com.li_routi.core.data.network.dto.response.ReportDayResponse
import com.li_routi.core.data.network.dto.response.ReportStatsResponse
import com.li_routi.core.data.network.dto.response.WeeklyReportResponse
import com.li_routi.core.domain.report.MonthlyReport
import com.li_routi.core.domain.report.ReportDay
import com.li_routi.core.domain.report.ReportStats
import com.li_routi.core.domain.report.WeeklyReport

fun WeeklyReportResponse.toDomain(): WeeklyReport = WeeklyReport(
    displayMonth = displayMonth,
    weekOfMonth = weekOfMonth,
    weekStart = weekStart,
    weekEnd = weekEnd,
    days = days.map { it.toDomain() },
    stats = stats.toDomain(),
)

fun MonthlyReportResponse.toDomain(): MonthlyReport = MonthlyReport(
    yearMonth = yearMonth,
    days = days.map { it.toDomain() },
    stats = stats.toDomain(),
)

fun ReportDayResponse.toDomain(): ReportDay = ReportDay(
    date = date,
    scheduledCount = scheduledCount,
    completedCount = completedCount,
    isToday = isToday,
)

fun ReportStatsResponse.toDomain(): ReportStats = ReportStats(
    completedRoutineCount = completedRoutineCount,
    averageCompletionRate = averageCompletionRate,
    completedChallengeCount = completedChallengeCount,
    earnedCoinCount = earnedCoinCount,
)
