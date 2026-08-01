package com.li_routi.core.data.network.service

import com.li_routi.core.data.network.dto.response.ApiResponse
import com.li_routi.core.data.network.dto.response.HomeSummaryResponse
import retrofit2.http.GET

interface HomeApiService {

    @GET("api/home")
    suspend fun getHome(): ApiResponse<HomeSummaryResponse>
}
