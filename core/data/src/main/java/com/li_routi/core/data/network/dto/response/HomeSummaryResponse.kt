package com.li_routi.core.data.network.dto.response

/** GET /api/home 응답 result. */
data class HomeSummaryResponse(
    val userInfo: HomeUserInfoResponse,
    val myRoutines: MyRoutinesSectionResponse,
    val groupRoutines: GroupRoutinesSectionResponse,
)

data class HomeUserInfoResponse(
    val memberId: Long,
    val nickname: String,
)

data class MyRoutinesSectionResponse(
    val routines: List<MyRoutineItemResponse> = emptyList(),
    val isEmpty: Boolean = true,
)

data class MyRoutineItemResponse(
    val routineId: Long,
    val categoryId: Long,
    val categoryName: String,
    val templateId: Long?,
    val name: String,
    val endTime: String?,
    val repeatDays: List<String> = emptyList(),
    val alarmTime: String?,
    val completedToday: Boolean = false,
)

data class GroupRoutinesSectionResponse(
    val routines: List<GroupRoutineItemResponse> = emptyList(),
    val isEmpty: Boolean = true,
)

data class GroupRoutineItemResponse(
    val assignmentId: Long,
    val routineId: Long,
    val groupId: Long,
    val groupName: String,
    val categoryId: Long,
    val categoryName: String,
    val title: String,
    val description: String?,
    val assignedDate: String?,
    val scheduledStartTime: String?,
    val scheduledEndTime: String?,
    val status: String,
)
