package com.li_routi.core.data.repository

import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.common.kotlin.util.safeApiCall
import com.li_routi.core.data.mapper.toDomain
import com.li_routi.core.data.network.apiCall
import com.li_routi.core.data.network.apiCallUnit
import com.li_routi.core.data.network.dto.request.RepresentativeAchievementRequest
import com.li_routi.core.data.network.dto.request.SelectWaveRoutineRequest
import com.li_routi.core.data.network.service.AchievementApiService
import com.li_routi.core.domain.achievement.AchievementCategoryGroup
import com.li_routi.core.domain.achievement.AchievementClaimResult
import com.li_routi.core.domain.achievement.AchievementRepository
import com.li_routi.core.domain.achievement.SelectableAchievement
import com.li_routi.core.domain.achievement.WaveRoutineStatus

class AchievementRepositoryImpl(
    private val api: AchievementApiService,
) : AchievementRepository {

    override suspend fun getAchievements(): ResultState<List<AchievementCategoryGroup>> = safeApiCall {
        apiCall { api.getAchievements() }.toDomain()
    }

    override suspend fun claimAchievement(achievementId: Long): ResultState<AchievementClaimResult> = safeApiCall {
        apiCall { api.claim(achievementId) }.toDomain()
    }

    override suspend fun getWaveRoutineStatus(): ResultState<WaveRoutineStatus> = safeApiCall {
        apiCall { api.getWaveRoutineStatus() }.toDomain()
    }

    override suspend fun selectWaveRoutine(memberRoutineId: Long): ResultState<Unit> = safeApiCall {
        apiCallUnit { api.selectWaveRoutine(SelectWaveRoutineRequest(memberRoutineId)) }
    }

    override suspend fun getSelectableRepresentativeAchievements(): ResultState<List<SelectableAchievement>> =
        safeApiCall {
            apiCall { api.getSelectableRepresentativeAchievements() }.toDomain()
        }

    override suspend fun setRepresentativeAchievement(achievementId: Long): ResultState<Unit> = safeApiCall {
        apiCallUnit { api.setRepresentativeAchievement(RepresentativeAchievementRequest(achievementId)) }
    }

    override suspend fun clearRepresentativeAchievement(): ResultState<Unit> = safeApiCall {
        apiCallUnit { api.clearRepresentativeAchievement() }
    }
}
