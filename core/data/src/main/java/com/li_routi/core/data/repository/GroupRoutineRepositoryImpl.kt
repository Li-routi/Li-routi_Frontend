package com.li_routi.core.data.repository

import com.li_routi.core.common.kotlin.util.ApiException
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.common.kotlin.util.safeApiCall
import com.li_routi.core.data.mapper.toDomain
import com.li_routi.core.data.network.dto.request.CreateGroupCategoryRequest
import com.li_routi.core.data.network.dto.request.CreateGroupRequest
import com.li_routi.core.data.network.dto.request.CreateGroupRoutineCategoryRequest
import com.li_routi.core.data.network.dto.request.CreateGroupRoutineRequest
import com.li_routi.core.data.network.dto.request.GroupRoutineScheduleRequest
import com.li_routi.core.data.network.dto.request.UpdateGroupRoutineRequest
import com.li_routi.core.data.network.dto.response.ApiResponse
import com.li_routi.core.data.network.service.GroupRoutineApiService
import com.li_routi.core.domain.grouproutine.CreatedGroup
import com.li_routi.core.domain.grouproutine.GroupInviteCode
import com.li_routi.core.domain.grouproutine.GroupRoutineCategory
import com.li_routi.core.domain.grouproutine.GroupRoutineCategoryList
import com.li_routi.core.domain.grouproutine.GroupRoutineRepository
import com.li_routi.core.domain.grouproutine.GroupRoutineSchedule
import com.li_routi.core.domain.grouproutine.GroupRoutineUpdateResult
import com.li_routi.core.domain.grouproutine.GroupRoutineVerificationFeed
import com.li_routi.core.domain.grouproutine.NewGroupCategory
import com.li_routi.core.domain.grouproutine.NewGroupRoutine
import com.li_routi.core.domain.grouproutine.TodayGroupRoutine

class GroupRoutineRepositoryImpl(
    private val api: GroupRoutineApiService,
) : GroupRoutineRepository {

    override suspend fun createGroup(
        name: String,
        customCategories: List<NewGroupCategory>,
        routines: List<NewGroupRoutine>,
    ): ResultState<CreatedGroup> = safeApiCall {
        api.createGroup(
            CreateGroupRequest(
                name = name,
                customCategories = customCategories.map {
                    CreateGroupCategoryRequest(clientKey = it.clientKey, name = it.name, color = it.color)
                },
                routines = routines.map { routine ->
                    CreateGroupRoutineRequest(
                        categoryId = routine.categoryId,
                        categoryKey = routine.categoryKey,
                        title = routine.title,
                        description = routine.description,
                        schedules = routine.schedules.map {
                            GroupRoutineScheduleRequest(
                                repeatDay = it.repeatDay.name,
                                startTime = it.startTime,
                                endTime = it.endTime,
                            )
                        },
                    )
                },
            ),
        ).unwrap().toDomain()
    }

    // 실서버로 확인해보니 생성/수정 요청 모양이 똑같음(스웨거 문서랑 다름)
    override suspend fun createGroupRoutine(
        groupId: Long,
        categoryId: Long,
        title: String,
        description: String,
        schedules: List<GroupRoutineSchedule>,
    ): ResultState<GroupRoutineUpdateResult> = safeApiCall {
        api.createRoutine(
            groupId = groupId,
            request = buildRequest(categoryId, title, description, schedules),
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
            request = buildRequest(categoryId, title, description, schedules),
        ).unwrap().toDomain()
    }

    private fun buildRequest(
        categoryId: Long,
        title: String,
        description: String,
        schedules: List<GroupRoutineSchedule>,
    ) = UpdateGroupRoutineRequest(
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
    )

    override suspend fun getTodayGroupRoutines(): ResultState<List<TodayGroupRoutine>> = safeApiCall {
        api.getTodayRoutines().unwrap().toDomain()
    }

    override suspend fun issueInviteCode(groupId: Long): ResultState<GroupInviteCode> = safeApiCall {
        api.issueInviteCode(groupId).unwrap().toDomain()
    }

    override suspend fun getInviteCode(groupId: Long): ResultState<GroupInviteCode> = safeApiCall {
        api.getInviteCode(groupId).unwrap().toDomain()
    }

    override suspend fun getGroupRoutineCategories(groupId: Long): ResultState<GroupRoutineCategoryList> = safeApiCall {
        api.getCategories(groupId).unwrap().toDomain()
    }

    override suspend fun createGroupRoutineCategory(
        groupId: Long,
        name: String,
        color: String?,
    ): ResultState<GroupRoutineCategory> = safeApiCall {
        api.createCategory(
            groupId = groupId,
            request = CreateGroupRoutineCategoryRequest(
                name = name,
                color = color,
            ),
        ).unwrap().toDomain()
    }

    override suspend fun getGroupRoutineVerifications(
        groupId: Long,
        routineId: Long,
        cursor: Long?,
        size: Int?,
    ): ResultState<GroupRoutineVerificationFeed> = safeApiCall {
        api.getRoutineVerifications(
            groupId = groupId,
            routineId = routineId,
            cursor = cursor,
            size = size,
        ).unwrap().toDomain()
    }
}

private fun <T> ApiResponse<T>.unwrap(): T {
    val result = result
    if (!isSuccess || result == null) throw ApiException(message)
    return result
}
