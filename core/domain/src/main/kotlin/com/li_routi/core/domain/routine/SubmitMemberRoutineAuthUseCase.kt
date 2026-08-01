package com.li_routi.core.domain.routine

import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.domain.media.MediaPurpose
import com.li_routi.core.domain.media.UploadMediaUseCase

/**
 * 개인 루틴 인증 전체 플로우.
 * 1) purpose=MEMBER_ROUTINE_VERIFICATION 으로 사진 업로드 → mediaKey
 * 2) 선택된 각 routineId에 대해 인증 API 호출 (같은 mediaKey 재사용)
 */
class SubmitMemberRoutineAuthUseCase(
    private val uploadMediaUseCase: UploadMediaUseCase,
    private val verifyMemberRoutineUseCase: VerifyMemberRoutineUseCase,
) {
    suspend operator fun invoke(
        contentType: String,
        bytes: ByteArray,
        content: String?,
        routineIds: List<Long>,
    ): ResultState<Unit> {
        if (routineIds.isEmpty()) {
            return ResultState.Error("인증할 개인 루틴을 선택해 주세요.")
        }
        val mediaKey = when (
            val uploaded = uploadMediaUseCase(
                purpose = MediaPurpose.MEMBER_ROUTINE_VERIFICATION,
                contentType = contentType,
                bytes = bytes,
            )
        ) {
            is ResultState.Success -> uploaded.data
            is ResultState.Error -> return uploaded
            ResultState.Loading -> return ResultState.Loading
        }
        for (routineId in routineIds) {
            when (
                val verified = verifyMemberRoutineUseCase(
                    routineId = routineId,
                    mediaKey = mediaKey,
                    content = content,
                )
            ) {
                is ResultState.Success -> Unit
                is ResultState.Error -> return verified
                ResultState.Loading -> return ResultState.Loading
            }
        }
        return ResultState.Success(Unit)
    }
}
