package com.li_routi.core.domain.achievement

import com.li_routi.core.common.kotlin.util.ResultState

class GetAchievementsUseCase(
    private val repository: AchievementRepository,
) {
    suspend operator fun invoke(): ResultState<List<AchievementCategoryGroup>> = repository.getAchievements()
}
