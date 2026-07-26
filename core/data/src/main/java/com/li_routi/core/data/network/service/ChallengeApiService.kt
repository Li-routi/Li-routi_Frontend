package com.li_routi.core.data.network.service

import com.li_routi.core.data.network.dto.response.ApiResponse
import com.li_routi.core.data.network.dto.response.ChallengeListingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ChallengeApiService {

    @GET("api/challenges")
    suspend fun getChallenges(
        @Query("category") category: String?,
        @Query("keyword") keyword: String?,
        @Query("cursor") cursor: Long?,
        @Query("size") size: Int?,
    ): ApiResponse<ChallengeListingResponse>
}
