package com.li_routi.core.data.mapper

import com.li_routi.core.common.kotlin.util.ApiException
import com.li_routi.core.data.network.dto.response.GroupCreateResultResponse
import com.li_routi.core.data.network.dto.response.GroupDetailResponse
import com.li_routi.core.data.network.dto.response.GroupInviteCodeResponse
import com.li_routi.core.data.network.dto.response.GroupMemberActivityResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineCategoryListResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineCategoryResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineFeedResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineVerificationItemResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineScheduleResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineUpdateResultResponse
import com.li_routi.core.data.network.dto.response.TodayGroupRoutineListResponse
import com.li_routi.core.data.network.dto.response.TodayGroupRoutineResponse
import com.li_routi.core.domain.grouproutine.CreatedGroup
import com.li_routi.core.domain.grouproutine.GroupDetail
import com.li_routi.core.domain.grouproutine.GroupInviteCode
import com.li_routi.core.domain.grouproutine.GroupMemberActivity
import com.li_routi.core.domain.grouproutine.GroupRoutineCategory
import com.li_routi.core.domain.grouproutine.GroupRoutineCategoryList
import com.li_routi.core.domain.grouproutine.GroupRoutineVerificationFeed
import com.li_routi.core.domain.grouproutine.GroupRoutineVerificationItem
import com.li_routi.core.domain.grouproutine.GroupRoutineSchedule
import com.li_routi.core.domain.grouproutine.GroupRoutineStatus
import com.li_routi.core.domain.grouproutine.GroupRoutineUpdateResult
import com.li_routi.core.domain.grouproutine.RepeatDay
import com.li_routi.core.domain.grouproutine.TodayGroupRoutine

fun GroupCreateResultResponse.toDomain(): CreatedGroup = CreatedGroup(
    groupId = groupId,
    name = name,
    routines = routines.map { it.toDomain() },
    assignmentCount = assignmentCount,
)

fun GroupDetailResponse.toDomain(): GroupDetail = GroupDetail(
    groupId = groupId,
    groupName = groupName,
    inviteCode = inviteCode,
    members = members.map { it.toDomain() },
)

fun GroupMemberActivityResponse.toDomain(): GroupMemberActivity = GroupMemberActivity(
    memberId = memberId,
    name = name,
    profileImageKey = profileImageKey,
    statusMessage = statusMessage,
    currentStreak = currentStreak,
    totalLikeCount = totalLikeCount,
    // 할당이 없는 구성원은 서버가 dailyProgress를 안 내려줄 수 있어서 0/0으로 채움
    completedCount = dailyProgress?.completedCount ?: 0L,
    totalCount = dailyProgress?.totalCount ?: 0L,
)

fun GroupRoutineScheduleResponse.toDomain(): GroupRoutineSchedule = GroupRoutineSchedule(
    // PR 반영: Enum 변환 실패 시 안전하게 캐치하여 명확한 ApiException 발생
    repeatDay = runCatching { RepeatDay.valueOf(repeatDay) }
        .getOrElse { throw ApiException("지원하지 않는 repeatDay: $repeatDay") },
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
    // PR 반영: Enum 변환 실패 시 안전하게 캐치하여 명확한 ApiException 발생
    status = runCatching { GroupRoutineStatus.valueOf(status) }
        .getOrElse { throw ApiException("지원하지 않는 status: $status") },
)

fun TodayGroupRoutineListResponse.toDomain(): List<TodayGroupRoutine> = routines.map { it.toDomain() }

fun GroupInviteCodeResponse.toDomain(): GroupInviteCode = GroupInviteCode(
    inviteCode = inviteCode,
)

fun GroupRoutineCategoryListResponse.toDomain(): GroupRoutineCategoryList = GroupRoutineCategoryList(
    categories = categories.map { it.toDomain() },
    addableCount = addableCount,
)

fun GroupRoutineCategoryResponse.toDomain(): GroupRoutineCategory = GroupRoutineCategory(
    categoryId = categoryId,
    name = name,
    color = color,
    fixed = fixed,
)

fun GroupRoutineFeedResponse.toDomain(): GroupRoutineVerificationFeed = GroupRoutineVerificationFeed(
    verifications = verifications.map { it.toDomain() },
    nextCursor = nextCursor,
    hasNext = hasNext,
)

fun GroupRoutineVerificationItemResponse.toDomain(): GroupRoutineVerificationItem = GroupRoutineVerificationItem(
    verificationId = verificationId,
    assignmentId = assignmentId,
    memberId = memberId,
    nickname = nickname,
    imageUrl = imageUrl,
    content = content,
    verifiedAt = verifiedAt,
)
