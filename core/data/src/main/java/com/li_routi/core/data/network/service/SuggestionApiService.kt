package com.li_routi.core.data.network.service

import com.li_routi.core.data.network.dto.request.CreateSuggestionRequest
import com.li_routi.core.data.network.dto.response.ApiResponse
import com.li_routi.core.data.network.dto.response.SuggestionCategoriesResponse
import com.li_routi.core.data.network.dto.response.SuggestionItemResponse
import com.li_routi.core.data.network.dto.response.SuggestionListResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SuggestionApiService {

    @GET("api/members/me/suggestions")
    suspend fun getMySuggestions(
        @Query("cursor") cursor: Long?,
        @Query("size") size: Int?,
        @Query("keyword") keyword: String?,
        @Query("categoryId") categoryId: Long?,
    ): ApiResponse<SuggestionListResponse>

    @POST("api/members/me/suggestions")
    suspend fun createSuggestion(
        @Body body: CreateSuggestionRequest,
    ): ApiResponse<SuggestionItemResponse>

    /** 등록할 때 고르는 분류. 내려간 분류는 실리지 않고, 노출 순서대로 이미 정렬돼 있다. */
    @GET("api/members/me/suggestions/categories")
    suspend fun getCategories(): ApiResponse<SuggestionCategoriesResponse>
}
