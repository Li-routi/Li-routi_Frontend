package com.li_routi.core.domain.grouproutine

import com.li_routi.core.common.kotlin.util.ResultState

class GetParticipatingGroupsUseCase(
    private val repository: GroupRoutineRepository,
) {
    suspend operator fun invoke(): ResultState<List<ParticipatingGroup>> =
        repository.getParticipatingGroups()
}
