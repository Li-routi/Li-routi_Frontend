package com.li_routi.core.data.network.dto.response

/** GET /api/groups/{groupId} 응답 result. */
data class GroupDetailResponse(
    val groupId: Long,
    val groupName: String,
    val inviteCode: String,
    val members: List<GroupMemberActivityResponse>,
)

data class GroupMemberActivityResponse(
    val memberId: Long,
    val name: String,
    val profileImageKey: String?,
    val statusMessage: String?,
    val currentStreak: Int,
    val totalLikeCount: Long,
    val dailyProgress: DailyProgressResponse?,
)

data class DailyProgressResponse(
    val completedCount: Long,
    val totalCount: Long,
)
