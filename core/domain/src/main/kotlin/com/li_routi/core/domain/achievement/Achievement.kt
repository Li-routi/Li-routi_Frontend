package com.li_routi.core.domain.achievement

/** GET /api/achievements 조회 결과 — 카테고리(rare/epic/unique)별로 묶인 업적 목록. */
data class AchievementCategoryGroup(
    val category: AchievementCategory,
    val achievements: List<Achievement>,
)

/** [UNKNOWN]은 서버가 새 카테고리를 추가했을 때를 대비한 폴백이다. */
enum class AchievementCategory { RARE, EPIC, UNIQUE, UNKNOWN }

/**
 * 업적 한 건. [status]는 문서에 진행 전 상태(`IN_PROGRESS`)만 명시돼 있어 도메인에서도 문자열 그대로
 * 둔다 — [isInProgress]로만 분기하면 달성/보상수령 등 나머지 상태의 정확한 이름을 몰라도 안전하다.
 *
 * "달성" 여부는 `status`가 `IN_PROGRESS`가 아닌 것으로 판단하지 않는다 — 문서에 없는 새 상태(예: 실패/
 * 만료 등)가 추가되면 그런 항목도 전부 "달성"으로 잘못 취급될 수 있기 때문이다. 대신 서버가 실제로
 * 달성 시점을 채워주는 [achievedAt]의 존재 여부로 판단한다([isAchieved]).
 */
data class Achievement(
    val achievementId: Long,
    val code: String,
    val name: String,
    val conditionDesc: String,
    val status: String,
    val progressCurrent: Int,
    val progressTarget: Int,
    val topazReward: Int,
    val badgeYn: Boolean,
    val limitedOutfitYn: Boolean,
    val achievedAt: String?,
    val claimedAt: String?,
) {
    val isInProgress: Boolean get() = status == "IN_PROGRESS"
    val isAchieved: Boolean get() = achievedAt != null
}
