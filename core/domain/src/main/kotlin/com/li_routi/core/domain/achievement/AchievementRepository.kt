package com.li_routi.core.domain.achievement

import com.li_routi.core.common.kotlin.util.ResultState

interface AchievementRepository {

    /** 마이 > 업적 화면에 필요한 전체 업적 목록을 카테고리별로 묶어 조회한다. */
    suspend fun getAchievements(): ResultState<List<AchievementCategoryGroup>>

    /** 달성한 업적의 보상(토파즈/한정 의상 등)을 수령한다. */
    suspend fun claimAchievement(achievementId: Long): ResultState<AchievementClaimResult>
}
