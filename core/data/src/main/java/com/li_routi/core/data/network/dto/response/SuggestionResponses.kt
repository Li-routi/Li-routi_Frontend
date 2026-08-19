package com.li_routi.core.data.network.dto.response

data class SuggestionCategoryResponse(
    val id: Long,
    val code: String,
    val name: String,
)

data class SuggestionItemResponse(
    val id: Long,
    val category: SuggestionCategoryResponse,
    val content: String,
    val status: String,
    val createdAt: String,
)

data class SuggestionListResponse(
    val suggestions: List<SuggestionItemResponse>,
    val nextCursor: Long?,
    val hasNext: Boolean,
)

data class SuggestionCategoriesResponse(
    val categories: List<SuggestionCategoryResponse>,
)
