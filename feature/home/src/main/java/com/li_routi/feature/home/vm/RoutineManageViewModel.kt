package com.li_routi.feature.home.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.common.ui.routine.CategoryColor
import com.li_routi.core.common.ui.routine.RoutineChecklistItem
import com.li_routi.core.common.ui.routine.toApiColor
import com.li_routi.core.domain.routine.CreateMemberRoutinesUseCase
import com.li_routi.core.domain.routine.CreateRoutineCategoryUseCase
import com.li_routi.core.domain.routine.CreateRoutineItem
import com.li_routi.core.domain.routine.DeleteRoutineCategoryUseCase
import com.li_routi.core.domain.routine.GetRoutineCategoriesUseCase
import com.li_routi.core.domain.routine.GetRoutineTemplatesUseCase
import com.li_routi.core.domain.routine.RoutineCategory
import com.li_routi.core.domain.routine.RoutineCategoryName
import com.li_routi.core.domain.routine.RoutineTemplate
import com.li_routi.core.domain.routine.UpdateRoutineCategoryUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val AllCategoryLabel = "전체"
private const val CustomIdPrefix = "custom_"

data class RoutineManageUiState(
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val isMutatingCategory: Boolean = false,
    val categories: List<RoutineCategory> = emptyList(),
    val addableCount: Int = 0,
    val selectedCategoryName: String = AllCategoryLabel,
    val templates: List<RoutineTemplate> = emptyList(),
    /**
     * 지금까지 로드된 모든 템플릿(카테고리 전환으로 [templates]가 통째로 바뀌어도 유지되는 캐시).
     * templateId → RoutineTemplate. 최종 제출 payload는 (화면에 보이는 현재 카테고리뿐 아니라)
     * 다른 카테고리를 보다가 선택해둔 항목까지 포함해야 하므로 여기서 데이터를 가져온다.
     */
    val templateCache: Map<Long, RoutineTemplate> = emptyMap(),
    /** templateId / custom id → 선택 여부 */
    val selectedIds: Set<String> = emptySet(),
    val customItems: List<CreateRoutineItem> = emptyList(),
    val errorMessage: String? = null,
    val categoryNameError: String? = null,
) {
    val categoryLabels: List<String>
        get() = listOf(AllCategoryLabel) + categories.map { it.name }

    val addCategoryEnabled: Boolean
        get() = addableCount > 0

    val selectedCategoryId: Long?
        get() = categories.firstOrNull { it.name == selectedCategoryName }?.categoryId

    /** 아직 내 루틴에 없는(새로 추가 가능한) 항목 id. */
    val addableIds: Set<String>
        get() {
            val templateIds = templates
                .asSequence()
                .filter { !it.alreadyAdded }
                .map { it.templateId.toString() }
            val customIds = customItems.indices.asSequence().map { "$CustomIdPrefix$it" }
            return (templateIds + customIds).toSet()
        }

    val checklistItems: List<RoutineChecklistItem>
        get() {
            val templateItems = templates.map { template ->
                val id = template.templateId.toString()
                RoutineChecklistItem(
                    id = id,
                    name = template.name,
                    // 선택 여부는 selectedIds만 본다. alreadyAdded도 탭으로 해제 가능해야 한다.
                    checked = id in selectedIds,
                    category = template.categoryName,
                    selectable = true,
                )
            }
            val customs = customItems.mapIndexed { index, item ->
                val id = "$CustomIdPrefix$index"
                RoutineChecklistItem(
                    id = id,
                    name = item.name,
                    checked = id in selectedIds,
                    category = categories.firstOrNull { it.categoryId == item.categoryId }?.name
                        .orEmpty(),
                    selectable = true,
                )
            }
            return templateItems + customs
        }

    val allSelectableSelected: Boolean
        get() {
            val addable = addableIds
            return addable.isNotEmpty() && addable.all { it in selectedIds }
        }

    val selectedCount: Int
        get() = checklistItems.count { it.checked }

    /** 등록 중이 아니면 완료 버튼 활성 (선택 없어도 탭 가능). */
    val canSubmit: Boolean
        get() = !isSubmitting

    /**
     * 템플릿 선택·커스텀 추가 등 이탈 시 확인할 초안 변경.
     * alreadyAdded로 잠긴 기본 선택은 제외한다.
     */
    val hasDraftChanges: Boolean
        get() {
            if (customItems.isNotEmpty()) return true
            val lockedIds = templates
                .asSequence()
                .filter { it.alreadyAdded }
                .map { it.templateId.toString() }
                .toSet()
            return selectedIds.any { it !in lockedIds }
        }
}

