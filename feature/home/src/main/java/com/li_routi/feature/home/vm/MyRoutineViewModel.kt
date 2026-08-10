package com.li_routi.feature.home.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.common.ui.routine.CategoryColor
import com.li_routi.core.common.ui.routine.RoutineChecklistItem
import com.li_routi.core.common.ui.routine.toApiColor
import com.li_routi.core.domain.routine.CreateRoutineCategoryUseCase
import com.li_routi.core.domain.routine.CreatedRoutine
import com.li_routi.core.domain.routine.DeleteMemberRoutineUseCase
import com.li_routi.core.domain.routine.DeleteRoutineCategoryUseCase
import com.li_routi.core.domain.routine.GetMemberRoutinesUseCase
import com.li_routi.core.domain.routine.GetRoutineCategoriesUseCase
import com.li_routi.core.domain.routine.RoutineCategory
import com.li_routi.core.domain.routine.UpdateMemberRoutine
import com.li_routi.core.domain.routine.UpdateMemberRoutineUseCase
import com.li_routi.core.domain.routine.UpdateRoutineCategoryUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val AllCategoryLabel = "전체"

/** LiroutiDaySelector 인덱스: 0=일 … 6=토 */
private val DayIndexToApi = listOf(
    "SUNDAY",
    "MONDAY",
    "TUESDAY",
    "WEDNESDAY",
    "THURSDAY",
    "FRIDAY",
    "SATURDAY",
)

private val RepeatDayShortLabels = listOf(
    "MONDAY" to "월",
    "TUESDAY" to "화",
    "WEDNESDAY" to "수",
    "THURSDAY" to "목",
    "FRIDAY" to "금",
    "SATURDAY" to "토",
    "SUNDAY" to "일",
)

data class MyRoutineUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isCreatingCategory: Boolean = false,
    val query: String = "",
    val categories: List<RoutineCategory> = emptyList(),
    val addableCount: Int = 0,
    val selectedCategoryName: String = AllCategoryLabel,
    val routines: List<CreatedRoutine> = emptyList(),
    val errorMessage: String? = null,
) {
    val categoryLabels: List<String>
        get() = listOf(AllCategoryLabel) + categories.map { it.name }

    val addCategoryEnabled: Boolean
        get() = addableCount > 0 && !isCreatingCategory

    val visibleRoutines: List<RoutineChecklistItem>
        get() {
            val trimmedQuery = query.trim()
            return routines
                .asSequence()
                .filter { routine ->
                    selectedCategoryName == AllCategoryLabel ||
                        routine.categoryName == selectedCategoryName
                }
                .filter { routine ->
                    trimmedQuery.isEmpty() ||
                        routine.name.contains(trimmedQuery, ignoreCase = true)
                }
                .map { it.toChecklistItem() }
                .toList()
        }

    fun routineById(routineId: Long): CreatedRoutine? =
        routines.firstOrNull { it.routineId == routineId }
}

sealed interface MyRoutineUiEvent {
    data object NavigateBack : MyRoutineUiEvent
    data object NavigateToAddRoutine : MyRoutineUiEvent
    data object EditCompleted : MyRoutineUiEvent
    data object DeleteCompleted : MyRoutineUiEvent
    data object CategorySaved : MyRoutineUiEvent
    data object CategoryDeleted : MyRoutineUiEvent
}

