package com.li_routi.feature.home.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.common.ui.routine.CategoryColor
import com.li_routi.core.common.ui.routine.RoutineChecklistItem
import com.li_routi.core.common.ui.routine.toApiColor
import com.li_routi.core.domain.home.GetHomeSummaryUseCase
import com.li_routi.core.domain.routine.CreateMemberRoutinesUseCase
import com.li_routi.core.domain.routine.CreateRoutineCategoryUseCase
import com.li_routi.core.domain.routine.CreateRoutineItem
import com.li_routi.core.domain.routine.CreatedRoutine
import com.li_routi.core.domain.routine.DeleteMemberRoutineUseCase
import com.li_routi.core.domain.routine.DeleteRoutineCategoryUseCase
import com.li_routi.core.domain.routine.GetMemberRoutinesUseCase
import com.li_routi.core.domain.routine.GetRoutineCategoriesUseCase
import com.li_routi.core.domain.routine.GetRoutineTemplatesUseCase
import com.li_routi.core.domain.routine.RoutineCategory
import com.li_routi.core.domain.routine.RoutineCategoryName
import com.li_routi.core.domain.routine.RoutineTemplate
import com.li_routi.core.domain.routine.UpdateMemberRoutine
import com.li_routi.core.domain.routine.UpdateMemberRoutineUseCase
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
/** 이미 등록된 커스텀 루틴(templateId == null)을 체크리스트에 표시할 때 쓰는 id 접두사. */
const val RegisteredCustomIdPrefix = "routine_"

/**
 * 서버 규칙(POST /api/routines): "활성 루틴은 기존 개수와 요청 개수를 합해 최대 30개까지 등록할 수
 * 있습니다" (ROUTINE409_1). 초과분은 배치 전체가 롤백되므로, 제출 전에 클라이언트에서 미리 막는다.
 */
private const val MaxActiveRoutines = 30

/** 삭제 대상 해석용 — GET /routines · GET /home 양쪽에서 모은 등록 루틴. */
private data class RegisteredRoutineRef(
    val routineId: Long,
    val templateId: Long?,
    val categoryId: Long,
    val name: String,
)

/** 체크 해제된 템플릿 → 삭제할 member routineId. */
private data class DeleteTarget(
    val templateId: Long,
    val routineId: Long,
)

private sealed interface RegisteredLoadResult {
    data class Ok(
        val refs: List<RegisteredRoutineRef>,
        /** GET /api/routines 원본 — 커스텀 루틴 수정 시트 프리필용(repeatDays/endTime 포함). */
        val memberRoutines: List<CreatedRoutine>,
    ) : RegisteredLoadResult
    data class Failed(val message: String) : RegisteredLoadResult
}

