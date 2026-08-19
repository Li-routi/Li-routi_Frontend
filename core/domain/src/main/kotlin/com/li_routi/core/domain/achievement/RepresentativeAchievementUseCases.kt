package com.li_routi.core.domain.achievement

import com.li_routi.core.common.kotlin.util.ResultState

class GetSelectableRepresentativeAchievementsUseCase(
    private val repository: AchievementRepository,
) {
    suspend operator fun invoke(): ResultState<List<SelectableAchievement>> =
        repository.getSelectableRepresentativeAchievements()
}

class SetRepresentativeAchievementUseCase(
    private val repository: AchievementRepository,
) {
    suspend operator fun invoke(achievementId: Long): ResultState<Unit> =
        repository.setRepresentativeAchievement(achievementId)
}

class ClearRepresentativeAchievementUseCase(
    private val repository: AchievementRepository,
) {
    suspend operator fun invoke(): ResultState<Unit> = repository.clearRepresentativeAchievement()
}