class MyRoutineViewModel(
    private val getMemberRoutinesUseCase: GetMemberRoutinesUseCase,
    private val getRoutineCategoriesUseCase: GetRoutineCategoriesUseCase,
    private val createRoutineCategoryUseCase: CreateRoutineCategoryUseCase,
    private val updateRoutineCategoryUseCase: UpdateRoutineCategoryUseCase,
    private val deleteRoutineCategoryUseCase: DeleteRoutineCategoryUseCase,
    private val updateMemberRoutineUseCase: UpdateMemberRoutineUseCase,
    private val deleteMemberRoutineUseCase: DeleteMemberRoutineUseCase,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(MyRoutineUiState())
    val uiState: StateFlow<MyRoutineUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<MyRoutineUiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<MyRoutineUiEvent> = _uiEvent.asSharedFlow()

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val routinesDeferred = async { getMemberRoutinesUseCase() }
            val categoriesDeferred = async { getRoutineCategoriesUseCase() }
            val routinesResult = routinesDeferred.await()
            val categoriesResult = categoriesDeferred.await()

            val routines = when (routinesResult) {
                is ResultState.Success -> routinesResult.data.routines
                is ResultState.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = routinesResult.message)
                    }
                    return@launch
                }
                ResultState.Loading -> emptyList()
            }
            val (categories, addableCount) = when (categoriesResult) {
                is ResultState.Success ->
                    categoriesResult.data.categories to categoriesResult.data.addableCount
                is ResultState.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = categoriesResult.message)
                    }
                    return@launch
                }
                ResultState.Loading -> emptyList<RoutineCategory>() to 0
            }

            val selected = _uiState.value.selectedCategoryName
            val nextSelected = when {
                selected == AllCategoryLabel -> AllCategoryLabel
                categories.any { it.name == selected } -> selected
                else -> AllCategoryLabel
            }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    routines = routines,
                    categories = categories,
                    addableCount = addableCount,
                    selectedCategoryName = nextSelected,
                    errorMessage = null,
                )
            }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategoryName = category) }
    }

    fun onBackClick() {
        emitEvent(MyRoutineUiEvent.NavigateBack)
    }

    fun onCloseClick() {
        emitEvent(MyRoutineUiEvent.NavigateBack)
    }

    fun onAddRoutineClick() {
        emitEvent(MyRoutineUiEvent.NavigateToAddRoutine)
    }

    fun onUpdateRoutine(
        routineId: Long,
        name: String,
        endTime: String,
        selectedDayIndexes: Set<Int>,
        alarmTime: String?,
    ) {
        if (_uiState.value.isSaving) return
        val repeatDays = selectedDayIndexes
            .sorted()
            .mapNotNull { DayIndexToApi.getOrNull(it) }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            when (
                val result = updateMemberRoutineUseCase(
                    routineId = routineId,
                    update = UpdateMemberRoutine(
                        name = name,
                        endTime = endTime.ifBlank { "23:59" },
                        repeatDays = repeatDays,
                        alarmTime = alarmTime,
                    ),
                )
            ) {
                is ResultState.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            isSaving = false,
                            routines = state.routines.map { routine ->
                                if (routine.routineId == routineId) result.data else routine
                            },
                        )
                    }
                    emitEvent(MyRoutineUiEvent.EditCompleted)
                }
                is ResultState.Error -> _uiState.update {
                    it.copy(isSaving = false, errorMessage = result.message)
                }
                ResultState.Loading -> Unit
            }
        }
    }

    fun onDeleteRoutine(routineId: Long) {
        if (_uiState.value.isSaving) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            when (val result = deleteMemberRoutineUseCase(routineId)) {
                is ResultState.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            isSaving = false,
                            routines = state.routines.filterNot { it.routineId == routineId },
                        )
                    }
                    emitEvent(MyRoutineUiEvent.DeleteCompleted)
                }
                is ResultState.Error -> _uiState.update {
                    it.copy(isSaving = false, errorMessage = result.message)
                }
                ResultState.Loading -> Unit
            }
        }
    }

    fun onCreateCategory(name: String, color: CategoryColor?) {
        val trimmed = name.trim()
        if (trimmed.isEmpty() || trimmed.length > 10 || trimmed.contains('\n')) {
            _uiState.update { it.copy(errorMessage = "카테고리 이름은 1~10자로 입력해 주세요.") }
            return
        }
        if (trimmed == "전체") {
            _uiState.update { it.copy(errorMessage = "「전체」는 사용할 수 없는 이름이에요.") }
            return
        }
        if (!_uiState.value.addCategoryEnabled) {
            _uiState.update { it.copy(errorMessage = "카테고리는 최대 5개까지 추가할 수 있습니다.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingCategory = true, errorMessage = null) }
            when (
                val result = createRoutineCategoryUseCase(
                    name = trimmed,
                    color = color?.toApiColor(),
                )
            ) {
                is ResultState.Success -> {
                    _uiState.update { it.copy(isCreatingCategory = false) }
                    refresh()
                    emitEvent(MyRoutineUiEvent.CategorySaved)
                }
                is ResultState.Error -> _uiState.update {
                    it.copy(isCreatingCategory = false, errorMessage = result.message)
                }
                ResultState.Loading -> Unit
            }
        }
    }

    fun onUpdateCategory(categoryId: Long, name: String, color: CategoryColor?) {
        val trimmed = name.trim()
        if (trimmed.isEmpty() || trimmed.length > 10 || trimmed.contains('\n')) {
            _uiState.update { it.copy(errorMessage = "카테고리 이름은 1~10자로 입력해 주세요.") }
            return
        }
        if (trimmed == "전체") {
            _uiState.update { it.copy(errorMessage = "「전체」는 사용할 수 없는 이름이에요.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingCategory = true, errorMessage = null) }
            when (
                val result = updateRoutineCategoryUseCase(
                    categoryId = categoryId,
                    name = trimmed,
                    color = color?.toApiColor(),
                )
            ) {
                is ResultState.Success -> {
                    val updated = result.data
                    _uiState.update { state ->
                        state.copy(
                            isCreatingCategory = false,
                            selectedCategoryName = if (state.selectedCategoryName ==
                                state.categories.firstOrNull { it.categoryId == categoryId }?.name
                            ) {
                                updated.name
                            } else {
                                state.selectedCategoryName
                            },
                        )
                    }
                    refresh()
                    emitEvent(MyRoutineUiEvent.CategorySaved)
                }
                is ResultState.Error -> _uiState.update {
                    it.copy(isCreatingCategory = false, errorMessage = result.message)
                }
                ResultState.Loading -> Unit
            }
        }
    }

    fun onDeleteCategory(categoryId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingCategory = true, errorMessage = null) }
            when (val result = deleteRoutineCategoryUseCase(categoryId)) {
                is ResultState.Success -> {
                    _uiState.update { state ->
                        val deletedName = state.categories
                            .firstOrNull { it.categoryId == categoryId }
                            ?.name
                        state.copy(
                            isCreatingCategory = false,
                            selectedCategoryName = if (state.selectedCategoryName == deletedName) {
                                AllCategoryLabel
                            } else {
                                state.selectedCategoryName
                            },
                        )
                    }
                    refresh()
                    emitEvent(MyRoutineUiEvent.CategoryDeleted)
                }
                is ResultState.Error -> _uiState.update {
                    it.copy(isCreatingCategory = false, errorMessage = result.message)
                }
                ResultState.Loading -> Unit
            }
        }
    }

    fun categoryByName(name: String): RoutineCategory? =
        _uiState.value.categories.firstOrNull { it.name == name && !it.fixed }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun emitEvent(event: MyRoutineUiEvent) {
        viewModelScope.launch { _uiEvent.emit(event) }
    }
}

