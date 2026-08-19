package com.li_routi.core.domain.grouproutine

import com.li_routi.core.domain.shop.AvatarLayer

/** 그룹방 상세 정보. ACTIVE 구성원만 조회할 수 있음 */
data class GroupDetail(
    val groupId: Long,
    val groupName: String,
    val inviteCode: String,
    val isCurrentUserOwner: Boolean,
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
    val totalPokeCount: Long,
    /** 금일 완료한 그룹 루틴 할당 수. 할당이 없으면 0 */
    val completedCount: Long,
    /** 금일 전체 그룹 루틴 할당 수. 할당이 없으면 0 */
    val totalCount: Long,
    /**
     * 이 구성원의 아바타를 겹쳐 그릴 레이어. 캐릭터·둥지까지 포함해 받은 순서대로 그리면 됨.
     * 캐릭터를 하나도 못 열었으면 `CHARACTER`·둥지 레이어가 빠져서 옴 — 그때만 기본 캐릭터로 대체함
     */
    val layers: List<AvatarLayer> = emptyList(),
)
