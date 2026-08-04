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

    /** 로그인한 회원이 속한 모든 그룹의 오늘자 루틴을 조회함 */
    suspend fun getTodayGroupRoutines(): ResultState<List<TodayGroupRoutine>>

    /** 그룹 초대코드를 새로 발급함 */
    suspend fun issueInviteCode(groupId: Long): ResultState<GroupInviteCode>

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
