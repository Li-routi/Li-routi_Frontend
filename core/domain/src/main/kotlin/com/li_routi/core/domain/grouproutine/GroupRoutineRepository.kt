package com.li_routi.core.domain.grouproutine

import com.li_routi.core.common.kotlin.util.ResultState

interface GroupRoutineRepository {

    /** 모임방과 초기 그룹 루틴을 함께 생성함 */
    suspend fun createGroup(
        name: String,
        customCategories: List<NewGroupCategory>,
        routines: List<NewGroupRoutine>,
    ): ResultState<CreatedGroup>

    /** 그룹에 루틴을 등록함. 요청/응답 모양이 [updateGroupRoutine]과 동일함(실서버 확인, 스웨거 문서와 다름) */
    suspend fun createGroupRoutine(
        groupId: Long,
        categoryId: Long,
        title: String,
        description: String,
        schedules: List<GroupRoutineSchedule>,
    ): ResultState<GroupRoutineUpdateResult>

    /** 그룹 루틴을 수정함 */
    suspend fun updateGroupRoutine(
        groupId: Long,
        routineId: Long,
        categoryId: Long,
        title: String,
        description: String,
        schedules: List<GroupRoutineSchedule>,
    ): ResultState<GroupRoutineUpdateResult>

    /** 그룹방 상세(그룹명/초대코드/구성원별 활동 현황)를 조회함 */
    suspend fun getGroupDetail(groupId: Long): ResultState<GroupDetail>

    /** 그룹을 삭제함. ACTIVE OWNER만 가능하고 그룹에 종속된 데이터까지 지워짐 */
    suspend fun deleteGroup(groupId: Long): ResultState<Unit>

    /** 그룹에서 나감. OWNER는 나갈 수 없어서 [LeaveGroupResult.OwnerMustDelete]로 돌아옴 */
    suspend fun leaveGroup(groupId: Long): ResultState<LeaveGroupResult>

    /** 초대코드로 그룹에 가입함 */
    suspend fun joinGroup(inviteCode: String): ResultState<GroupJoinResult>

    /** 가입 전에 초대코드로 그룹 정보를 미리 봄 */
    suspend fun getGroupJoinPreview(inviteCode: String): ResultState<GroupJoinPreview>

    /** 방 잠금/해제. 잠그면 초대코드로 새로 못 들어옴 */
    suspend fun setGroupLock(groupId: Long, locked: Boolean): ResultState<Boolean>

    /** 이 그룹에서 쓰는 내 상태 메시지를 바꿈 */
    suspend fun updateMyStatusMessage(groupId: Long, statusMessage: String): ResultState<String>

    /** 방 이름을 바꿈. 방장만 가능함 */
    suspend fun updateGroupName(groupId: Long, name: String): ResultState<Unit>

    /** 방장 권한을 다른 구성원에게 넘김 */
    suspend fun transferGroupOwner(groupId: Long, targetMemberId: Long): ResultState<Unit>

    /** 구성원을 강제 퇴장시킴. 방장만 가능함 */
    suspend fun kickGroupMember(groupId: Long, targetMemberId: Long): ResultState<Unit>

    /** 그룹 루틴을 삭제함 */
    suspend fun deleteGroupRoutine(groupId: Long, routineId: Long): ResultState<Unit>

    /** 로그인한 회원이 속한 모든 그룹의 오늘자 루틴을 조회함 */
    suspend fun getTodayGroupRoutines(): ResultState<List<TodayGroupRoutine>>

    /** 그룹의 현재 초대코드를 조회함 */
    suspend fun getInviteCode(groupId: Long): ResultState<GroupInviteCode>

    suspend fun getGroupRoutineCategories(groupId: Long): ResultState<GroupRoutineCategoryList>

    suspend fun createGroupRoutineCategory(
        groupId: Long,
        name: String,
        color: String?,
    ): ResultState<GroupRoutineCategory>

    suspend fun getGroupRoutineVerifications(
        groupId: Long,
        routineId: Long,
        cursor: Long?,
        size: Int?,
    ): ResultState<GroupRoutineVerificationFeed>
}
