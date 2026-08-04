package com.li_routi.core.data.network.dto.response

data class GroupRoutineCategoryListResponse(
    val categories: List<GroupRoutineCategoryResponse> = emptyList(),
    val addableCount: Int = 0,
)

data class GroupRoutineCategoryResponse(
    val categoryId: Long,
    val name: String,
    val color: String?,
    val fixed: Boolean = false,
)
