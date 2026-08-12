package com.li_routi.core.data.mapper

import com.li_routi.core.data.network.dto.response.AchievementCategoryResponse
import com.li_routi.core.data.network.dto.response.AchievementResponse
import com.li_routi.core.data.network.dto.response.AchievementsResponse
import com.li_routi.core.domain.achievement.Achievement
import com.li_routi.core.domain.achievement.AchievementCategory
import com.li_routi.core.domain.achievement.AchievementCategoryGroup

fun AchievementsResponse.toDomain(): List<AchievementCategoryGroup> = categories.map { it.toDomain() }

fun AchievementCategoryResponse.toDomain(): AchievementCategoryGroup = AchievementCategoryGroup(
    category = category.toAchievementCategory(),
    achievements = achievements.map { it.toDomain() },
)

private fun String.toAchievementCategory(): AchievementCategory =
    runCatching { AchievementCategory.valueOf(this) }.getOrDefault(AchievementCategory.UNKNOWN)

fun AchievementResponse.toDomain(): Achievement = Achievement(
    achievementId = achievementId,
    code = code,
    name = name,
    conditionDesc = conditionDesc,
    status = status,
    // progress가 없는 업적(달성 여부만 있는 업적)은 진행도 0으로 취급한다 — API 문서의 "진행도가 없는
    // 업적은 진행도 0" 규칙을 progress 객체 자체가 없는 경우까지 확장한 것이다.
    progressCurrent = progress?.current ?: 0,
    progressTarget = progress?.target ?: 0,
    topazReward = topazReward,
    badgeYn = badgeYn,
    limitedOutfitYn = limitedOutfitYn,
    achievedAt = achievedAt,
    claimedAt = claimedAt,
)
