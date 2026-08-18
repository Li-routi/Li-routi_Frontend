package com.li_routi.core.data.network.dto.response

/**
 * 업적 한 건. `status`는 스웨거 스키마 기준 `IN_PROGRESS`/`ACHIEVED`/`CLAIMED` 3단계다 — 도메인에서는
 * 문자열 그대로 두되 `ACHIEVED`(달성했지만 미수령)일 때만 [ClaimAchievementUseCase] 호출이
 * 가능하다고 본다("달성" 자체는 `achievedAt` 존재 여부로 판단해 `ACHIEVED`/`CLAIMED` 둘 다 포함한다).
 * `progress`는 진행률 없이 달성 여부만 있는 업적(예: "첫 좋아요")에서 실제로 null이 내려온다.
 */
data class AchievementResponse(
    val achievementId: Long,
    val code: String,
    val name: String,
    // 숨김(시크릿) 업적(hiddenYn=true)은 실측상 conditionDesc가 null로 내려온다 — 이걸 non-null로
    // 선언해뒀다가 Achievement(도메인) 생성자의 null 체크에서 NPE가 나 목록 조회 전체가 실패했었다.
    val conditionDesc: String?,
    val status: String,
    val progress: AchievementProgressResponse?,
    val conditionProgresses: List<ConditionProgressResponse>? = null,
    val topazReward: Int,
    val badgeYn: Boolean,
    val limitedOutfitYn: Boolean,
    val hiddenYn: Boolean = false,
    val achievedAt: String?,
    val claimedAt: String?,
    val badgeImageUrl: String? = null,
)

data class AchievementProgressResponse(
    val current: Int,
    val target: Int,
)

data class ConditionProgressResponse(
    val conditionKey: String?,
    val current: Int,
    val target: Int,
)

/** POST api/achievements/{achievementId}/claim 응답. */
data class ClaimResponse(
    val achievementId: Long,
    val freeBalanceAfter: Int,
    val rewardApplied: Boolean,
)

/** GET api/achievements/wave-routine 응답. */
data class WaveRoutineStatusResponse(
    val memberRoutineId: Long?,
    val routineName: String?,
    val currentStreak: Int?,
    val targetStreak: Int?,
)
