package com.li_routi.core.data.mapper

import com.li_routi.core.common.kotlin.util.ApiException
import com.li_routi.core.data.network.dto.response.GroupCreateResultResponse
import com.li_routi.core.data.network.dto.response.GroupDetailResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineDisappointmentResponse
import com.li_routi.core.data.network.dto.response.GroupInviteCodeResponse
import com.li_routi.core.data.network.dto.response.GroupJoinPreviewResponse
import com.li_routi.core.data.network.dto.response.GroupJoinResultResponse
import com.li_routi.core.data.network.dto.response.GroupMemberActivityResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineLikeResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineListItemResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineListResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineCategoryListResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineCategoryResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineFeedResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineVerificationItemResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineVerificationReadResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineScheduleResponse
import com.li_routi.core.data.network.dto.response.GroupRoutineUpdateResultResponse
import com.li_routi.core.data.network.dto.response.ParticipatingGroupListResponse
import com.li_routi.core.data.network.dto.response.ParticipatingGroupResponse
import com.li_routi.core.data.network.dto.response.TodayGroupRoutineListResponse
import com.li_routi.core.data.network.dto.response.TodayGroupRoutineResponse
import com.li_routi.core.data.network.dto.response.UnreadGroupRoutineVerificationListResponse
import com.li_routi.core.data.network.dto.response.UnreadGroupRoutineVerificationResponse
import com.li_routi.core.domain.grouproutine.CreatedGroup
import com.li_routi.core.domain.grouproutine.GroupDetail
import com.li_routi.core.domain.grouproutine.GroupInviteCode
import com.li_routi.core.domain.grouproutine.GroupJoinPreview
import com.li_routi.core.domain.grouproutine.GroupJoinResult
import com.li_routi.core.domain.grouproutine.GroupMemberActivity
import com.li_routi.core.domain.grouproutine.GroupRoutineDisappointment
import com.li_routi.core.domain.grouproutine.GroupRoutineLike
import com.li_routi.core.domain.grouproutine.GroupRoutineItem
import com.li_routi.core.domain.grouproutine.GroupRoutineCategory
import com.li_routi.core.domain.grouproutine.GroupRoutineCategoryList
import com.li_routi.core.domain.grouproutine.GroupRoutineVerificationFeed
import com.li_routi.core.domain.grouproutine.GroupRoutineVerificationItem
import com.li_routi.core.domain.grouproutine.GroupRoutineVerificationRead
import com.li_routi.core.domain.grouproutine.ParticipatingGroup
import com.li_routi.core.domain.grouproutine.GroupRoutineSchedule
import com.li_routi.core.domain.grouproutine.GroupRoutineStatus
import com.li_routi.core.domain.grouproutine.GroupRoutineUpdateResult
import com.li_routi.core.domain.grouproutine.RepeatDay
import com.li_routi.core.domain.grouproutine.TodayGroupRoutine
import com.li_routi.core.domain.grouproutine.UnreadGroupRoutineVerification
import com.li_routi.core.domain.grouproutine.UnreadGroupRoutineVerificationFeed

/**
 * Gson은 응답에 없는 숫자 필드를 0으로 채움 — 그대로 두면 0이 실제 식별자인 것처럼
 * 도메인까지 흘러가서 엉뚱한 대상에 요청이 나감. 식별자는 0을 거부함
 */
private fun Long.requireId(name: String): Long {
    if (this == 0L) throw ApiException("서버 응답에 $name 가 없습니다.")
    return this
}

fun GroupCreateResultResponse.toDomain(): CreatedGroup = CreatedGroup(
    groupId = groupId.requireId("groupId"),
    name = name,
    routines = routines.map { it.toDomain() },
    assignmentCount = assignmentCount,
)

fun ParticipatingGroupListResponse.toDomain(): List<ParticipatingGroup> =
    groups.map { it.toDomain() }

fun ParticipatingGroupResponse.toDomain(): ParticipatingGroup = ParticipatingGroup(
    groupId = groupId.requireId("groupId"),
    groupName = groupName,
    activeMemberCount = activeMemberCount,
    activeRoutineCount = activeRoutineCount,
    todayAssignedRoutineCount = todayAssignedRoutineCount,
    todayCompletedRoutineCount = todayCompletedRoutineCount,
    currentStreak = currentStreak,
    monthlyAchievementRate = monthlyAchievementRate,
    todayGroupVerificationCount = todayGroupVerificationCount,
)

