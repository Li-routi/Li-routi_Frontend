package com.li_routi.core.domain.report

import com.li_routi.core.common.kotlin.util.ResultState

class GetWeeklyReportUseCase(
    private val repository: ReportRepository,
) {
    suspend operator fun invoke(date: String? = null): ResultState<WeeklyReport> = repository.getWeeklyReport(date)
}
