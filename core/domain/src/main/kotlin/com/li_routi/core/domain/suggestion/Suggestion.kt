package com.li_routi.core.domain.suggestion

import com.li_routi.core.common.kotlin.util.ResultState

data class SuggestionCategory(
    val id: Long,
    val code: String,
    val name: String,
)

/** GET/POST `/api/members/me/suggestions` 한 건. */
data class Suggestion(
    val id: Long,
    val category: SuggestionCategory,
    val title: String,
    val content: String,
    val status: String,
    val createdAt: String,
)

data class SuggestionPage(
    val suggestions: List<Suggestion>,
    val nextCursor: Long?,
    val hasNext: Boolean,
)

/**
 * 목록 조회 결과. 없는 분류 id(`SUGGESTION404_1`)는 빈 목록이 아니라 별도 결과로 둔다.
 */
sealed interface SuggestionPageResult {
    data class Page(val page: SuggestionPage) : SuggestionPageResult
    data object CategoryNotFound : SuggestionPageResult
}

/**
 * 건의 등록 결과. 없는 분류(404/`SUGGESTION404_1`)와 내려간 분류(409/`SUGGESTION409_1`)는
 * 분류 목록을 다시 받아 고르게 한다.
 */
sealed interface CreateSuggestionResult {
    data class Success(val suggestion: Suggestion) : CreateSuggestionResult
    data object CategoryUnavailable : CreateSuggestionResult
}

interface SuggestionRepository {
    suspend fun getMySuggestions(
        cursor: Long?,
        size: Int,
        keyword: String?,
        categoryId: Long?,
    ): ResultState<SuggestionPageResult>

    suspend fun getCategories(): ResultState<List<SuggestionCategory>>

    suspend fun createSuggestion(
        categoryId: Long,
        title: String,
        content: String,
    ): ResultState<CreateSuggestionResult>
}
