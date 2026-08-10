package com.li_routi.core.domain.grouproutine

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
    val joinable: Boolean,
    val unavailableReason: String?,
)