sealed interface RoutineManageUiEvent {
    data object NavigateBack : RoutineManageUiEvent
    data object SubmitSuccess : RoutineManageUiEvent
    data object CategorySaved : RoutineManageUiEvent
    data object CategoryDeleted : RoutineManageUiEvent
}

class RoutineManageViewModel(
    private val getRoutineCategoriesUseCase: GetRoutineCategoriesUseCase,
    private val createRoutineCategoryUseCase: CreateRoutineCategoryUseCase,
    private val updateRoutineCategoryUseCase: UpdateRoutineCategoryUseCase,
    private val deleteRoutineCategoryUseCase: DeleteRoutineCategoryUseCase,
    private val getRoutineTemplatesUseCase: GetRoutineTemplatesUseCase,
    private val createMemberRoutinesUseCase: CreateMemberRoutinesUseCase,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(RoutineManageUiState())
    val uiState: StateFlow<RoutineManageUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<RoutineManageUiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<RoutineManageUiEvent> = _uiEvent.asSharedFlow()

    // loadTemplates가 새로 시작될 때마다 올라간다. 응답이 왔을 때 이 값이 그대로면(그 사이 더 최신
    // 요청이 시작되지 않았으면)만 결과를 반영한다 — 그렇지 않으면 카테고리를 빠르게 연달아 전환할 때
    // 먼저 쏜(느린) 요청의 응답이 나중에 도착해 최신 카테고리의 templates를 덮어쓸 수 있다.
    private var templatesGeneration = 0

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val categories = getRoutineCategoriesUseCase()) {
                is ResultState.Success -> {
                    _uiState.update {
                        it.copy(
                            categories = categories.data.categories,
                            addableCount = categories.data.addableCount,
                        )
                    }
                    loadTemplates(categoryId = _uiState.value.selectedCategoryId)
                }
                is ResultState.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = categories.message)
                }
                ResultState.Loading -> Unit
            }
        }
    }

    fun onCategorySelected(name: String) {
        if (name == _uiState.value.selectedCategoryName) return
        _uiState.update { it.copy(selectedCategoryName = name) }
        viewModelScope.launch {
            loadTemplates(categoryId = _uiState.value.selectedCategoryId)
        }
    }

    fun onItemCheckedChange(id: String, checked: Boolean) {
        _uiState.update { state ->
            val next = state.selectedIds.toMutableSet()
            if (checked) next.add(id) else next.remove(id)
            state.copy(selectedIds = next)
        }
    }

    fun onSelectAllChange(checked: Boolean) {
        _uiState.update { state ->
            val addableIds = state.addableIds
            val next = state.selectedIds.toMutableSet()
            if (checked) next.addAll(addableIds) else next.removeAll(addableIds)
            state.copy(selectedIds = next)
        }
    }

    fun onCreateCategory(name: String, color: CategoryColor?) {
        if (_uiState.value.isMutatingCategory) return
        val error = RoutineCategoryName.validate(name).onValid { trimmed ->
            viewModelScope.launch {
                _uiState.update { it.copy(isMutatingCategory = true, categoryNameError = null, errorMessage = null) }
                when (
                    val result = createRoutineCategoryUseCase(
                        name = trimmed,
                        color = color?.toApiColor(),
                    )
                ) {
                    is ResultState.Success -> {
                        val created = result.data
                        when (val categories = getRoutineCategoriesUseCase()) {
                            is ResultState.Success -> {
                                _uiState.update {
                                    it.copy(
                                        isMutatingCategory = false,
                                        categories = categories.data.categories,
                                        addableCount = categories.data.addableCount,
                                        selectedCategoryName = created.name,
                                    )
                                }
                                loadTemplates(categoryId = created.categoryId)
                                _uiEvent.emit(RoutineManageUiEvent.CategorySaved)
                            }
                            is ResultState.Error -> _uiState.update {
                                it.copy(isMutatingCategory = false, errorMessage = categories.message)
                            }
                            ResultState.Loading -> Unit
                        }
                    }
                    is ResultState.Error -> _uiState.update {
                        it.copy(
                            isMutatingCategory = false,
                            categoryNameError = result.message,
                            errorMessage = result.message,
                        )
                    }
                    ResultState.Loading -> Unit
                }
            }
        }
        if (error != null) {
            _uiState.update { it.copy(categoryNameError = error) }
        }
    }

    fun onUpdateCategory(categoryId: Long, name: String, color: CategoryColor?) {
        if (_uiState.value.isMutatingCategory) return
        val error = RoutineCategoryName.validate(name).onValid { trimmed ->
            viewModelScope.launch {
                _uiState.update {
                    it.copy(isMutatingCategory = true, categoryNameError = null, errorMessage = null)
                }
                when (
                    val result = updateRoutineCategoryUseCase(
                        categoryId = categoryId,
                        name = trimmed,
                        color = color?.toApiColor(),
                    )
                ) {
                    is ResultState.Success -> {
                        val updated = result.data
                        when (val categories = getRoutineCategoriesUseCase()) {
                            is ResultState.Success -> {
                                _uiState.update { state ->
                                    state.copy(
                                        isMutatingCategory = false,
                                        categories = categories.data.categories,
                                        addableCount = categories.data.addableCount,
                                        selectedCategoryName = if (
                                            state.categories.any {
                                                it.categoryId == categoryId &&
                                                    it.name == state.selectedCategoryName
                                            }
                                        ) {
                                            updated.name
                                        } else {
                                            state.selectedCategoryName
                                        },
                                    )
                                }
                                loadTemplates(categoryId = _uiState.value.selectedCategoryId)
                                _uiEvent.emit(RoutineManageUiEvent.CategorySaved)
                            }
                            is ResultState.Error -> _uiState.update {
                                it.copy(isMutatingCategory = false, errorMessage = categories.message)
                            }
                            ResultState.Loading -> Unit
                        }
                    }
                    is ResultState.Error -> _uiState.update {
                        it.copy(
                            isMutatingCategory = false,
                            categoryNameError = result.message,
                            errorMessage = result.message,
                        )
                    }
                    ResultState.Loading -> Unit
                }
            }
        }
        if (error != null) {
            _uiState.update { it.copy(categoryNameError = error) }
        }
    }

    fun onDeleteCategory(categoryId: Long) {
        if (_uiState.value.isMutatingCategory) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(isMutatingCategory = true, errorMessage = null, categoryNameError = null)
            }
            when (val result = deleteRoutineCategoryUseCase(categoryId)) {
                is ResultState.Success -> {
                    val wasSelected = _uiState.value.categories
                        .firstOrNull { it.categoryId == categoryId }
                        ?.name == _uiState.value.selectedCategoryName
                    when (val categories = getRoutineCategoriesUseCase()) {
                        is ResultState.Success -> {
                            _uiState.update {
                                it.copy(
                                    isMutatingCategory = false,
                                    categories = categories.data.categories,
                                    addableCount = categories.data.addableCount,
                                    selectedCategoryName = if (wasSelected) {
                                        AllCategoryLabel
                                    } else {
                                        it.selectedCategoryName
                                    },
                                ).withCategoryRemoved(categoryId)
                            }
                            loadTemplates(categoryId = _uiState.value.selectedCategoryId)
                            _uiEvent.emit(RoutineManageUiEvent.CategoryDeleted)
                        }
                        is ResultState.Error -> _uiState.update {
                            it.copy(isMutatingCategory = false, errorMessage = categories.message)
                        }
                        ResultState.Loading -> Unit
                    }
                }
                is ResultState.Error -> _uiState.update {
                    it.copy(isMutatingCategory = false, errorMessage = result.message)
                }
                ResultState.Loading -> Unit
            }
        }
    }

    fun categoryByName(name: String): RoutineCategory? =
        _uiState.value.categories.firstOrNull { it.name == name && !it.fixed }

    fun onAddCustomRoutine(
        name: String,
        categoryId: Long?,
        endTime: String? = null,
        repeatDays: List<String>? = null,
    ): Boolean {
        val trimmed = name.trim()
        val targetCategoryId = categoryId
            ?: _uiState.value.selectedCategoryId
            ?: _uiState.value.categories.firstOrNull()?.categoryId
            ?: return false
        if (trimmed.isEmpty() || trimmed.length > 20 || trimmed.contains('\n')) {
            _uiState.update { it.copy(errorMessage = "루틴 이름은 1~20자로 입력해 주세요.") }
            return false
        }
        val customId = "$CustomIdPrefix${_uiState.value.customItems.size}"
        _uiState.update { state ->
            state.copy(
                customItems = state.customItems + CreateRoutineItem(
                    categoryId = targetCategoryId,
                    templateId = null,
                    name = trimmed,
                    endTime = endTime,
                    repeatDays = repeatDays?.takeIf { it.isNotEmpty() },
                ),
                selectedIds = state.selectedIds + customId,
                errorMessage = null,
            )
        }
        return true
    }

    fun onSubmit() {
        val state = _uiState.value
        if (state.isSubmitting) return
        // 빈 payload NavigateBack 포함, 연타로 pop이 여러 번 나가지 않도록 동기 가드.
        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

        val payload = buildCreatePayload(state)
        if (payload.isEmpty()) {
            // 새로 추가할 선택이 없으면 저장 없이 완료(뒤로가기).
            _uiState.update { it.copy(isSubmitting = false) }
            viewModelScope.launch { _uiEvent.emit(RoutineManageUiEvent.NavigateBack) }
            return
        }
        viewModelScope.launch {
            when (val result = createMemberRoutinesUseCase(payload)) {
                is ResultState.Success -> {
                    _uiState.update { it.copy(isSubmitting = false) }
                    _uiEvent.emit(RoutineManageUiEvent.SubmitSuccess)
                }
                is ResultState.Error -> _uiState.update {
                    it.copy(isSubmitting = false, errorMessage = result.message)
                }
                ResultState.Loading -> Unit
            }
        }
    }

    fun onBack() {
        viewModelScope.launch { _uiEvent.emit(RoutineManageUiEvent.NavigateBack) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null, categoryNameError = null) }
    }

    private suspend fun loadTemplates(categoryId: Long?) {
        templatesGeneration++
        val generation = templatesGeneration
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        when (val result = getRoutineTemplatesUseCase(categoryId)) {
            is ResultState.Success -> {
                if (generation != templatesGeneration) return
                val lockedIds = result.data
                    .filter { it.alreadyAdded }
                    .map { it.templateId.toString() }
                    .toSet()
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        templates = result.data,
                        // 카테고리 전환으로 templates가 통째로 바뀌어도, 이전에 로드했던 템플릿(다른
                        // 카테고리 것 포함)은 캐시에 남겨 선택 상태/최종 payload가 유지되게 한다.
                        templateCache = state.templateCache + result.data.associateBy { it.templateId },
                        selectedIds = state.selectedIds + lockedIds,
                    )
                }
            }
            is ResultState.Error -> {
                if (generation != templatesGeneration) return
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message, templates = emptyList())
                }
            }
            ResultState.Loading -> Unit
        }
    }

    private fun buildCreatePayload(state: RoutineManageUiState): List<CreateRoutineItem> {
        // 현재 화면에 보이는 카테고리(state.templates)가 아니라 templateCache에서 가져온다 —
        // 그래야 다른 카테고리를 보던 중 선택해둔 템플릿도 최종 payload에서 빠지지 않는다.
        val fromTemplates = state.templateCache.values
            .filter { template ->
                val id = template.templateId.toString()
                !template.alreadyAdded && id in state.selectedIds
            }
            .map { template ->
                CreateRoutineItem(
                    categoryId = template.categoryId,
                    templateId = template.templateId,
                    name = template.name,
                )
            }
        val fromCustoms = state.customItems.mapIndexedNotNull { index, item ->
            val id = "$CustomIdPrefix$index"
            if (id in state.selectedIds) item else null
        }
        return fromTemplates + fromCustoms
    }
}

