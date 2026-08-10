package com.li_routi.core.domain.grouproutine

/** 그룹방 상세 정보. ACTIVE 구성원만 조회할 수 있음 */
data class GroupDetail(
    val groupId: Long,
    val groupName: String,
    val inviteCode: String,
    val members: List<GroupMemberActivity>,
)

/** 그룹 구성원 한 명의 활동 현황 */
data class GroupMemberActivity(
    val memberId: Long,
    val name: String,
    val profileImageKey: String?,
    val statusMessage: String?,
    val currentStreak: Int,
    val totalLikeCount: Long,
    /** 금일 완료한 그룹 루틴 할당 수. 할당이 없으면 0 */
    val completedCount: Long,
    /** 금일 전체 그룹 루틴 할당 수. 할당이 없으면 0 */
    val totalCount: Long,
)
