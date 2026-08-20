package com.li_routi.core.data.mapper

import com.li_routi.core.data.network.dto.request.CreateRoutineRequestItem
import com.li_routi.core.data.network.dto.response.CreateRoutinesResultResponse
import com.li_routi.core.data.network.dto.response.CreatedRoutineResponse
import com.li_routi.core.data.network.dto.response.MemberRoutineListResponse
import com.li_routi.core.data.network.dto.response.RoutineCategoryListResponse
import com.li_routi.core.data.network.dto.response.RoutineCategoryResponse
import com.li_routi.core.data.network.dto.response.RoutineTemplateListResponse
import com.li_routi.core.data.network.dto.response.RoutineTemplateResponse
import com.li_routi.core.domain.routine.CreateRoutineItem
import com.li_routi.core.domain.routine.CreateRoutinesResult
import com.li_routi.core.domain.routine.CreatedRoutine
import com.li_routi.core.domain.routine.MemberRoutineList
import com.li_routi.core.domain.routine.RoutineCategory
import com.li_routi.core.domain.routine.RoutineCategoryList
import com.li_routi.core.domain.routine.RoutineTemplate

fun MemberRoutineListResponse.toDomain(): MemberRoutineList = MemberRoutineList(
    routines = routines.map { it.toDomain() },
)

fun RoutineCategoryListResponse.toDomain(): RoutineCategoryList = RoutineCategoryList(
    categories = categories.map { it.toDomain() },
    addableCount = addableCount,
)

fun RoutineCategoryResponse.toDomain(): RoutineCategory = RoutineCategory(
    categoryId = categoryId,
    name = name,
    color = color,
    fixed = fixed,
)

fun RoutineTemplateListResponse.toDomain(): List<RoutineTemplate> =
    templates.map { it.toDomain() }

fun RoutineTemplateResponse.toDomain(): RoutineTemplate = RoutineTemplate(
    templateId = templateId,
    categoryId = categoryId,
    categoryName = categoryName,
    name = name,
    alreadyAdded = alreadyAdded,
)

fun CreateRoutineItem.toRequest(): CreateRoutineRequestItem = CreateRoutineRequestItem(
    categoryId = categoryId,
    templateId = templateId,
    name = name,
    startTime = startTime,
    endTime = endTime,
    repeatDays = repeatDays,
    alarmTime = alarmTime,
)

fun CreateRoutinesResultResponse.toDomain(): CreateRoutinesResult = CreateRoutinesResult(
    routines = routines.map { it.toDomain() },
    activeRoutineCount = activeRoutineCount,
)

fun CreatedRoutineResponse.toDomain(): CreatedRoutine = CreatedRoutine(
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
