package com.li_routi.core.domain.grouproutine

import com.li_routi.core.common.kotlin.util.ResultState

class GetGroupDetailUseCase(
    private val repository: GroupRoutineRepository,
) {
    suspend operator fun invoke(groupId: Long): ResultState<GroupDetail> =
        repository.getGroupDetail(groupId)
}
