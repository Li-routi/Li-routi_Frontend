package com.li_routi.core.domain.grouproutine

import com.li_routi.core.common.kotlin.util.ResultState

class GetGroupJoinPreviewUseCase(
    private val repository: GroupRoutineRepository,
) {
    suspend operator fun invoke(inviteCode: String): ResultState<GroupJoinPreview> =
        repository.getGroupJoinPreview(inviteCode)
}
