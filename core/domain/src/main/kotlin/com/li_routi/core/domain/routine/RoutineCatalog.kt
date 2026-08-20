package com.li_routi.core.domain.routine

data class RoutineCategory(
    val categoryId: Long,
    val name: String,
    val color: String?,
    val fixed: Boolean,
)

data class RoutineCategoryList(
    val categories: List<RoutineCategory>,
    val addableCount: Int,
)

data class RoutineTemplate(
    val templateId: Long,
    val categoryId: Long,
    val categoryName: String,
    val name: String,
    val alreadyAdded: Boolean,
)

data class CreateRoutineItem(
    val categoryId: Long,
    val templateId: Long? = null,
    val name: String,
    val startTime: String? = null,
    val endTime: String? = null,
    val repeatDays: List<String>? = null,
    val alarmTime: String? = null,
)

data class CreatedRoutine(
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

data class CreateRoutinesResult(
    val routines: List<CreatedRoutine>,
    val activeRoutineCount: Int,
)

/** GET /api/routines — 활성 개인 루틴 목록. */
data class MemberRoutineList(
    val routines: List<CreatedRoutine>,
)

/** PATCH /api/routines/{routineId} 요청. startTime이 null이면 시작 시각 제한을 해제한다. */
data class UpdateMemberRoutine(
    val name: String,
    val startTime: String? = null,
    val endTime: String,
    val repeatDays: List<String>,
    val alarmTime: String? = null,
)

private val HHmmPattern = Regex("^([01]\\d|2[0-3]):([0-5]\\d)$")

/** ROUTINE400_5: startTime이 있으면 endTime보다 빨라야 한다. HH:mm. 생략/blank면 제한 없음. */
fun isValidRoutineTimeRange(startTime: String?, endTime: String?): Boolean {
    val start = startTime?.trim().orEmpty()
    if (start.isEmpty()) return true
    val end = endTime?.trim().orEmpty()
    if (end.isEmpty()) return true
    val startMinutes = parseHHmmToMinutes(start) ?: return false
    val endMinutes = parseHHmmToMinutes(end) ?: return false
    return startMinutes < endMinutes
}

/** `HH:mm`(00:00–23:59). 형식이 아니면 null. */
private fun parseHHmmToMinutes(value: String): Int? {
    val match = HHmmPattern.matchEntire(value) ?: return null
    val hour = match.groupValues[1].toInt()
    val minute = match.groupValues[2].toInt()
    return hour * 60 + minute
}

const val InvalidRoutineTimeRangeMessage = "시작 시간은 마감 시간보다 빨라야 해요."