fun GroupDetailResponse.toDomain(): GroupDetail = GroupDetail(
    groupId = groupId.requireId("groupId"),
    groupName = groupName.orEmpty(),
    inviteCode = inviteCode.orEmpty(),
    isCurrentUserOwner = myRole == "OWNER",
    members = members.orEmpty().map { it.toDomain() },
)

fun GroupMemberActivityResponse.toDomain(): GroupMemberActivity = GroupMemberActivity(
    memberId = memberId.requireId("memberId"),
    name = name.orEmpty(),
    profileImageKey = profileImageKey,
    statusMessage = statusMessage,
    currentStreak = currentStreak,
    totalLikeCount = totalLikeCount,
    totalPokeCount = totalPokeCount,
    // 할당이 없는 구성원은 서버가 dailyProgress를 안 내려줄 수 있어서 0/0으로 채움
    completedCount = dailyProgress?.completedCount ?: 0L,
    totalCount = dailyProgress?.totalCount ?: 0L,
)

fun GroupJoinResultResponse.toDomain(): GroupJoinResult = GroupJoinResult(
    groupId = groupId.requireId("groupId"),
    name = name,
    memberStatus = memberStatus,
)

fun GroupJoinPreviewResponse.toDomain(): GroupJoinPreview = GroupJoinPreview(
    groupId = groupId.requireId("groupId"),
    name = name,
    activeMemberCount = activeMemberCount,
    maxMemberCount = maxMemberCount,
    totalRoutineCount = totalRoutineCount,
    joinable = joinable,
    unavailableReason = unavailableReason,
)

fun GroupRoutineScheduleResponse.toDomain(): GroupRoutineSchedule = GroupRoutineSchedule(
    // PR 반영: Enum 변환 실패 시 안전하게 캐치하여 명확한 ApiException 발생
    repeatDay = runCatching { RepeatDay.valueOf(repeatDay) }
        .getOrElse { throw ApiException("지원하지 않는 repeatDay: $repeatDay") },
    startTime = startTime,
    endTime = endTime,
)

fun GroupRoutineListItemResponse.toDomain(): GroupRoutineItem = GroupRoutineItem(
    routineId = routineId.requireId("routineId"),
    categoryId = categoryId.requireId("categoryId"),
    categoryName = categoryName,
    title = title,
    description = description,
    schedules = schedules.map { it.toDomain() },
)

fun GroupRoutineListResponse.toDomain(): List<GroupRoutineItem> = routines.map { it.toDomain() }

// 그룹 생성 응답(CreatedRoutine)에는 groupId가 없어서 0으로 들어옴 — routineId만 검증함
fun GroupRoutineUpdateResultResponse.toDomain(): GroupRoutineUpdateResult = GroupRoutineUpdateResult(
    routineId = routineId.requireId("routineId"),
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
    routineId = routineId.requireId("routineId"),
    groupId = groupId.requireId("groupId"),
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
    likeCount = likeCount,
    liked = liked,
)

fun UnreadGroupRoutineVerificationListResponse.toDomain(): UnreadGroupRoutineVerificationFeed =
    UnreadGroupRoutineVerificationFeed(
        verifications = verifications.map { it.toDomain() },
        nextCursor = nextCursor,
        hasNext = hasNext,
    )

fun UnreadGroupRoutineVerificationResponse.toDomain(): UnreadGroupRoutineVerification =
    UnreadGroupRoutineVerification(
        verificationId = verificationId,
        authorMemberId = authorMemberId,
        authorName = authorName,
        routineName = routineName,
        imageUrl = imageUrl,
        content = content,
        verifiedAt = verifiedAt,
    )

fun GroupRoutineVerificationReadResponse.toDomain(): GroupRoutineVerificationRead =
    GroupRoutineVerificationRead(lastReadVerificationId = lastReadVerificationId)

fun GroupRoutineLikeResponse.toDomain(): GroupRoutineLike =
    GroupRoutineLike(
        verificationId = verificationId,
        likeCount = likeCount,
        liked = liked,
    )

fun GroupRoutineDisappointmentResponse.toDomain(): GroupRoutineDisappointment =
    GroupRoutineDisappointment(
        verificationId = verificationId,
        count = count,
        disappointed = disappointed,
    )
