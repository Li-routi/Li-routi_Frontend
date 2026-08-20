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
    /** 대표로 설정한 업적 배지. 대표 업적이 없으면 null. */
    val representativeAchievement: HomeRepresentativeAchievementResponse?,
)

data class HomeRepresentativeAchievementResponse(
    val achievementId: Long,
    val name: String?,
    val badgeImageUrl: String?,
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
    /** HH:mm. 기존 루틴은 생략될 수 있다. */
    val startTime: String? = null,
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