data class RoutineManageUiState(
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val isMutatingCategory: Boolean = false,
    /** 이미 등록된 커스텀 루틴 수정/삭제 진행 중 여부(수정 시트의 확인 버튼 중복 탭 방지용). */
    val isMutatingCustomRoutine: Boolean = false,
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
    /**
     * 화면에 한 번이라도 alreadyAdded로 보인 템플릿 id.
     * 체크 해제 후 완료 시 삭제 후보를 고르는 기준(캐시 alreadyAdded 플래그만보다 안전).
     */
    val knownAddedTemplateIds: Set<Long> = emptySet(),
    /** templateId / custom id → 선택 여부 */
    val selectedIds: Set<String> = emptySet(),
    /** 사용자가 명시적으로 체크 해제한 id — 템플릿 재로드 시 다시 잠기지 않게 한다. */
    val userUncheckedIds: Set<String> = emptySet(),
    val customItems: List<CreateRoutineItem> = emptyList(),
    /** 이미 등록된 커스텀 루틴(templateId == null) — 체크리스트에 표시하고 탭하면 수정 시트를 연다. */
    val registeredCustomRoutines: List<CreatedRoutine> = emptyList(),
    /** 이번 화면 진입 시점 기준 이미 등록돼 있던 활성 루틴 수 (30개 한도 계산용). */
    val existingActiveRoutineCount: Int = 0,
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
            // 이미 등록된 커스텀 루틴 — 새로 만드는 항목이 아니므로 체크박스는 없고, 탭하면 수정 시트가 연다.
            val registered = registeredCustomRoutines.map { routine ->
                RoutineChecklistItem(
                    id = "$RegisteredCustomIdPrefix${routine.routineId}",
                    name = routine.name,
                    checked = true,
                    category = routine.categoryName,
                    selectable = false,
                    showCheckbox = false,
                    editable = true,
                )
            }
            return (templateItems + customs + registered).sortedByDescending { it.checked }
        }

    val allSelectableSelected: Boolean
        get() {
            val addable = addableIds
            return addable.isNotEmpty() && addable.all { it in selectedIds }
        }

    val selectedCount: Int
        get() = checklistItems.count { it.showCheckbox && it.checked }

    /**
     * 제출 시 실제로 새로 생성될 개수. [RoutineManageViewModel.buildCreatePayload]와 같은 기준으로 센다
     * (템플릿 캐시 기준 + 아직 등록되지 않은 것만) — 화면에 보이는 카테고리 밖에서 선택해둔 것도 포함.
     */
    val pendingCreateCount: Int
        get() {
            val newTemplateCount = templateCache.values.count { template ->
                template.templateId !in knownAddedTemplateIds &&
                    template.templateId.toString() in selectedIds
            }
            val newCustomCount = customItems.indices.count { "$CustomIdPrefix$it" in selectedIds }
            return newTemplateCount + newCustomCount
        }

    /**
     * 체크 해제된, 이미 등록된 템플릿 — [onSubmit]이 새로 만들기 전에 먼저 삭제하는 개수.
     * [hasDraftChanges]의 "잠긴 항목이 해제됐는지" 판단과 같은 기준([knownAddedTemplateIds] 중
     * [selectedIds]에서 빠진 것)이지만, 여기서는 실제 개수가 필요해 count로 센다.
     */
    val pendingDeleteCount: Int
        get() = knownAddedTemplateIds.count { it.toString() !in selectedIds }

    /**
     * 제출 시 서버에 남을 실제 활성 루틴 수. [onSubmit]은 체크 해제한 등록 루틴을 먼저 지운 뒤에
     * 새 루틴을 만들므로, 기존 개수에서 삭제 예정([pendingDeleteCount])을 빼고 생성 예정을 더해야 한다.
     */
    private val projectedActiveRoutineCount: Int
        get() = existingActiveRoutineCount - pendingDeleteCount + pendingCreateCount

    /** 기존 + 새로 생성될 개수가 서버 한도(30개)를 넘으면 안내 문구, 아니면 null. */
    val overLimitMessage: String?
        get() {
            val total = projectedActiveRoutineCount
            return "루틴은 최대 ${MaxActiveRoutines}개까지 등록할 수 있어요 (현재 ${total}개 선택됨)"
                .takeIf { total > MaxActiveRoutines }
        }

    /** 등록 중이 아니고, 제출하면 30개 한도를 넘지 않을 때만 완료 버튼 활성. */
    val canSubmit: Boolean
        get() = !isSubmitting && projectedActiveRoutineCount <= MaxActiveRoutines

    /**
     * 템플릿 선택·커스텀 추가·이미 등록된 항목 체크 해제 등 이탈 시 확인할 초안 변경.
     */
    val hasDraftChanges: Boolean
        get() {
            if (customItems.isNotEmpty()) return true
            val lockedIds = knownAddedTemplateIds.map { it.toString() }.toSet()
            if (lockedIds.any { it !in selectedIds }) return true
            return selectedIds.any { it !in lockedIds }
        }
}

sealed interface RoutineManageUiEvent {
    data object NavigateBack : RoutineManageUiEvent
    data object SubmitSuccess : RoutineManageUiEvent
    data object CategorySaved : RoutineManageUiEvent
    data object CategoryDeleted : RoutineManageUiEvent
    data object CustomRoutineSaved : RoutineManageUiEvent
    data object CustomRoutineDeleted : RoutineManageUiEvent
}

