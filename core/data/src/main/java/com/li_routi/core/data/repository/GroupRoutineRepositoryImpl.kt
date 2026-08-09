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
import com.li_routi.core.data.network.dto.request.JoinGroupRequest
import com.li_routi.core.data.network.dto.request.TransferGroupOwnerRequest
import com.li_routi.core.data.network.dto.request.UpdateGroupNameRequest
import com.li_routi.core.data.network.dto.request.UpdateStatusMessageRequest
import com.li_routi.core.data.network.dto.request.UpdateGroupRoutineRequest
import com.li_routi.core.data.network.dto.response.ApiResponse
import com.li_routi.core.data.network.service.GroupRoutineApiService
import com.li_routi.core.domain.grouproutine.CreatedGroup
import com.li_routi.core.domain.grouproutine.GroupDetail
import com.li_routi.core.domain.grouproutine.GroupInviteCode
import com.li_routi.core.domain.grouproutine.GroupJoinPreview
import com.li_routi.core.domain.grouproutine.GroupJoinResult
import com.li_routi.core.domain.grouproutine.GroupRoutineCategory
import com.li_routi.core.domain.grouproutine.GroupRoutineCategoryList
import com.li_routi.core.domain.grouproutine.GroupRoutineRepository
import com.li_routi.core.domain.grouproutine.GroupRoutineSchedule
import com.li_routi.core.domain.grouproutine.GroupRoutineUpdateResult
import com.li_routi.core.domain.grouproutine.GroupRoutineVerificationFeed
import com.li_routi.core.domain.grouproutine.LeaveGroupResult
import com.li_routi.core.domain.grouproutine.NewGroupCategory
import com.li_routi.core.domain.grouproutine.NewGroupRoutine
import com.li_routi.core.domain.grouproutine.TodayGroupRoutine
import retrofit2.HttpException

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

    override suspend fun getGroupDetail(groupId: Long): ResultState<GroupDetail> = safeApiCall {
        api.getGroupDetail(groupId).unwrap().toDomain()
    }

    override suspend fun deleteGroup(groupId: Long): ResultState<Unit> = safeApiCall {
        api.deleteGroup(groupId).ensureSuccess()
    }

    override suspend fun leaveGroup(groupId: Long): ResultState<LeaveGroupResult> = safeApiCall {
        try {
            api.leaveGroup(groupId).ensureSuccess()
            LeaveGroupResult.Left
        } catch (e: HttpException) {
            // 409는 OWNER라 못 나가는 경우뿐임(GROUP409_1) — 에러 대신 결과로 돌려줘서 삭제로 유도함
            if (e.code() == 409) LeaveGroupResult.OwnerMustDelete else throw e
        }
    }

    override suspend fun joinGroup(inviteCode: String): ResultState<GroupJoinResult> = safeApiCall {
        api.joinGroup(JoinGroupRequest(inviteCode = inviteCode)).unwrap().toDomain()
    }

    override suspend fun getGroupJoinPreview(inviteCode: String): ResultState<GroupJoinPreview> = safeApiCall {
        api.getJoinPreview(inviteCode).unwrap().toDomain()
    }

    override suspend fun setGroupLock(groupId: Long, locked: Boolean): ResultState<Boolean> = safeApiCall {
        val response = if (locked) api.lockGroup(groupId) else api.unlockGroup(groupId)
        response.unwrap().isLocked
    }

    override suspend fun updateMyStatusMessage(groupId: Long, statusMessage: String): ResultState<String> = safeApiCall {
        api.updateStatusMessage(
            groupId = groupId,
            request = UpdateStatusMessageRequest(statusMessage = statusMessage),
        ).unwrap().statusMessage
    }

    override suspend fun updateGroupName(groupId: Long, name: String): ResultState<Unit> = safeApiCall {
        api.updateGroupName(groupId = groupId, request = UpdateGroupNameRequest(name = name)).ensureSuccess()
    }

    override suspend fun transferGroupOwner(groupId: Long, targetMemberId: Long): ResultState<Unit> = safeApiCall {
        api.transferOwner(
            groupId = groupId,
            request = TransferGroupOwnerRequest(targetMemberId = targetMemberId),
        ).ensureSuccess()
    }

    override suspend fun kickGroupMember(groupId: Long, targetMemberId: Long): ResultState<Unit> = safeApiCall {
        api.kickMember(groupId = groupId, targetMemberId = targetMemberId).ensureSuccess()
    }

    override suspend fun deleteGroupRoutine(groupId: Long, routineId: Long): ResultState<Unit> = safeApiCall {
        api.deleteRoutine(groupId = groupId, routineId = routineId).ensureSuccess()
    }

    override suspend fun getTodayGroupRoutines(): ResultState<List<TodayGroupRoutine>> = safeApiCall {
        api.getTodayRoutines().unwrap().toDomain()
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

/** result가 비어 오는(Void) 응답용 — 성공 여부만 확인함 */
private fun ApiResponse<*>.ensureSuccess() {
    if (!isSuccess) throw ApiException(message)
}
