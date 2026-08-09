package com.li_routi.core.domain.grouproutine

import com.li_routi.core.common.kotlin.util.ResultState

class JoinGroupUseCase(
    private val repository: GroupRoutineRepository,
) {
    suspend operator fun invoke(inviteCode: String): ResultState<GroupJoinResult> =
        repository.joinGroup(inviteCode)
}
