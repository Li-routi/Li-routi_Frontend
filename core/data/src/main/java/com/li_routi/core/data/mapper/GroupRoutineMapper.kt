package com.li_routi.core.data.mapper

import com.li_routi.core.data.network.dto.response.GroupCreateResultResponse
import com.li_routi.core.data.network.dto.response.GroupInviteCodeResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineScheduleResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineUpdateResultResponse
import com.li_routi.core.data.network.dto.response.TodayGroupRoutineListResponse
import com.li_routi.core.data.network.dto.response.TodayGroupRoutineResponse
import com.li_routi.core.domain.grouproutine.CreatedGroup
import com.li_routi.core.domain.grouproutine.GroupInviteCode
import com.li_routi.core.domain.grouproutine.GroupRoutineSchedule
import com.li_routi.core.domain.grouproutine.GroupRoutineStatus
import com.li_routi.core.domain.grouproutine.GroupRoutineUpdateResult
import com.li_routi.core.domain.grouproutine.RepeatDay
import com.li_routi.core.domain.grouproutine.TodayGroupRoutine

fun GroupCreateResultResponse.toDomain(): CreatedGroup = CreatedGroup(
    groupId = groupId,
    name = name,
    assignmentCount = assignmentCount,
)

fun GroupRoutineScheduleResponse.toDomain(): GroupRoutineSchedule = GroupRoutineSchedule(
    repeatDay = RepeatDay.valueOf(repeatDay),
    startTime = startTime,
    endTime = endTime,
)

fun GroupRoutineUpdateResultResponse.toDomain(): GroupRoutineUpdateResult = GroupRoutineUpdateResult(
    routineId = routineId,
    groupId = groupId,
    categoryId = categoryId,
    categoryName = categoryName,
    title = title,
    description = description,
    schedules = schedules.map { it.toDomain() },
    assignmentCount = assignmentCount,
)

fun TodayGroupRoutineResponse.toDomain(): TodayGroupRoutine = TodayGroupRoutine(
    assignmentId = assignmentId,
    routineId = routineId,
    groupId = groupId,
    groupName = groupName,
    categoryId = categoryId,
    categoryName = categoryName,
    title = title,
    description = description,
    assignedDate = assignedDate,
    scheduledStartTime = scheduledStartTime,
    scheduledEndTime = scheduledEndTime,
    status = GroupRoutineStatus.valueOf(status),
)

fun TodayGroupRoutineListResponse.toDomain(): List<TodayGroupRoutine> = routines.map { it.toDomain() }

fun GroupInviteCodeResponse.toDomain(): GroupInviteCode = GroupInviteCode(
    inviteCode = inviteCode,
    expiresAt = expiresAt,
)
