package com.li_routi.core.domain.achievement

import com.li_routi.core.common.kotlin.util.ResultState

class ClaimAchievementUseCase(
    private val repository: AchievementRepository,
) {
    suspend operator fun invoke(achievementId: Long): ResultState<AchievementClaimResult> =
        repository.claimAchievement(achievementId)
}
