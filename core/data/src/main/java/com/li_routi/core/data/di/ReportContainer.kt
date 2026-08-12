package com.li_routi.core.data.di

import com.li_routi.core.data.network.NetworkModule
import com.li_routi.core.data.repository.ReportRepositoryImpl
import com.li_routi.core.domain.report.GetMonthlyReportUseCase
import com.li_routi.core.domain.report.GetWeeklyReportUseCase
import com.li_routi.core.domain.report.ReportRepository

/**
 * Hilt 등 DI 프레임워크가 붙기 전까지 사용하는 수동 구성 root.
 * feature 모듈은 여기서 필요한 UseCase만 가져다 쓴다.
 */
object ReportContainer {

    private val repository: ReportRepository by lazy {
        ReportRepositoryImpl(NetworkModule.reportApiService)
    }

    val getWeeklyReportUseCase: GetWeeklyReportUseCase by lazy {
        GetWeeklyReportUseCase(repository)
    }

    val getMonthlyReportUseCase: GetMonthlyReportUseCase by lazy {
        GetMonthlyReportUseCase(repository)
    }
}
