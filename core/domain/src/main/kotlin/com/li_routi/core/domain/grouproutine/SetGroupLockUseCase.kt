package com.li_routi.core.domain.grouproutine

import com.li_routi.core.common.kotlin.util.ResultState

class SetGroupLockUseCase(
    private val repository: GroupRoutineRepository,
) {
    /** @return 잠금 처리 후 서버가 알려준 최종 잠금 상태 */
    suspend operator fun invoke(groupId: Long, locked: Boolean): ResultState<Boolean> =
        repository.setGroupLock(groupId, locked)
}
