package com.li_routi.core.domain.suggestion

import com.li_routi.core.common.kotlin.util.ResultState

class GetMySuggestionsUseCase(
    private val repository: SuggestionRepository,
) {
    suspend operator fun invoke(
        cursor: Long? = null,
        size: Int = SuggestionPageSize,
    ): ResultState<SuggestionPage> = repository.getMySuggestions(cursor, size)
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
        content: String,
    ): ResultState<CreateSuggestionResult> = repository.createSuggestion(categoryId, content)
}

/** 서버 기본값과 같다. 1~50 밖이면 400이라 클라에서 깎지 않는다. */
const val SuggestionPageSize = 20
