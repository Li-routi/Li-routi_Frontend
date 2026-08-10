package com.li_routi.core.domain.grouproutine

import com.li_routi.core.common.kotlin.util.ResultState

class KickGroupMemberUseCase(
    private val repository: GroupRoutineRepository,
) {
    suspend operator fun invoke(groupId: Long, targetMemberId: Long): ResultState<Unit> =
        repository.kickGroupMember(groupId, targetMemberId)
}
