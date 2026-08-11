package com.li_routi.core.data.network.dto.response

data class ParticipatingGroupListResponse(
    val groups: List<ParticipatingGroupResponse>,
)
data class ParticipatingGroupResponse(
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

