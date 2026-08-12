package com.li_routi.core.data.repository

import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.common.kotlin.util.safeApiCall
import com.li_routi.core.data.mapper.toDomain
import com.li_routi.core.data.network.apiCall
import com.li_routi.core.data.network.service.ReportApiService
import com.li_routi.core.domain.report.MonthlyReport
import com.li_routi.core.domain.report.ReportRepository
import com.li_routi.core.domain.report.WeeklyReport

class ReportRepositoryImpl(
    private val api: ReportApiService,
) : ReportRepository {

    override suspend fun getWeeklyReport(date: String?): ResultState<WeeklyReport> = safeApiCall {
        apiCall { api.getWeeklyReport(date) }.toDomain()
    }

    override suspend fun getMonthlyReport(yearMonth: String?): ResultState<MonthlyReport> = safeApiCall {
        apiCall { api.getMonthlyReport(yearMonth) }.toDomain()
    }
}
