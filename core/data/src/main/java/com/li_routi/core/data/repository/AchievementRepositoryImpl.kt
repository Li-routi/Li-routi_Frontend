package com.li_routi.core.data.repository

import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.common.kotlin.util.safeApiCall
import com.li_routi.core.data.mapper.toDomain
import com.li_routi.core.data.network.apiCall
import com.li_routi.core.data.network.service.AchievementApiService
import com.li_routi.core.domain.achievement.AchievementCategoryGroup
import com.li_routi.core.domain.achievement.AchievementClaimResult
import com.li_routi.core.domain.achievement.AchievementRepository

class AchievementRepositoryImpl(
    private val api: AchievementApiService,
) : AchievementRepository {

    override suspend fun getAchievements(): ResultState<List<AchievementCategoryGroup>> = safeApiCall {
        apiCall { api.getAchievements() }.toDomain()
    }

    override suspend fun claimAchievement(achievementId: Long): ResultState<AchievementClaimResult> = safeApiCall {
        apiCall { api.claim(achievementId) }.toDomain()
    }
}
