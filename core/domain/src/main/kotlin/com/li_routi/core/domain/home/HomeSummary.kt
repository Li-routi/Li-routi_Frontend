package com.li_routi.core.domain.home

/** GET /api/home 홈 화면 요약. */
data class HomeSummary(
    val userInfo: HomeUserInfo,
    val myRoutines: List<MyRoutine>,
    val groupRoutines: List<GroupRoutine>,
) {
    val hasActiveRoutine: Boolean get() = myRoutines.isNotEmpty()
    val hasGroupRoom: Boolean get() = groupRoutines.isNotEmpty()
}

data class HomeUserInfo(
    val memberId: Long,
    val nickname: String,
    /** 대표로 설정한 업적 배지 이름. 대표 업적이 없으면 null. */
    val representativeBadgeName: String? = null,
    /** 대표로 설정한 업적 배지 이미지. 대표 업적이 없으면 null. */
    val representativeBadgeImageUrl: String? = null,
)

data class MyRoutine(
    val routineId: Long,
    val categoryId: Long,
    val categoryName: String,
    val templateId: Long?,
    val name: String,
    val startTime: String? = null,
    val endTime: String?,
    val repeatDays: List<String>,
    val alarmTime: String?,
    val completedToday: Boolean,
)

data class GroupRoutine(
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
    val status: GroupRoutineStatus,
)

/**
 * 그룹 루틴 배정 상태.
 * 서버 enum과 맞춰 두되, 알 수 없는 값은 [Unknown]으로 받는다.
 */
enum class GroupRoutineStatus {
    PENDING,
    COMPLETED,
    VERIFIED,
    MISSED,
    Unknown,
    ;

    val isDone: Boolean
        get() = this == COMPLETED || this == VERIFIED

    /** 사진 인증을 시도할 수 있는지 (MISSED·완료는 불가). */
    val canVerify: Boolean
        get() = this == PENDING || this == Unknown
}
