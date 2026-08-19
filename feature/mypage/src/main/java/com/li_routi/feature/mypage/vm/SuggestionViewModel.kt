package com.li_routi.feature.mypage.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.di.SuggestionContainer
import com.li_routi.core.domain.suggestion.CreateSuggestionResult
import com.li_routi.core.domain.suggestion.CreateSuggestionUseCase
import com.li_routi.core.domain.suggestion.GetMySuggestionsUseCase
import com.li_routi.core.domain.suggestion.GetSuggestionCategoriesUseCase
import com.li_routi.core.domain.suggestion.Suggestion
import com.li_routi.core.domain.suggestion.SuggestionCategory
import com.li_routi.core.domain.suggestion.SuggestionPageSize
import com.li_routi.feature.mypage.component.SuggestionCategoryUiModel
import com.li_routi.feature.mypage.component.SuggestionUiModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val SuggestionContentMaxLength = 2000

private const val CategoryUnavailableMessage = "선택할 수 없는 분류입니다. 다시 골라 주세요."

data class SuggestionUiState(
    val items: List<SuggestionUiModel> = emptyList(),
    val nextCursor: Long? = null,
    val hasNext: Boolean = false,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val listError: String? = null,
    val categories: List<SuggestionCategoryUiModel> = emptyList(),
    val selectedCategoryId: Long? = null,
    val isCategoriesLoading: Boolean = false,
    val categoriesError: String? = null,
    val isSaving: Boolean = false,
)

sealed interface SuggestionUiEvent {
    data object Created : SuggestionUiEvent
    data class ShowError(val message: String) : SuggestionUiEvent
}

/**
 * 건의하기 ViewModel. 목록은 커서 페이지네이션, 작성은 서버 분류 + 본문만 받는다.
 */
class SuggestionViewModel(
    private val getMySuggestionsUseCase: GetMySuggestionsUseCase =
        SuggestionContainer.getMySuggestionsUseCase,
    private val getSuggestionCategoriesUseCase: GetSuggestionCategoriesUseCase =
        SuggestionContainer.getSuggestionCategoriesUseCase,
    private val createSuggestionUseCase: CreateSuggestionUseCase =
        SuggestionContainer.createSuggestionUseCase,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(SuggestionUiState())
    val uiState: StateFlow<SuggestionUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<SuggestionUiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<SuggestionUiEvent> = _uiEvent.asSharedFlow()

    private var listGeneration = 0
    private var categoriesGeneration = 0

    fun refresh() {
        loadSuggestions(reset = true)
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.isLoading || state.isLoadingMore || !state.hasNext) return
        loadSuggestions(reset = false)
    }

    fun loadCategories() {
        val generation = ++categoriesGeneration
        _uiState.update { it.copy(isCategoriesLoading = true, categoriesError = null) }
        viewModelScope.launch {
            when (val result = getSuggestionCategoriesUseCase()) {
                is ResultState.Success -> {
                    if (generation != categoriesGeneration) return@launch
                    val mapped = result.data.map { it.toUiModel() }
                    val selectedStillValid = mapped.any { it.id == _uiState.value.selectedCategoryId }
                    _uiState.update {
                        it.copy(
                            categories = mapped,
                            selectedCategoryId = if (selectedStillValid) it.selectedCategoryId else null,
                            isCategoriesLoading = false,
                            categoriesError = null,
                        )
                    }
                }
                is ResultState.Error -> {
                    if (generation != categoriesGeneration) return@launch
                    _uiState.update {
                        it.copy(isCategoriesLoading = false, categoriesError = result.message)
                    }
                }
                ResultState.Loading -> Unit
            }
        }
    }

    fun onCreateCategorySelected(categoryId: Long) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun create(content: String) {
        val categoryId = _uiState.value.selectedCategoryId ?: return
        val trimmed = content.trim()
        if (trimmed.isEmpty() || _uiState.value.isSaving) return

        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            when (val result = createSuggestionUseCase(categoryId, trimmed.take(SuggestionContentMaxLength))) {
                is ResultState.Success -> when (result.data) {
                    is CreateSuggestionResult.Success -> {
                        _uiState.update {
                            it.copy(isSaving = false, selectedCategoryId = null)
                        }
                        _uiEvent.emit(SuggestionUiEvent.Created)
                        loadSuggestions(reset = true)
                    }
                    CreateSuggestionResult.CategoryUnavailable -> {
                        _uiState.update { it.copy(isSaving = false, selectedCategoryId = null) }
                        _uiEvent.emit(SuggestionUiEvent.ShowError(CategoryUnavailableMessage))
                        loadCategories()
                    }
                }
                is ResultState.Error -> {
                    _uiState.update { it.copy(isSaving = false) }
                    _uiEvent.emit(SuggestionUiEvent.ShowError(result.message))
                }
                ResultState.Loading -> Unit
            }
        }
    }

    private fun loadSuggestions(reset: Boolean) {
        val generation = ++listGeneration
        val cursor = if (reset) null else _uiState.value.nextCursor
        _uiState.update {
            it.copy(
                isLoading = reset,
                isLoadingMore = !reset,
                listError = null,
            )
        }
        viewModelScope.launch {
            when (val result = getMySuggestionsUseCase(cursor = cursor, size = SuggestionPageSize)) {
                is ResultState.Success -> {
                    if (generation != listGeneration) return@launch
                    val mapped = result.data.suggestions.map { it.toUiModel() }
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            items = if (reset) mapped else state.items + mapped,
                            nextCursor = result.data.nextCursor,
                            hasNext = result.data.hasNext && result.data.nextCursor != null,
                            listError = null,
                        )
                    }
                }
                is ResultState.Error -> {
                    if (generation != listGeneration) return@launch
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            listError = result.message,
                        )
                    }
                }
                ResultState.Loading -> Unit
            }
        }
    }
}

private fun Suggestion.toUiModel(): SuggestionUiModel = SuggestionUiModel(
    id = id,
    categoryName = category.name,
    date = createdAt.toDisplayDate(),
    content = content,
)

private fun SuggestionCategory.toUiModel(): SuggestionCategoryUiModel =
    SuggestionCategoryUiModel(id = id, name = name)

private fun String.toDisplayDate(): String {
    val millis = toEpochMillisOrNull() ?: return this
    return SimpleDateFormat("yyyy. MM. dd", Locale.KOREA).apply {
        timeZone = TimeZone.getDefault()
    }.format(Date(millis))
}

private fun String.toEpochMillisOrNull(): Long? {
    val normalized = trim()
        .replace("Z", "+0000")
        .replace(Regex("([+-]\\d{2}):(\\d{2})$"), "$1$2")
    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
        "yyyy-MM-dd'T'HH:mm:ssZ",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd'T'HH:mm:ss",
    )
    for (pattern in patterns) {
        val millis = runCatching {
            SimpleDateFormat(pattern, Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
                isLenient = false
            }.parse(normalized)?.time
        }.getOrNull()
        if (millis != null) return millis
    }
    return null
}
