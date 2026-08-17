package com.li_routi.core.data.network.dto.response

/**
 * 업적 한 건. `status`는 스웨거 스키마 기준 `IN_PROGRESS`/`ACHIEVED`/`CLAIMED` 3단계다 — 도메인에서는
 * 문자열 그대로 두되 `ACHIEVED`(달성했지만 미수령)일 때만 [ClaimAchievementUseCase] 호출이
 * 가능하다고 본다("달성" 자체는 `achievedAt` 존재 여부로 판단해 `ACHIEVED`/`CLAIMED` 둘 다 포함한다).
 * `progress`는 진행률 없이 달성 여부만 있는 업적(예: "첫 좋아요")에서 실제로 null이 내려온다.
 * `conditionProgresses`(세부 조건별 진행도)는 현재 화면이 단일 진행률만 보여줘서 매핑하지 않는다.
 */
data class AchievementResponse(
    val achievementId: Long,
    val code: String,
    val name: String,
    val conditionDesc: String,
    val status: String,
    val progress: AchievementProgressResponse?,
    val topazReward: Int,
    val badgeYn: Boolean,
    val limitedOutfitYn: Boolean,
    val achievedAt: String?,
    val claimedAt: String?,
    val badgeImageUrl: String? = null,
)

data class AchievementProgressResponse(
    val current: Int,
    val target: Int,
)

/** POST api/achievements/{achievementId}/claim 응답. */
data class ClaimResponse(
    val achievementId: Long,
    val freeBalanceAfter: Int,
    val rewardApplied: Boolean,
)
