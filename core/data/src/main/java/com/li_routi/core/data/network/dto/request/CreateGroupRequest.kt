package com.li_routi.core.data.network.dto.request

data class CreateGroupRequest(
    val name: String,
    val customCategories: List<CreateGroupCategoryRequest>,
    val routines: List<CreateGroupRoutineRequest>,
)

data class CreateGroupCategoryRequest(
    val clientKey: String,
    val name: String,
    val color: String?,
)

data class CreateGroupRoutineRequest(
    val categoryId: Long?,
    val categoryKey: String?,
    val title: String,
    val description: String,
    val schedules: List<GroupRoutineScheduleRequest>,
)
