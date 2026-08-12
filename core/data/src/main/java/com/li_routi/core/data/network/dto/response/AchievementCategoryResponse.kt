package com.li_routi.core.data.network.dto.response

data class AchievementCategoryResponse(
    val category: String,
    val achievements: List<AchievementResponse>,
)
