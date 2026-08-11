package com.li_routi.core.domain.grouproutine

import com.li_routi.core.common.kotlin.util.ResultState

class PokeGroupMemberUseCase(
    private val repository: GroupRoutineRepository,
) {
    suspend operator fun invoke(groupId: Long, targetMemberId: Long): ResultState<Unit> =
        repository.pokeGroupMember(groupId, targetMemberId)
}
