package com.li_routi.core.domain.suggestion

import com.li_routi.core.common.kotlin.util.ResultState

/**
 * 건의 분류. 서버 마스터 데이터라 앱에 code/name을 상수로 두지 않는다.
 * [id]만 등록 요청에 쓰고, [name]은 목록·작성 화면에 그대로 보여 준다.
 */
data class SuggestionCategory(
    val id: Long,
    val code: String,
    val name: String,
)

/** GET/POST `/api/members/me/suggestions` 한 건. */
data class Suggestion(
    val id: Long,
    val category: SuggestionCategory,
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
 * 건의 등록 결과. 없는 분류(404/`SUGGESTION404_1`)와 내려간 분류(409/`SUGGESTION409_1`)는
 * 둘 다 작성용 분류 목록을 다시 받아야 해서 [CategoryUnavailable]로 묶는다.
 */
sealed interface CreateSuggestionResult {
    data class Success(val suggestion: Suggestion) : CreateSuggestionResult
    data object CategoryUnavailable : CreateSuggestionResult
}

interface SuggestionRepository {
    suspend fun getMySuggestions(cursor: Long?, size: Int): ResultState<SuggestionPage>
    suspend fun getCategories(): ResultState<List<SuggestionCategory>>
    suspend fun createSuggestion(categoryId: Long, content: String): ResultState<CreateSuggestionResult>
}
