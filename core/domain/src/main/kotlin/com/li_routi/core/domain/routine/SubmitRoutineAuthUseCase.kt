package com.li_routi.core.domain.routine

import com.li_routi.core.common.kotlin.util.ResultState

/**
 * 개인·그룹 루틴 인증을 한 번에 처리한다.
 *
 * purpose가 다르므로 개인/그룹 각각 미디어를 한 번씩 올린다(같은 사진 바이트).
 */
class SubmitRoutineAuthUseCase(
    private val submitMemberRoutineAuthUseCase: SubmitMemberRoutineAuthUseCase,
    private val submitGroupRoutineAuthUseCase: SubmitGroupRoutineAuthUseCase,
) {
    suspend operator fun invoke(
        contentType: String,
        bytes: ByteArray,
        content: String?,
        memberRoutineIds: List<Long>,
        groupTargets: List<GroupRoutineTarget>,
    ): ResultState<Unit> {
        if (memberRoutineIds.isEmpty() && groupTargets.isEmpty()) {
            return ResultState.Error("인증할 루틴을 선택해 주세요.")
        }
        if (memberRoutineIds.isNotEmpty()) {
            when (
                val member = submitMemberRoutineAuthUseCase(
                    contentType = contentType,
                    bytes = bytes,
                    content = content,
                    routineIds = memberRoutineIds,
                )
            ) {
                is ResultState.Success -> Unit
                is ResultState.Error -> return member
                ResultState.Loading -> return ResultState.Loading
            }
        }
        if (groupTargets.isNotEmpty()) {
            when (
                val group = submitGroupRoutineAuthUseCase(
                    contentType = contentType,
                    bytes = bytes,
                    content = content,
                    targets = groupTargets,
                )
            ) {
                is ResultState.Success -> Unit
                is ResultState.Error -> return group
                ResultState.Loading -> return ResultState.Loading
            }
        }
        return ResultState.Success(Unit)
    }
}
