package com.li_routi.core.data.repository

import com.li_routi.core.common.kotlin.util.ApiException
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.common.kotlin.util.safeApiCall
import com.li_routi.core.data.mapper.toDomain
import com.li_routi.core.data.network.apiCall
import com.li_routi.core.data.network.dto.request.CreateSuggestionRequest
import com.li_routi.core.data.network.service.SuggestionApiService
import com.li_routi.core.domain.suggestion.CreateSuggestionResult
import com.li_routi.core.domain.suggestion.SuggestionCategory
import com.li_routi.core.domain.suggestion.SuggestionPageResult
import com.li_routi.core.domain.suggestion.SuggestionRepository

class SuggestionRepositoryImpl(
    private val api: SuggestionApiService,
) : SuggestionRepository {

    override suspend fun getMySuggestions(
        cursor: Long?,
        size: Int,
        keyword: String?,
        categoryId: Long?,
    ): ResultState<SuggestionPageResult> = safeApiCall {
        try {
            SuggestionPageResult.Page(
                apiCall {
                    api.getMySuggestions(
                        cursor = cursor,
                        size = size,
                        keyword = keyword,
                        categoryId = categoryId,
                    )
                }.toDomain(),
            )
        } catch (e: ApiException) {
            // SUGGESTION404_1 없는 분류 — 빈 목록으로 읽히지 않게 별도 결과로 둔다.
            // 분류를 안 고른 요청의 404는 다른 실패이므로 그대로 올린다.
            if (e.statusCode == 404 && categoryId != null) {
                SuggestionPageResult.CategoryNotFound
            } else {
                throw e
            }
        }
    }

    override suspend fun getCategories(): ResultState<List<SuggestionCategory>> = safeApiCall {
        apiCall { api.getCategories() }.toDomain()
    }

    override suspend fun createSuggestion(
        categoryId: Long,
        title: String,
        content: String,
    ): ResultState<CreateSuggestionResult> = safeApiCall {
        try {
            CreateSuggestionResult.Success(
                apiCall { api.createSuggestion(CreateSuggestionRequest(categoryId, title, content)) }.toDomain(),
            )
        } catch (e: ApiException) {
            // SUGGESTION404_1 없는 분류 / SUGGESTION409_1 내려간 분류 — 작성 분류 목록을 다시 받는다.
            if (e.statusCode == 404 || e.statusCode == 409) {
                CreateSuggestionResult.CategoryUnavailable
            } else {
                throw e
            }
        }
    }
}
