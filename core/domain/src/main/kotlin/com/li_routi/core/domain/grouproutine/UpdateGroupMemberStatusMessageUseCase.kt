package com.li_routi.core.domain.grouproutine

import com.li_routi.core.common.kotlin.util.ResultState

class UpdateGroupMemberStatusMessageUseCase(
    private val repository: GroupRoutineRepository,
) {
    /** @return 서버에 저장된 최종 상태 메시지 */
    suspend operator fun invoke(groupId: Long, statusMessage: String): ResultState<String> =
        repository.updateMyStatusMessage(groupId, statusMessage)
}
