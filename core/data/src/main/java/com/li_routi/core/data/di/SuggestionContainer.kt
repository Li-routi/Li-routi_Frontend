package com.li_routi.core.data.di

import com.li_routi.core.data.network.NetworkModule
import com.li_routi.core.data.repository.SuggestionRepositoryImpl
import com.li_routi.core.domain.suggestion.CreateSuggestionUseCase
import com.li_routi.core.domain.suggestion.GetMySuggestionsUseCase
import com.li_routi.core.domain.suggestion.GetSuggestionCategoriesUseCase
import com.li_routi.core.domain.suggestion.SuggestionRepository

/**
 * 건의하기 API 수동 구성 root.
 */
object SuggestionContainer {

    private val repository: SuggestionRepository by lazy {
        SuggestionRepositoryImpl(NetworkModule.suggestionApiService)
    }

    val getMySuggestionsUseCase: GetMySuggestionsUseCase by lazy {
        GetMySuggestionsUseCase(repository)
    }

    val getSuggestionCategoriesUseCase: GetSuggestionCategoriesUseCase by lazy {
        GetSuggestionCategoriesUseCase(repository)
    }

    val createSuggestionUseCase: CreateSuggestionUseCase by lazy {
        CreateSuggestionUseCase(repository)
    }
}
