package com.li_routi.core.domain.report

import com.li_routi.core.common.kotlin.util.ResultState

class GetMonthlyReportUseCase(
    private val repository: ReportRepository,
) {
    suspend operator fun invoke(yearMonth: String? = null): ResultState<MonthlyReport> = repository.getMonthlyReport(yearMonth)
}
