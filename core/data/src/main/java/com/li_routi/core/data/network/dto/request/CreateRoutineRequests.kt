package com.li_routi.core.data.network.dto.request

data class CreateRoutineCategoryRequest(
    val name: String,
    val color: String? = null,
)

data class CreateRoutinesRequest(
    val routines: List<CreateRoutineRequestItem>,
)

data class CreateRoutineRequestItem(
    val categoryId: Long,
    val templateId: Long? = null,
    val name: String,
    /** HH:mm. 생략/null이면 시작 시각 제한 없음. 지정 시 endTime보다 빨라야 한다. */
    val startTime: String? = null,
    val endTime: String? = null,
    val repeatDays: List<String>? = null,
    val alarmTime: String? = null,
)

/** PATCH /api/routines/{routineId} 요청 body. */
data class UpdateMemberRoutineRequest(
    val name: String,
    /** HH:mm. 생략/null이면 시작 시각 제한을 해제한다. */
    val startTime: String? = null,
    val endTime: String,
    val repeatDays: List<String>,
    val alarmTime: String? = null,
)

/** PATCH /api/routines/categories/{categoryId} 요청 body. */
data class UpdateRoutineCategoryRequest(
    val name: String,
    val color: String? = null,
)
