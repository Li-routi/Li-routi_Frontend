package com.li_routi.core.data.mapper

import com.li_routi.core.data.network.dto.response.GroupRoutineItemResponse
import com.li_routi.core.data.network.dto.response.HomeSummaryResponse
import com.li_routi.core.data.network.dto.response.MyRoutineItemResponse
import com.li_routi.core.domain.home.GroupRoutine
import com.li_routi.core.domain.home.GroupRoutineStatus
import com.li_routi.core.domain.home.HomeSummary
import com.li_routi.core.domain.home.HomeUserInfo
import com.li_routi.core.domain.home.MyRoutine

fun HomeSummaryResponse.toDomain(): HomeSummary = HomeSummary(
    userInfo = HomeUserInfo(
        memberId = userInfo.memberId,
        nickname = userInfo.nickname,
        representativeBadgeName = userInfo.representativeAchievement?.name,
        representativeBadgeImageUrl = userInfo.representativeAchievement?.badgeImageUrl,
    ),
    myRoutines = myRoutines.routines.map { it.toDomain() },
    groupRoutines = groupRoutines.routines.map { it.toDomain() },
)

fun MyRoutineItemResponse.toDomain(): MyRoutine = MyRoutine(
    routineId = routineId,
    categoryId = categoryId,
    categoryName = categoryName,
    templateId = templateId,
    name = name,
    startTime = startTime,
    endTime = endTime,
    repeatDays = repeatDays,
    alarmTime = alarmTime,
    completedToday = completedToday,
)

fun GroupRoutineItemResponse.toDomain(): GroupRoutine = GroupRoutine(
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
    status = status.toGroupRoutineStatus(),
)

private fun String.toGroupRoutineStatus(): GroupRoutineStatus =
    runCatching { GroupRoutineStatus.valueOf(this) }.getOrDefault(GroupRoutineStatus.Unknown)