internal fun CreatedRoutine.toDayIndexes(): Set<Int> =
    repeatDays.mapNotNull { day -> DayIndexToApi.indexOf(day).takeIf { it >= 0 } }.toSet()

internal fun CreatedRoutine.deadlineDisplay(): String = endTime.toDueLabel()

internal fun CreatedRoutine.alarmDisplay(): String =
    alarmTime?.trim()?.takeIf { it.isNotEmpty() } ?: "없음"

internal fun CreatedRoutine.repeatDisplay(): String = repeatDays.toRepeatLabel()

private fun CreatedRoutine.toChecklistItem(): RoutineChecklistItem = RoutineChecklistItem(
    id = routineId.toString(),
    name = name,
    checked = completedToday,
    deadlineText = endTime.toDueLabel(),
    category = categoryName,
    repeatLabel = repeatDays.toRepeatLabel(),
)

private fun String?.toDueLabel(): String {
    val time = this?.trim().orEmpty()
    if (time.isEmpty()) return ""
    return if (time.startsWith("마감")) time else "마감 $time"
}

private fun List<String>.toRepeatLabel(): String {
    if (isEmpty() || size == 7) return "매일"
    val shorts = mapNotNull { day ->
        RepeatDayShortLabels.firstOrNull { it.first == day }?.second
    }
    if (shorts.isEmpty()) return ""
    if (shorts.toSet() == setOf("월", "화", "수", "목", "금")) return "주중"
    if (shorts.toSet() == setOf("토", "일")) return "주말"
    if (shorts.size == 1) return "${shorts.first()}요일마다"
    return shorts.joinToString(",")
}
