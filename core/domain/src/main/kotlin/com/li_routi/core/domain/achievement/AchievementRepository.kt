package com.li_routi.core.domain.achievement

import com.li_routi.core.common.kotlin.util.ResultState

interface AchievementRepository {

    /** 마이 > 업적 화면에 필요한 전체 업적 목록을 카테고리별로 묶어 조회한다. */
    suspend fun getAchievements(): ResultState<List<AchievementCategoryGroup>>

    /** 달성한 업적의 보상(토파즈/한정 의상 등)을 수령한다. */
    suspend fun claimAchievement(achievementId: Long): ResultState<AchievementClaimResult>

    /** "파도타기"(연속 기록) 업적이 추적 중인 루틴과 현재/목표 연속 기록을 조회한다. */
    suspend fun getWaveRoutineStatus(): ResultState<WaveRoutineStatus>

    /** 연속 기록을 추적할 개인 루틴을 지정(변경)한다. */
    suspend fun selectWaveRoutine(memberRoutineId: Long): ResultState<Unit>

    /** 대표 업적으로 선택 가능한(배지 이미지가 있고 CLAIMED된) 업적 목록을 조회한다. */
    suspend fun getSelectableRepresentativeAchievements(): ResultState<List<SelectableAchievement>>

    /** [achievementId]를 대표 업적으로 설정한다 — 이미 설정된 게 있으면 덮어쓴다. */
    suspend fun setRepresentativeAchievement(achievementId: Long): ResultState<Unit>

    /** 설정된 대표 업적을 해제한다. */
    suspend fun clearRepresentativeAchievement(): ResultState<Unit>
}
