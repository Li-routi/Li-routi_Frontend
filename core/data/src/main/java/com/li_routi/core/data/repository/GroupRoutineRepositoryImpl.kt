package com.li_routi.core.data.repository

import com.li_routi.core.common.kotlin.util.ApiException
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.common.kotlin.util.safeApiCall
import com.li_routi.core.data.mapper.toDomain
import com.li_routi.core.data.network.dto.request.CreateGroupRoutineRequest
import com.li_routi.core.data.network.dto.request.GroupRoutineScheduleRequest
import com.li_routi.core.data.network.dto.request.UpdateGroupRoutineRequest
import com.li_routi.core.data.network.dto.response.ApiResponse
import com.li_routi.core.data.network.service.GroupRoutineApiService
import com.li_routi.core.domain.grouproutine.GroupInviteCode
import com.li_routi.core.domain.grouproutine.GroupRoutineCreateResult
import com.li_routi.core.domain.grouproutine.GroupRoutineRepository
import com.li_routi.core.domain.grouproutine.GroupRoutineSchedule
import com.li_routi.core.domain.grouproutine.GroupRoutineUpdateResult
import com.li_routi.core.domain.grouproutine.RepeatDay
import com.li_routi.core.domain.grouproutine.TodayGroupRoutine

class GroupRoutineRepositoryImpl(
    private val api: GroupRoutineApiService,
) : GroupRoutineRepository {

    override suspend fun createGroupRoutine(
        groupId: Long,
        categoryId: Long,
        templateId: Long?,
        name: String,
        endTime: String?,
        repeatDays: List<RepeatDay>?,
        alarmTime: String?,
    ): ResultState<GroupRoutineCreateResult> = safeApiCall {
        api.createRoutine(
            groupId = groupId,
            request = CreateGroupRoutineRequest(
                categoryId = categoryId,
                templateId = templateId,
                name = name,
                endTime = endTime,
                repeatDays = repeatDays?.map { it.name },
                alarmTime = alarmTime,
            ),
        ).unwrap().toDomain()
    }

    override suspend fun updateGroupRoutine(
        groupId: Long,
        routineId: Long,
        categoryId: Long,
        title: String,
        description: String,
        schedules: List<GroupRoutineSchedule>,
    ): ResultState<GroupRoutineUpdateResult> = safeApiCall {
        api.updateRoutine(
            groupId = groupId,
            routineId = routineId,
            request = UpdateGroupRoutineRequest(
                categoryId = categoryId,
                title = title,
                description = description,
                schedules = schedules.map {
                    GroupRoutineScheduleRequest(
                        repeatDay = it.repeatDay.name,
                        startTime = it.startTime,
                        endTime = it.endTime,
                    )
                },
            ),
        ).unwrap().toDomain()
    }

    override suspend fun getTodayGroupRoutines(): ResultState<List<TodayGroupRoutine>> = safeApiCall {
        api.getTodayRoutines().unwrap().toDomain()
    }

    override suspend fun issueInviteCode(groupId: Long): ResultState<GroupInviteCode> = safeApiCall {
        api.issueInviteCode(groupId).unwrap().toDomain()
    }

    override suspend fun getInviteCode(groupId: Long): ResultState<GroupInviteCode> = safeApiCall {
        api.getInviteCode(groupId).unwrap().toDomain()
    }
}

private fun <T> ApiResponse<T>.unwrap(): T {
    val result = result
    if (!isSuccess || result == null) throw ApiException(message)
    return result
}
