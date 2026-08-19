package com.li_routi.core.data.mapper

import com.li_routi.core.data.network.dto.response.SuggestionCategoriesResponse
import com.li_routi.core.data.network.dto.response.SuggestionCategoryResponse
import com.li_routi.core.data.network.dto.response.SuggestionItemResponse
import com.li_routi.core.data.network.dto.response.SuggestionListResponse
import com.li_routi.core.domain.suggestion.Suggestion
import com.li_routi.core.domain.suggestion.SuggestionCategory
import com.li_routi.core.domain.suggestion.SuggestionPage

fun SuggestionCategoryResponse.toDomain(): SuggestionCategory = SuggestionCategory(
    id = id,
    code = code,
    name = name,
)

fun SuggestionItemResponse.toDomain(): Suggestion = Suggestion(
    id = id,
    category = category.toDomain(),
    content = content,
    status = status,
    createdAt = createdAt,
)

fun SuggestionListResponse.toDomain(): SuggestionPage = SuggestionPage(
    suggestions = suggestions.map { it.toDomain() },
    nextCursor = nextCursor,
    hasNext = hasNext,
)

fun SuggestionCategoriesResponse.toDomain(): List<SuggestionCategory> = categories.map { it.toDomain() }
