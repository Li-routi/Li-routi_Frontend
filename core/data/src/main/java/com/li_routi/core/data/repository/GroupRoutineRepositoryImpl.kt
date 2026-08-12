package com.li_routi.core.data.repository

import com.li_routi.core.common.kotlin.util.ApiException
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.common.kotlin.util.safeApiCall
import com.li_routi.core.data.mapper.toDomain
import com.li_routi.core.data.network.dto.request.CreateGroupCategoryRequest
import com.li_routi.core.data.network.dto.request.CreateGroupRequest
import com.li_routi.core.data.network.dto.request.CreateGroupRoutineCategoryRequest
import com.li_routi.core.data.network.dto.request.CreateGroupRoutineRequest
import com.li_routi.core.data.network.dto.request.GroupRoutineVerificationReadRequest
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
import com.li_routi.core.domain.grouproutine.GroupRoutineDisappointment
import com.li_routi.core.domain.grouproutine.GroupRoutineLike
import com.li_routi.core.domain.grouproutine.GroupRoutineCategory
import com.li_routi.core.domain.grouproutine.GroupRoutineCategoryList
import com.li_routi.core.domain.grouproutine.GroupRoutineRepository
import com.li_routi.core.domain.grouproutine.GroupRoutineSchedule
import com.li_routi.core.domain.grouproutine.GroupRoutineUpdateResult
import com.li_routi.core.domain.grouproutine.GroupRoutineVerificationFeed
import com.li_routi.core.domain.grouproutine.GroupRoutineVerificationRead
import com.li_routi.core.domain.grouproutine.LeaveGroupResult
import com.li_routi.core.domain.grouproutine.NewGroupCategory
import com.li_routi.core.domain.grouproutine.NewGroupRoutine
import com.li_routi.core.domain.grouproutine.ParticipatingGroup
import com.li_routi.core.domain.grouproutine.TodayGroupRoutine
import com.li_routi.core.domain.grouproutine.UnreadGroupRoutineVerificationFeed
import com.google.gson.Gson
import com.google.gson.JsonObject
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
    override suspend fun getParticipatingGroups(): ResultState<List<ParticipatingGroup>> = safeApiCall {
        api.getParticipatingGroups().unwrap().toDomain()
    }

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
            // OWNER라 못 나가는 경우(GROUP409_1)만 결과로 바꿔서 삭제로 유도함.
            // 409를 상태코드만 보고 판단하면 나중에 다른 사유가 409로 묶였을 때 엉뚱하게 삭제를 권하게 됨
            if (e.errorCode() == OwnerCannotLeaveCode) LeaveGroupResult.OwnerMustDelete else throw e
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
        ).unwrap().statusMessage.orEmpty()
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

    override suspend fun pokeGroupMember(groupId: Long, targetMemberId: Long): ResultState<Unit> = safeApiCall {
        api.pokeMember(groupId = groupId, targetMemberId = targetMemberId).ensureSuccess()
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

    override suspend fun getUnreadGroupRoutineVerifications(
        groupId: Long,
        cursor: Long?,
        size: Int?,
    ): ResultState<UnreadGroupRoutineVerificationFeed> = safeApiCall {
        api.getUnreadRoutineVerifications(
            groupId = groupId,
            cursor = cursor,
            size = size,
        ).unwrap().toDomain()
    }

    override suspend fun markGroupRoutineVerificationsRead(
        groupId: Long,
        lastReadVerificationId: Long,
    ): ResultState<GroupRoutineVerificationRead> = safeApiCall {
        api.markRoutineVerificationsRead(
            groupId = groupId,
            request = GroupRoutineVerificationReadRequest(lastReadVerificationId = lastReadVerificationId),
        ).unwrap().toDomain()
    }

    override suspend fun likeGroupRoutineVerification(
        groupId: Long,
        verificationId: Long,
    ): ResultState<GroupRoutineLike> = safeApiCall {
        api.likeRoutineVerification(groupId = groupId, verificationId = verificationId).unwrap().toDomain()
    }

    override suspend fun unlikeGroupRoutineVerification(
        groupId: Long,
        verificationId: Long,
    ): ResultState<GroupRoutineLike> = safeApiCall {
        api.unlikeRoutineVerification(groupId = groupId, verificationId = verificationId).unwrap().toDomain()
    }

    override suspend fun disappointGroupRoutineVerification(
        groupId: Long,
        verificationId: Long,
    ): ResultState<GroupRoutineDisappointment> = safeApiCall {
        api.disappointRoutineVerification(groupId = groupId, verificationId = verificationId).unwrap().toDomain()
    }

    override suspend fun undisappointGroupRoutineVerification(
        groupId: Long,
        verificationId: Long,
    ): ResultState<GroupRoutineDisappointment> = safeApiCall {
        api.undisappointRoutineVerification(groupId = groupId, verificationId = verificationId).unwrap().toDomain()
    }
}

/** OWNER는 그룹을 나갈 수 없음 */
private const val OwnerCannotLeaveCode = "GROUP409_1"

/** 에러 응답 바디에서 서버가 준 code를 꺼냄. 못 읽으면 null */
private fun HttpException.errorCode(): String? = runCatching {
    response()?.errorBody()?.string()?.let { body ->
        Gson().fromJson(body, JsonObject::class.java)?.get("code")?.asString
    }
}.getOrNull()

private fun <T> ApiResponse<T>.unwrap(): T {
    val result = result
    if (!isSuccess || result == null) throw ApiException(message)
    return result
}

/** result가 비어 오는(Void) 응답용 — 성공 여부만 확인함 */
private fun ApiResponse<*>.ensureSuccess() {
    if (!isSuccess) throw ApiException(message)
}
