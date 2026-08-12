package com.li_routi.core.data.network.dto.response

/**
 * 업적 한 건. `status`는 문서에 "진행도가 없으면 IN_PROGRESS"만 명시돼 있고 달성/보상수령 상태의
 * 정확한 값은 나와 있지 않아, 도메인에서는 문자열 그대로 두고 "IN_PROGRESS가 아니면 달성"으로 판단한다.
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
)

data class AchievementProgressResponse(
    val current: Int,
    val target: Int,
)