/**
 * 삭제된 카테고리를 참조하는, 아직 제출하지 않은 커스텀 루틴을 정리한다.
 * 커스텀 id(`custom_N`)가 리스트 인덱스 기반이라, 남은 항목의 selectedIds도 새 인덱스에
 * 맞춰 다시 계산해야 한다(그대로 두면 삭제 후 인덱스가 밀리면서 엉뚱한 항목이 선택된 것처럼 보일
 * 수 있다).
 */
private fun RoutineManageUiState.withCategoryRemoved(deletedCategoryId: Long): RoutineManageUiState {
    val removedTemplateIds = templateCache.values
        .filter { it.categoryId == deletedCategoryId }
        .map { it.templateId.toString() }
        .toSet()
    val survivingIndexed = customItems.withIndex()
        .filter { it.value.categoryId != deletedCategoryId }
    val newCustomItems = survivingIndexed.map { it.value }
    val nonCustomSelectedIds = selectedIds.filterNot {
        it.startsWith(CustomIdPrefix) || it in removedTemplateIds
    }
    val newCustomSelectedIds = survivingIndexed.mapIndexedNotNull { newIndex, indexed ->
        "$CustomIdPrefix$newIndex".takeIf { "$CustomIdPrefix${indexed.index}" in selectedIds }
    }
    return copy(
        templateCache = templateCache.filterValues { it.categoryId != deletedCategoryId },
        customItems = newCustomItems,
        selectedIds = (nonCustomSelectedIds + newCustomSelectedIds).toSet(),
    )
}
