package com.li_routi.core.data.repository

import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.mapper.toDomain
import com.li_routi.core.data.network.apiCall
import com.li_routi.core.data.network.dto.request.RoutineVerificationRequest
import com.li_routi.core.data.network.safeDataApiCall
import com.li_routi.core.data.network.service.RoutineApiService
import com.li_routi.core.domain.routine.GroupRoutineVerification
import com.li_routi.core.domain.routine.MemberRoutineVerification
import com.li_routi.core.domain.routine.RoutineVerificationRepository

class RoutineVerificationRepositoryImpl(
    private val api: RoutineApiService,
) : RoutineVerificationRepository {

    override suspend fun verifyMemberRoutine(
        routineId: Long,
        mediaKey: String,
        content: String?,
    ): ResultState<MemberRoutineVerification> = safeDataApiCall {
        apiCall {
            api.verifyMemberRoutine(
                routineId = routineId,
                body = RoutineVerificationRequest(
                    mediaKey = mediaKey,
                    content = content?.takeIf { it.isNotBlank() },
                ),
            )
        }.toDomain()
    }

    override suspend fun verifyGroupRoutine(
        groupId: Long,
        routineId: Long,
        mediaKey: String,
        content: String?,
    ): ResultState<GroupRoutineVerification> = safeDataApiCall {
        apiCall {
            api.verifyGroupRoutine(
                groupId = groupId,
                routineId = routineId,
                body = RoutineVerificationRequest(
                    mediaKey = mediaKey,
                    content = content?.takeIf { it.isNotBlank() },
                ),
            )
        }.toDomain()
    }

    override suspend fun reverifyGroupRoutine(
        groupId: Long,
        routineId: Long,
        verificationId: Long,
        mediaKey: String,
        content: String?,
    ): ResultState<GroupRoutineVerification> = safeDataApiCall {
        apiCall {
            api.reverifyGroupRoutine(
                groupId = groupId,
                routineId = routineId,
                verificationId = verificationId,
                body = RoutineVerificationRequest(
                    mediaKey = mediaKey,
                    content = content?.takeIf { it.isNotBlank() },
                ),
            )
        }.toDomain()
    }
}
