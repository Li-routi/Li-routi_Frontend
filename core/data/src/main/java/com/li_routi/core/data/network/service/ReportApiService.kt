package com.li_routi.core.data.network.service

import com.li_routi.core.data.network.dto.response.ApiResponse
import com.li_routi.core.data.network.dto.response.MonthlyReportResponse
import com.li_routi.core.data.network.dto.response.WeeklyReportResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ReportApiService {

    /** [date]가 속한 주(일~토)의 일별 예정/완료 건수와 그 주가 표시되는 달 기준 활동 통계를 조회한다. */
    @GET("api/members/me/reports/weekly")
    suspend fun getWeeklyReport(
        @Query("date") date: String?,
    ): ApiResponse<WeeklyReportResponse>

    /** [yearMonth]("yyyy-MM")의 일별 예정/완료 건수와 활동 통계를 조회한다. */
    @GET("api/members/me/reports/monthly")
    suspend fun getMonthlyReport(
        @Query("yearMonth") yearMonth: String?,
    ): ApiResponse<MonthlyReportResponse>
}
