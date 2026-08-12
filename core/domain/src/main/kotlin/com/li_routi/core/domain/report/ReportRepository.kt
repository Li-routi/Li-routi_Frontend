package com.li_routi.core.domain.report

import com.li_routi.core.common.kotlin.util.ResultState

interface ReportRepository {

    /** [date]("yyyy-MM-dd")가 속한 주의 리포트를 조회한다. 생략하면 오늘(KST)이 속한 주. */
    suspend fun getWeeklyReport(date: String? = null): ResultState<WeeklyReport>

    /** [yearMonth]("yyyy-MM")의 리포트를 조회한다. 생략하면 이번 달(KST). */
    suspend fun getMonthlyReport(yearMonth: String? = null): ResultState<MonthlyReport>
}
