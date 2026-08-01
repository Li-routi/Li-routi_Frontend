package com.li_routi.core.domain.home

import com.li_routi.core.common.kotlin.util.ResultState

class GetHomeSummaryUseCase(
    private val repository: HomeRepository,
) {
    suspend operator fun invoke(): ResultState<HomeSummary> = repository.getHomeSummary()
}
