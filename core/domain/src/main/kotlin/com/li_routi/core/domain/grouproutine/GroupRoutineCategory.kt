package com.li_routi.core.domain.grouproutine

data class GroupRoutineCategory(
    val categoryId: Long,
    val name: String,
    val color: String?,
    val fixed: Boolean,
)

data class GroupRoutineCategoryList(
    val categories: List<GroupRoutineCategory>,
    val addableCount: Int,
)
