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

/** PATCH /api/routines/{routineId} 요청. */
data class UpdateMemberRoutine(
    val name: String,
    val endTime: String,
    val repeatDays: List<String>,
    val alarmTime: String? = null,
)
