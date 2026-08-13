package com.li_routi.feature.mypage.vm

import com.li_routi.core.domain.achievement.Achievement
import com.li_routi.core.domain.achievement.AchievementCategory
import com.li_routi.core.domain.achievement.AchievementCategoryGroup

/**
 * `GET /api/achievements` 목데이터. Figma node `6008:31220`(전체)/`6008:31578`(레어)/`6008:31510`(에픽)/
 * `6008:31459`(유니크)/`6008:31728`(달성) 기준 — 실제 서버 API가 준비되기 전까지 [AchievementViewModel]이
 * 이 데이터를 대신 쓴다. API가 나오면 [AchievementViewModel.load]에서 이 객체 참조를 지우고
 * `getAchievementsUseCase()` 호출로 되돌리면 된다.
 */
internal object AchievementMockData {
    val groups: List<AchievementCategoryGroup> = listOf(
        AchievementCategoryGroup(
            category = AchievementCategory.RARE,
            achievements = listOf(
                achievement(1, "FIRST_LIKE", "첫 좋아요", "좋아요를 처음 받아보세요", 0, 1, badgeYn = true),
                achievement(2, "FIRST_POKE", "첫 쿡쿡 찌르기", "쿡쿡 찌르기를 처음 보내보세요", 0, 1, badgeYn = true),
                achievement(3, "FIRST_ROOM", "첫 방만들기", "모임방을 처음 만들어보세요", 1, 1, badgeYn = true, achieved = true),
                achievement(4, "FIRST_INVITE_JOIN", "첫 초대 참여", "초대를 통해 처음 참여해보세요", 0, 1, badgeYn = true),
                achievement(5, "HEALTH_START", "건강 시작", "건강 카테고리 루틴을 처음 만들어보세요", 0, 1, badgeYn = true),
                achievement(6, "FIRST_GEM_EXCHANGE", "첫 오렌지젬 교환", "오렌지젬으로 아이템을 처음 교환해보세요", 0, 1, badgeYn = true),
                achievement(7, "VERIFY_5", "인증 5회", "루틴 인증을 5회 완료하세요", 0, 5, badgeYn = true),
                achievement(8, "SELF_DEV_START", "자기계발 시작", "자기계발 카테고리 루틴을 처음 만들어보세요", 0, 1, badgeYn = true),
                achievement(9, "ORGANIZE_START", "정리 시작", "정리 카테고리 루틴을 처음 만들어보세요", 0, 1, badgeYn = true),
                achievement(10, "FIRST_REPORT", "첫 리포트 확인", "리포트를 처음 확인해보세요", 1, 1, badgeYn = true, achieved = true),
                achievement(11, "EXERCISE_START", "운동 시작", "운동 카테고리 루틴을 처음 만들어보세요", 1, 1, badgeYn = true, achieved = true),
            ),
        ),
        AchievementCategoryGroup(
            category = AchievementCategory.EPIC,
            achievements = listOf(
                achievement(12, "LIKE_10", "좋아요 10회", "좋아요를 10회 받아보세요", 0, 10, topazReward = 50, badgeYn = true),
                achievement(13, "POKE_5", "쿡쿡 5회", "쿡쿡 찌르기를 5회 보내보세요", 0, 5, badgeYn = true),
                achievement(14, "ROOM_JOIN_2", "방 참여 2회", "모임방에 2회 참여해보세요", 2, 2, badgeYn = true, achieved = true),
                achievement(15, "VERIFY_10", "인증 10회", "루틴 인증을 10회 완료하세요", 0, 10, badgeYn = true),
            ),
        ),
        AchievementCategoryGroup(
            category = AchievementCategory.UNIQUE,
            achievements = listOf(
                achievement(16, "LIKE_30", "좋아요 30회", "좋아요를 30회 받아보세요", 0, 30, badgeYn = true),
                achievement(17, "POKE_10", "쿡쿡 10회", "쿡쿡 찌르기를 10회 보내보세요", 0, 10, badgeYn = true),
                achievement(18, "VERIFY_30", "인증 30회", "루틴 인증을 30회 완료하세요", 0, 30, badgeYn = true),
            ),
        ),
    )

    private fun achievement(
        id: Long,
        code: String,
        name: String,
        conditionDesc: String,
        progressCurrent: Int,
        progressTarget: Int,
        topazReward: Int = 0,
        badgeYn: Boolean = false,
        achieved: Boolean = false,
    ) = Achievement(
        achievementId = id,
        code = code,
        name = name,
        conditionDesc = conditionDesc,
        status = if (progressCurrent >= progressTarget) "COMPLETED" else "IN_PROGRESS",
        progressCurrent = progressCurrent,
        progressTarget = progressTarget,
        topazReward = topazReward,
        badgeYn = badgeYn,
        limitedOutfitYn = false,
        achievedAt = if (achieved) "2026-08-01T00:00:00" else null,
        claimedAt = null,
    )
}
