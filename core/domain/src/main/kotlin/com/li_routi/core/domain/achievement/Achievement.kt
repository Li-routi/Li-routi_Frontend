package com.li_routi.core.domain.achievement

/** GET /api/achievements 조회 결과 — 카테고리(rare/epic/unique)별로 묶인 업적 목록. */
data class AchievementCategoryGroup(
    val category: AchievementCategory,
    val achievements: List<Achievement>,
)

/**
 * [EGG]는 캐릭터 해금용 알을 얻는 업적 분류로, 화면에는 "캐릭터" 등급으로 보여준다.
 * [UNKNOWN]은 서버가 또 새 카테고리를 추가했을 때를 대비한 폴백이다.
 */
enum class AchievementCategory { RARE, EPIC, UNIQUE, EGG, UNKNOWN }

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
    /** 숨김(시크릿) 업적([hiddenYn])은 실제로 이 필드가 null로 내려온다 — 달성 전까지 조건을 감추는 의도. */
    val conditionDesc: String?,
    val status: String,
    val progressCurrent: Int,
    val progressTarget: Int,
    val topazReward: Int,
    val badgeYn: Boolean,
    val limitedOutfitYn: Boolean,
    val achievedAt: String?,
    val claimedAt: String?,
    /** 서버가 제공하는 업적 뱃지 이미지. 없는 업적도 있어(null) 그럴 땐 앱 내장 이미지로 대체한다. */
    val badgeImageUrl: String? = null,
    /** 숨김(시크릿) 업적 여부. 달성 전에는 목록에서 감춰야 한다. */
    val hiddenYn: Boolean = false,
    /** 조건이 여러 개인 업적의 조건별 진행도. 단일 조건 업적은 비어 있거나 [progressCurrent]/
     * [progressTarget]과 같은 값 하나만 담겨 온다. */
    val conditionProgresses: List<AchievementConditionProgress> = emptyList(),
) {
    val isInProgress: Boolean get() = status == "IN_PROGRESS"
    val isAchieved: Boolean get() = achievedAt != null

    /** 달성했지만 보상을 아직 수령하지 않아, [ClaimAchievementUseCase]를 호출할 수 있는 상태인지. */
    val isClaimable: Boolean get() = status == "ACHIEVED"
}

/** 업적 조건 하나의 진행도. */
data class AchievementConditionProgress(
    val conditionKey: String,
    val current: Int,
    val target: Int,
)

/** POST .../claim 응답 — 수령 처리 후의 무료(토파즈) 잔액과 보상 지급 여부. */
data class AchievementClaimResult(
    val achievementId: Long,
    val freeBalanceAfter: Int,
    val rewardApplied: Boolean,
)

/**
 * GET /api/achievements/representative/selectable 조회 결과 — 대표 업적으로 선택 가능한 업적 하나.
 * 배지 이미지가 등록돼 있고 보상까지 수령(CLAIMED)한 업적만 이 목록에 온다.
 */
data class SelectableAchievement(
    val achievementId: Long,
    val name: String,
    val badgeImageUrl: String?,
    val isRepresentative: Boolean,
)
