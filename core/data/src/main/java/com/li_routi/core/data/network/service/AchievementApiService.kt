package com.li_routi.core.data.network.service

import com.li_routi.core.data.network.dto.response.AchievementsResponse
import com.li_routi.core.data.network.dto.response.ApiResponse
import com.li_routi.core.data.network.dto.response.ClaimResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AchievementApiService {

    /** 마이 > 업적 화면에 필요한 전체 업적 목록을 카테고리(rare/epic/unique/egg)별로 묶어 조회한다. */
    @GET("api/achievements")
    suspend fun getAchievements(): ApiResponse<AchievementsResponse>

    /** 달성한 업적의 보상을 수령한다. */
    @POST("api/achievements/{achievementId}/claim")
    suspend fun claim(@Path("achievementId") achievementId: Long): ApiResponse<ClaimResponse>
}
