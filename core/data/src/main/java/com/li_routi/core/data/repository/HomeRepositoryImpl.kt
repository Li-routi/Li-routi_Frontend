package com.li_routi.core.data.repository

import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.mapper.toDomain
import com.li_routi.core.data.network.apiCall
import com.li_routi.core.data.network.safeDataApiCall
import com.li_routi.core.data.network.service.HomeApiService
import com.li_routi.core.domain.home.HomeRepository
import com.li_routi.core.domain.home.HomeSummary

class HomeRepositoryImpl(
    private val api: HomeApiService,
) : HomeRepository {

    override suspend fun getHomeSummary(): ResultState<HomeSummary> = safeDataApiCall {
        apiCall { api.getHome() }.toDomain()
    }
}
