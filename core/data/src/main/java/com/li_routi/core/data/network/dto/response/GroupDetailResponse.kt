package com.li_routi.core.data.network.dto.response

/** GET /api/groups/{groupId} 응답 result. */
// Gson은 Kotlin non-null을 강제하지 않고 없는 필드에 null을 넣어버림 —
// 서버가 필드를 빼면 그대로 크래시라 문자열은 전부 nullable로 받음
data class GroupDetailResponse(
    val groupId: Long,
    val groupName: String?,
    val inviteCode: String?,
    val myRole: String?,
    val members: List<GroupMemberActivityResponse>?,
)

data class GroupMemberActivityResponse(
    val memberId: Long,
    val name: String?,
    val profileImageKey: String?,
    val statusMessage: String?,
    val currentStreak: Int,
    val totalLikeCount: Long,
    val totalPokeCount: Long,
    val dailyProgress: DailyProgressResponse?,
    /** 구성원의 현재 조합 아바타(착용 아이템). 안 입은 자리는 실리지 않는다. */
    val avatar: GroupMemberAvatarResponse?,
    /** 구성원이 대표로 설정한 업적 배지. 대표 업적이 없으면 null. */
    val representativeAchievement: RepresentativeAchievementResponse?,
)

data class RepresentativeAchievementResponse(
    val name: String?,
    val badgeImageUrl: String?,
)

data class DailyProgressResponse(
    val completedCount: Long,
    val totalCount: Long,
)

data class GroupMemberAvatarResponse(
    val equipped: List<GroupMemberAvatarEquippedItemResponse>?,
)

data class GroupMemberAvatarEquippedItemResponse(
    val slot: String?,
    val imageUrl: String?,
)
