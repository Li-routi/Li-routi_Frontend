package com.li_routi.core.domain.grouproutine

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
    /** 현재 착용 중인 아이템 이미지. 겹칠 순서대로(BODY→HEAD→HAND) 옴. 캐릭터 본체(어떤 동물/색)는
     * 서버에 없어(로컬 전용 선택) 포함되지 않는다 — 기본 캐릭터 위에 이 이미지들만 겹쳐 그린다. */
    val equippedImageUrls: List<String> = emptyList(),
)
