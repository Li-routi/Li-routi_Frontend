package com.li_routi.core.domain.grouproutine

data class ParticipatingGroup(
    val groupId: Long,
    val groupName: String,
    val activeMemberCount: Int,
    val activeRoutineCount: Int,
    val todayAssignedRoutineCount: Int,
    val todayCompletedRoutineCount: Int,
    val currentStreak: Int,
    val monthlyAchievementRate: Int,
    val todayGroupVerificationCount: Int,
)
