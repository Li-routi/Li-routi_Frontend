package com.li_routi.core.data.mapper

import com.li_routi.core.data.network.dto.response.AchievementCategoryResponse
import com.li_routi.core.data.network.dto.response.AchievementResponse
import com.li_routi.core.data.network.dto.response.AchievementsResponse
import com.li_routi.core.data.network.dto.response.ClaimResponse
import com.li_routi.core.domain.achievement.Achievement
import com.li_routi.core.domain.achievement.AchievementCategory
import com.li_routi.core.domain.achievement.AchievementCategoryGroup
import com.li_routi.core.domain.achievement.AchievementClaimResult

fun AchievementsResponse.toDomain(): List<AchievementCategoryGroup> = categories.map { it.toDomain() }

fun AchievementCategoryResponse.toDomain(): AchievementCategoryGroup = AchievementCategoryGroup(
    category = category.toAchievementCategory(),
    achievements = achievements.map { it.toDomain() },
)

// 문서 설명은 소문자(rare/epic/unique)로 적혀 있고 실측 응답은 대문자(RARE/EPIC/UNIQUE)였다 — 어느
// 쪽으로 오든 안전하게 매칭되도록 대문자로 정규화한 뒤 비교한다.
private fun String.toAchievementCategory(): AchievementCategory =
    runCatching { AchievementCategory.valueOf(trim().uppercase()) }.getOrDefault(AchievementCategory.UNKNOWN)

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
    badgeImageUrl = badgeImageUrl?.takeIf { it.isNotBlank() },
)

fun ClaimResponse.toDomain(): AchievementClaimResult = AchievementClaimResult(
    achievementId = achievementId,
    freeBalanceAfter = freeBalanceAfter,
    rewardApplied = rewardApplied,
)
