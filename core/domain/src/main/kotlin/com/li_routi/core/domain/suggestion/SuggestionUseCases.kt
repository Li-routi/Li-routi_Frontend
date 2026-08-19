package com.li_routi.core.domain.suggestion

import com.li_routi.core.common.kotlin.util.ResultState

const val SuggestionPageSize = 20

class GetMySuggestionsUseCase(
    private val repository: SuggestionRepository,
) {
    suspend operator fun invoke(
        cursor: Long? = null,
        size: Int = SuggestionPageSize,
        keyword: String? = null,
        categoryId: Long? = null,
    ): ResultState<SuggestionPageResult> = repository.getMySuggestions(cursor, size, keyword, categoryId)
}

class GetSuggestionCategoriesUseCase(
    private val repository: SuggestionRepository,
) {
    suspend operator fun invoke(): ResultState<List<SuggestionCategory>> = repository.getCategories()
}

class CreateSuggestionUseCase(
    private val repository: SuggestionRepository,
) {
    suspend operator fun invoke(
        categoryId: Long,
        title: String,
        content: String,
    ): ResultState<CreateSuggestionResult> = repository.createSuggestion(categoryId, title, content)
}
