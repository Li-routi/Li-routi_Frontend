package com.li_routi.core.domain.grouproutine

import com.li_routi.core.domain.shop.AvatarLayer

/** 초대코드로 가입한 결과 */
data class GroupJoinResult(
    val groupId: Long,
    val name: String,
    val memberStatus: String,
)

/** 가입 전에 보여주는 그룹 미리보기. [joinable]이 false면 [unavailableReason]에 사유가 담김 */
data class GroupJoinPreview(
    val groupId: Long,
    val name: String,
    val activeMemberCount: Int,
    val maxMemberCount: Int,
    val totalRoutineCount: Int,
    /** 식별 정보 없이 구성원별 현재 조합 아바타 레이어만 담김(받은 순서대로 겹쳐 그리면 됨). */
    val memberAvatars: List<List<AvatarLayer>> = emptyList(),
    val joinable: Boolean,
    val unavailableReason: String?,
)
