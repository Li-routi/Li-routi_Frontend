package com.li_routi.core.data.network.dto.response

/** GET /api/groups/{groupId} 응답 result. */
// Gson은 Kotlin non-null을 강제하지 않고 없는 필드에 null을 넣어버림 —
// 서버가 필드를 빼면 그대로 크래시라 문자열은 전부 nullable로 받음
data class GroupDetailResponse(
    val groupId: Long,
    val groupName: String?,
    val inviteCode: String?,
    val members: List<GroupMemberActivityResponse>?,
)

data class GroupMemberActivityResponse(
    val memberId: Long,
    val name: String?,
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
