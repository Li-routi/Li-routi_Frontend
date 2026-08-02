package com.li_routi.core.domain.grouproutine

import com.li_routi.core.common.kotlin.util.ResultState

interface GroupRoutineRepository {

    /** 그룹에 루틴을 등록한다. 요청/응답 모양이 [updateGroupRoutine]과 동일하다(실서버 확인, 스웨거 문서와 다름). */
    suspend fun createGroupRoutine(
        groupId: Long,
        categoryId: Long,
        title: String,
        description: String,
        schedules: List<GroupRoutineSchedule>,
    ): ResultState<GroupRoutineUpdateResult>

    /** 그룹 루틴을 수정한다. */
    suspend fun updateGroupRoutine(
        groupId: Long,
        routineId: Long,
        categoryId: Long,
        title: String,
        description: String,
        schedules: List<GroupRoutineSchedule>,
    ): ResultState<GroupRoutineUpdateResult>

    /** 로그인한 회원이 속한 모든 그룹의 오늘자 루틴을 조회한다. */
    suspend fun getTodayGroupRoutines(): ResultState<List<TodayGroupRoutine>>

    /** 그룹 초대코드를 새로 발급한다. */
    suspend fun issueInviteCode(groupId: Long): ResultState<GroupInviteCode>

    /** 그룹의 현재 초대코드를 조회한다. */
    suspend fun getInviteCode(groupId: Long): ResultState<GroupInviteCode>
}