class RoutineManageViewModel(
    private val getRoutineCategoriesUseCase: GetRoutineCategoriesUseCase,
    private val createRoutineCategoryUseCase: CreateRoutineCategoryUseCase,
    private val updateRoutineCategoryUseCase: UpdateRoutineCategoryUseCase,
    private val deleteRoutineCategoryUseCase: DeleteRoutineCategoryUseCase,
    private val getRoutineTemplatesUseCase: GetRoutineTemplatesUseCase,
    private val getMemberRoutinesUseCase: GetMemberRoutinesUseCase,
    private val getHomeSummaryUseCase: GetHomeSummaryUseCase,
    private val createMemberRoutinesUseCase: CreateMemberRoutinesUseCase,
    private val updateMemberRoutineUseCase: UpdateMemberRoutineUseCase,
    private val deleteMemberRoutineUseCase: DeleteMemberRoutineUseCase,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(RoutineManageUiState())
    val uiState: StateFlow<RoutineManageUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<RoutineManageUiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<RoutineManageUiEvent> = _uiEvent.asSharedFlow()

    // loadTemplates가 새로 시작될 때마다 올라간다. 응답이 왔을 때 이 값이 그대로면(그 사이 더 최신
    // 요청이 시작되지 않았으면)만 결과를 반영한다 — 그렇지 않으면 카테고리를 빠르게 연달아 전환할 때
    // 먼저 쏜(느린) 요청의 응답이 나중에 도착해 최신 카테고리의 templates를 덮어쓸 수 있다.
    private var templatesGeneration = 0

    /** 제출 직전 재조회용 캐시. UiState에는 올리지 않는다. */
    private var registeredRoutineRefs: List<RegisteredRoutineRef> = emptyList()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val load = loadRegisteredRoutineRefs()) {
                is RegisteredLoadResult.Ok -> {
                    registeredRoutineRefs = load.refs
                    _uiState.update {
                        it.copy(
                            existingActiveRoutineCount = load.refs.size,
                            registeredCustomRoutines = load.memberRoutines.filter { r -> r.templateId == null },
                        )
                    }
                }
                is RegisteredLoadResult.Failed -> {
                    registeredRoutineRefs = emptyList()
                    // existingActiveRoutineCount/registeredCustomRoutines를 이전 값 그대로 두면,
                    // 실제로는 더 이상 없는(혹은 확인 못한) 커스텀 루틴이 체크리스트에 계속 보이고
                    // 탭하면 이제 무효한 routineId로 수정 시트가 열릴 수 있다 — 같이 비운다.
                    _uiState.update {
                        it.copy(existingActiveRoutineCount = 0, registeredCustomRoutines = emptyList())
                    }
                }
            }
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
            val unchecked = state.userUncheckedIds.toMutableSet()
            if (checked) {
                next.add(id)
                unchecked.remove(id)
            } else {
                next.remove(id)
                unchecked.add(id)
            }
            state.copy(selectedIds = next, userUncheckedIds = unchecked)
        }
    }

    fun onSelectAllChange(checked: Boolean) {
        _uiState.update { state ->
            val addableIds = state.addableIds
            val next = state.selectedIds.toMutableSet()
            val unchecked = state.userUncheckedIds.toMutableSet()
            if (checked) {
                next.addAll(addableIds)
                unchecked.removeAll(addableIds)
            } else {
                next.removeAll(addableIds)
                unchecked.addAll(addableIds)
            }
            state.copy(selectedIds = next, userUncheckedIds = unchecked)
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
        if (repeatDays.isNullOrEmpty()) {
            _uiState.update { it.copy(errorMessage = "반복 요일을 선택해 주세요.") }
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

    /** 이미 등록된 커스텀 루틴 수정. name/repeatDays 검증은 [UpdateMemberRoutineUseCase]가 한다. */
    fun onUpdateCustomRoutine(
        routineId: Long,
        name: String,
        endTime: String,
        repeatDays: List<String>,
    ) {
        if (_uiState.value.isMutatingCustomRoutine) return
        _uiState.update { it.copy(isMutatingCustomRoutine = true, errorMessage = null) }
        viewModelScope.launch {
            when (
                val result = updateMemberRoutineUseCase(
                    routineId = routineId,
                    update = UpdateMemberRoutine(name = name, endTime = endTime, repeatDays = repeatDays),
                )
            ) {
                is ResultState.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            isMutatingCustomRoutine = false,
                            registeredCustomRoutines = state.registeredCustomRoutines.map { routine ->
                                if (routine.routineId == routineId) result.data else routine
                            },
                        )
                    }
                    _uiEvent.emit(RoutineManageUiEvent.CustomRoutineSaved)
                }
                is ResultState.Error -> _uiState.update {
                    it.copy(isMutatingCustomRoutine = false, errorMessage = result.message)
                }
                ResultState.Loading -> Unit
            }
        }
    }

    /** 이미 등록된 커스텀 루틴 삭제(수정 시트의 삭제 버튼에서 호출). */
    fun onDeleteCustomRoutine(routineId: Long) {
        if (_uiState.value.isMutatingCustomRoutine) return
        _uiState.update { it.copy(isMutatingCustomRoutine = true, errorMessage = null) }
        viewModelScope.launch {
            when (val result = deleteMemberRoutineUseCase(routineId)) {
                is ResultState.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            isMutatingCustomRoutine = false,
                            registeredCustomRoutines = state.registeredCustomRoutines
                                .filterNot { it.routineId == routineId },
                            existingActiveRoutineCount = (state.existingActiveRoutineCount - 1)
                                .coerceAtLeast(0),
                        )
                    }
                    _uiEvent.emit(RoutineManageUiEvent.CustomRoutineDeleted)
                }
                is ResultState.Error -> _uiState.update {
                    it.copy(isMutatingCustomRoutine = false, errorMessage = result.message)
                }
                ResultState.Loading -> Unit
            }
        }
    }

    fun onSubmit() {
        val state = _uiState.value
        if (state.isSubmitting) return
        // 빈 payload NavigateBack 포함, 연타로 pop이 여러 번 나가지 않도록 동기 가드.
        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

        viewModelScope.launch {
            // 삭제 매핑은 제출 직전 최신 등록 목록으로 다시 잡는다(/routines + /home).
            when (val load = loadRegisteredRoutineRefs()) {
                is RegisteredLoadResult.Failed -> {
                    _uiState.update {
                        it.copy(isSubmitting = false, errorMessage = load.message)
                    }
                    return@launch
                }
                is RegisteredLoadResult.Ok -> {
                    registeredRoutineRefs = load.refs
                    _uiState.update {
                        it.copy(
                            existingActiveRoutineCount = load.refs.size,
                            registeredCustomRoutines = load.memberRoutines.filter { r -> r.templateId == null },
                        )
                    }
                }
            }
            val registered = registeredRoutineRefs
            val latest = _uiState.value

            val toCreate = buildCreatePayload(latest)
            val uncheckedAdded = latest.knownAddedTemplateIds
                .map { it.toString() }
                .filter { it !in latest.selectedIds }
            val toDelete = buildDeleteTargets(latest, registered)

            if (uncheckedAdded.isNotEmpty() && toDelete.isEmpty()) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = "삭제할 루틴을 찾지 못했습니다. 다시 시도해 주세요.",
                    )
                }
                return@launch
            }

            if (toCreate.isEmpty() && toDelete.isEmpty()) {
                _uiState.update { it.copy(isSubmitting = false) }
                _uiEvent.emit(RoutineManageUiEvent.NavigateBack)
                return@launch
            }

            for (target in toDelete) {
                when (val result = deleteMemberRoutineUseCase(target.routineId)) {
                    is ResultState.Success -> {
                        // 일부만 성공하고 이후 실패해도, 이미 지운 템플릿은 추적 집합에서 빼
                        // 재제출 시 "삭제할 루틴을 찾지 못함"으로 막히지 않게 한다.
                        _uiState.update { state ->
                            val tid = target.templateId.toString()
                            state.copy(
                                knownAddedTemplateIds = state.knownAddedTemplateIds - target.templateId,
                                userUncheckedIds = state.userUncheckedIds - tid,
                            )
                        }
                    }
                    is ResultState.Error -> {
                        _uiState.update {
                            it.copy(isSubmitting = false, errorMessage = result.message)
                        }
                        return@launch
                    }
                    ResultState.Loading -> Unit
                }
            }
            if (toCreate.isEmpty()) {
                _uiState.update { it.copy(isSubmitting = false) }
                _uiEvent.emit(RoutineManageUiEvent.SubmitSuccess)
                return@launch
            }
            when (val result = createMemberRoutinesUseCase(toCreate)) {
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
                val addedIds = result.data
                    .asSequence()
                    .filter { it.alreadyAdded }
                    .map { it.templateId }
                    .toSet()
                val lockedIds = addedIds.map { it.toString() }.toSet()
                _uiState.update { state ->
                    // 사용자가 이미 해제한 항목은 템플릿 재로드로 다시 체크되지 않게 한다.
                    val mergedSelected = (state.selectedIds + lockedIds) - state.userUncheckedIds
                    state.copy(
                        isLoading = false,
                        templates = result.data,
                        templateCache = state.templateCache + result.data.associateBy { it.templateId },
                        knownAddedTemplateIds = state.knownAddedTemplateIds + addedIds,
                        selectedIds = mergedSelected,
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

    /** /api/routines + /api/home 의 내 루틴을 합쳐 삭제 매핑에 쓴다. */
    private suspend fun loadRegisteredRoutineRefs(): RegisteredLoadResult {
        val memberResult = getMemberRoutinesUseCase()
        val homeResult = getHomeSummaryUseCase()

        val fromMember = when (memberResult) {
            is ResultState.Success -> memberResult.data.routines.map {
                RegisteredRoutineRef(
                    routineId = it.routineId,
                    templateId = it.templateId,
                    categoryId = it.categoryId,
                    name = it.name,
                )
            }
            else -> null
        }
        val fromHome = when (homeResult) {
            is ResultState.Success -> homeResult.data.myRoutines.map {
                RegisteredRoutineRef(
                    routineId = it.routineId,
                    templateId = it.templateId,
                    categoryId = it.categoryId,
                    name = it.name,
                )
            }
            else -> null
        }

        if (fromMember == null && fromHome == null) {
            val message = (memberResult as? ResultState.Error)?.message
                ?: (homeResult as? ResultState.Error)?.message
                ?: "루틴 목록을 불러오지 못했습니다. 다시 시도해 주세요."
            return RegisteredLoadResult.Failed(message)
        }

        return RegisteredLoadResult.Ok(
            refs = (fromMember.orEmpty() + fromHome.orEmpty()).distinctBy { it.routineId },
            memberRoutines = (memberResult as? ResultState.Success)?.data?.routines.orEmpty(),
        )
    }

    private fun buildCreatePayload(state: RoutineManageUiState): List<CreateRoutineItem> {
        // 현재 화면에 보이는 카테고리(state.templates)가 아니라 templateCache에서 가져온다 —
        // 그래야 다른 카테고리를 보던 중 선택해둔 템플릿도 최종 payload에서 빠지지 않는다.
        val fromTemplates = state.templateCache.values
            .filter { template ->
                val id = template.templateId.toString()
                template.templateId !in state.knownAddedTemplateIds && id in state.selectedIds
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

    /**
     * 이미 등록돼 있던 템플릿을 체크 해제한 항목의 삭제 대상.
     * templateId 우선, 없으면 name+categoryId로 /routines·/home 스냅샷에서 찾는다.
     * 이름 fallback은 아직 선택된 템플릿이 소유한 routine / 이미 다른 삭제에 쓰인 routine을 쓰지 않는다.
     */
    private fun buildDeleteTargets(
        state: RoutineManageUiState,
        registered: List<RegisteredRoutineRef>,
    ): List<DeleteTarget> {
        val selectedTemplateIds = state.selectedIds.mapNotNull { it.toLongOrNull() }.toSet()
        val protectedRoutineIds = registered
            .asSequence()
            .filter { ref -> ref.templateId != null && ref.templateId in selectedTemplateIds }
            .map { it.routineId }
            .toSet()
        val claimedRoutineIds = mutableSetOf<Long>()

        return state.knownAddedTemplateIds
            .asSequence()
            .filter { templateId -> templateId.toString() !in state.selectedIds }
            .mapNotNull { templateId ->
                val template = state.templateCache[templateId]
                val byTemplateId = registered.firstOrNull { it.templateId == templateId }?.routineId
                val byNameFallback = if (byTemplateId == null) {
                    template?.let { t ->
                        registered.firstOrNull { ref ->
                            ref.name == t.name &&
                                ref.categoryId == t.categoryId &&
                                ref.routineId !in protectedRoutineIds &&
                                ref.routineId !in claimedRoutineIds &&
                                (ref.templateId == null || ref.templateId !in selectedTemplateIds)
                        }?.routineId
                    }
                } else {
                    null
                }
                val routineId = byTemplateId ?: byNameFallback ?: return@mapNotNull null
                if (!claimedRoutineIds.add(routineId)) return@mapNotNull null
                DeleteTarget(templateId = templateId, routineId = routineId)
            }
            .toList()
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
        .map { it.templateId }
        .toSet()
    val removedTemplateIdStrings = removedTemplateIds.map { it.toString() }.toSet()
    val survivingIndexed = customItems.withIndex()
        .filter { it.value.categoryId != deletedCategoryId }
    val newCustomItems = survivingIndexed.map { it.value }
    val nonCustomSelectedIds = selectedIds.filterNot {
        it.startsWith(CustomIdPrefix) || it in removedTemplateIdStrings
    }
    val newCustomSelectedIds = survivingIndexed.mapIndexedNotNull { newIndex, indexed ->
        "$CustomIdPrefix$newIndex".takeIf { "$CustomIdPrefix${indexed.index}" in selectedIds }
    }
    return copy(
        templateCache = templateCache.filterValues { it.categoryId != deletedCategoryId },
        knownAddedTemplateIds = knownAddedTemplateIds - removedTemplateIds,
        customItems = newCustomItems,
        selectedIds = (nonCustomSelectedIds + newCustomSelectedIds).toSet(),
        userUncheckedIds = userUncheckedIds - removedTemplateIdStrings,
    )
}
