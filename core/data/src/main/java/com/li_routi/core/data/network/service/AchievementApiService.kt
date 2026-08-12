package com.li_routi.core.data.network.service

import com.li_routi.core.data.network.dto.response.AchievementsResponse
import com.li_routi.core.data.network.dto.response.ApiResponse
import retrofit2.http.GET

interface AchievementApiService {

    /** 마이 > 업적 화면에 필요한 전체 업적 목록을 카테고리(rare/epic/unique)별로 묶어 조회한다. */
    @GET("api/achievements")
    suspend fun getAchievements(): ApiResponse<AchievementsResponse>
}
