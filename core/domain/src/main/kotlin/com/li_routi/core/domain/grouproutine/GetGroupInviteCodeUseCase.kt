package com.li_routi.core.domain.grouproutine

import com.li_routi.core.common.kotlin.util.ResultState

class GetGroupInviteCodeUseCase(
    private val repository: GroupRoutineRepository,
) {
    suspend operator fun invoke(groupId: Long): ResultState<GroupInviteCode> =
        repository.getInviteCode(groupId)
}
