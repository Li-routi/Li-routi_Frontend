package com.li_routi.feature.home.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.common.ui.routine.CategoryColor
import com.li_routi.core.common.ui.routine.toApiColor
import com.li_routi.core.data.di.ShopContainer
import com.li_routi.core.domain.home.GetHomeSummaryUseCase
import com.li_routi.core.domain.shop.GetMyAvatarUseCase
import com.li_routi.feature.home.component.equippedImageUrlsOf
import com.li_routi.core.domain.routine.CreateRoutineCategoryUseCase
import com.li_routi.core.domain.routine.DeleteRoutineCategoryUseCase
import com.li_routi.core.domain.routine.GetRoutineCategoriesUseCase
import com.li_routi.core.domain.routine.RoutineCategory
import com.li_routi.core.domain.routine.RoutineCategoryName
import com.li_routi.core.domain.routine.UpdateRoutineCategoryUseCase
import com.li_routi.feature.home.navigation.HomeScreenActions
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 홈 화면 ViewModel.
 *
 * - [uiState]: 화면 렌더링에 필요한 상태 (루틴/그룹방 유무, 리스트 등)
 * - [uiEvent]: 클릭으로 발생하는 일회성 이벤트 (네비게이션 등). [HomeRoute]에서 collect한다.
 *
 * [HomeScreenActions]를 구현해 Screen의 버튼 이벤트를 여기서 처리한다.
 * 진입 시 [GetHomeSummaryUseCase]로 홈 요약을 조회한다.
 */
class HomeViewModel(
    private val getHomeSummaryUseCase: GetHomeSummaryUseCase,
    private val createRoutineCategoryUseCase: CreateRoutineCategoryUseCase,
    private val getRoutineCategoriesUseCase: GetRoutineCategoriesUseCase,
    private val updateRoutineCategoryUseCase: UpdateRoutineCategoryUseCase,
    private val deleteRoutineCategoryUseCase: DeleteRoutineCategoryUseCase,
    private val getMyAvatarUseCase: GetMyAvatarUseCase = ShopContainer.getMyAvatarUseCase,
    initialState: HomeUiState = HomeUiState(isLoading = true),
) : BaseViewModel(), HomeScreenActions {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<HomeUiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<HomeUiEvent> = _uiEvent.asSharedFlow()

    init {
        refresh()
        loadAvatar()
    }

    /** 홈 캐릭터에 입힐 착장. 상점에서 갈아입고 오면 다시 불러야 해서 refresh와 따로 둠 */
    fun loadAvatar() {
        viewModelScope.launch {
            val result = getMyAvatarUseCase()
            if (result is ResultState.Success) {
                val urls = equippedImageUrlsOf(
                    result.data.equipped.associate { it.slot to it.imageUrl },
                )
                _uiState.update { it.copy(equippedImageUrls = urls) }
            }
        }
    }

    /** 홈 요약을 다시 불러온다. 인증 업로드 성공 후 등에서 호출한다. */
    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadError = false) }
            val summaryDeferred = async { getHomeSummaryUseCase() }
            val categoriesDeferred = async { getRoutineCategoriesUseCase() }
            when (val result = summaryDeferred.await()) {
                is ResultState.Success -> {
                    var next = result.data.toHomeUiState()
                    when (val categoriesResult = categoriesDeferred.await()) {
                        is ResultState.Success -> {
                            val categories = categoriesResult.data.categories
                            val colorById = categories.associate { it.categoryId to it.color }
                            next = next.copy(
                                myCategories = categories,
                                addableCategoryCount = categoriesResult.data.addableCount,
                                myRoutineFilters = mergeCategoryFilters(
                                    routineFilters = next.myRoutineFilters,
                                    categories = categories,
                                    hasActiveRoutine = next.hasActiveRoutine,
                                ),
                                myRoutineItems = next.myRoutineItems.map { item ->
                                    item.withCategoryColor(colorById)
                                },
                                groupRoomItems = next.groupRoomItems.map { item ->
                                    item.withCategoryColor(colorById)
                                },
                            )
                        }
                        is ResultState.Error, ResultState.Loading -> Unit
                    }
                    _uiState.value = next
                }
                is ResultState.Error -> {
                    _uiState.update { it.copy(isLoading = false, loadError = true) }
                }
                ResultState.Loading -> Unit
            }
        }
    }

    override fun onNotificationClick() {
        emitEvent(HomeUiEvent.NavigateToNotification)
    }

    override fun onNavigateToShop() {
        emitEvent(HomeUiEvent.NavigateToShop)
    }

    override fun onMyRoutineClick() {
        emitEvent(HomeUiEvent.NavigateToMyRoutine)
    }

    override fun onRoutineCameraClick(routineId: String) {
        emitEvent(HomeUiEvent.NavigateToRoutineAuthCameraWithId(routineId))
    }

    override fun onSwipeToVerification() {
        emitEvent(HomeUiEvent.NavigateToRoutineAuthCamera)
    }

    override fun onManageMyRoutineClick() {
        emitEvent(HomeUiEvent.NavigateToManageMyRoutine)
    }

    override fun onCreateRoomClick() {
        emitEvent(HomeUiEvent.NavigateToCreateRoom)
    }

    override fun onJoinRoomWithInviteCodeClick() {
        emitEvent(HomeUiEvent.NavigateToJoinRoomWithInviteCode)
    }

    override fun onRetryLoadClick() {
        refresh()
    }

    override fun onCreateCategory(name: String, color: CategoryColor?) {
        if (_uiState.value.isMutatingCategory) return
        val error = RoutineCategoryName.validate(name).onValid { trimmed ->
            viewModelScope.launch {
                _uiState.update { it.copy(isMutatingCategory = true) }
                when (
                    val result = createRoutineCategoryUseCase(
                        name = trimmed,
                        color = color?.toApiColor(),
                    )
                ) {
                    is ResultState.Success -> {
                        appendMyCategoryFilter(trimmed)
                        _uiState.update { state ->
                            state.copy(
                                isMutatingCategory = false,
                                myCategories = state.myCategories + result.data,
                                addableCategoryCount = (state.addableCategoryCount - 1).coerceAtLeast(0),
                            )
                        }
                        emitEvent(HomeUiEvent.CategoryCreated)
                    }
                    is ResultState.Error -> {
                        _uiState.update { it.copy(isMutatingCategory = false) }
                        emitEvent(HomeUiEvent.CategoryCreateFailed(result.message))
                    }
                    ResultState.Loading -> Unit
                }
            }
        }
        if (error != null) {
            emitEvent(HomeUiEvent.CategoryCreateFailed(error))
        }
    }

    override fun onUpdateCategory(categoryId: Long, name: String, color: CategoryColor?) {
        val error = RoutineCategoryName.validate(name).onValid { trimmed ->
            viewModelScope.launch {
                when (
                    val result = updateRoutineCategoryUseCase(
                        categoryId = categoryId,
                        name = trimmed,
                        color = color?.toApiColor(),
                    )
                ) {
                    is ResultState.Success -> {
                        refresh()
                        emitEvent(HomeUiEvent.CategoryUpdated)
                    }
                    is ResultState.Error -> emitEvent(HomeUiEvent.CategoryUpdateFailed(result.message))
                    ResultState.Loading -> Unit
                }
            }
        }
        if (error != null) {
            emitEvent(HomeUiEvent.CategoryUpdateFailed(error))
        }
    }

    override fun onDeleteCategory(categoryId: Long) {
        viewModelScope.launch {
            when (val result = deleteRoutineCategoryUseCase(categoryId)) {
                is ResultState.Success -> {
                    refresh()
                    emitEvent(HomeUiEvent.CategoryDeleted)
                }
                is ResultState.Error -> emitEvent(HomeUiEvent.CategoryDeleteFailed(result.message))
                ResultState.Loading -> Unit
            }
        }
    }

    fun editableCategoryByName(name: String): RoutineCategory? =
        _uiState.value.myCategories.firstOrNull { it.name == name && !it.fixed }

    /**
     * 새로 만든 카테고리를 개인 루틴 필터 chip에만 붙인다.
     * 그룹 탭 필터는 방 이름(Figma)이라 카테고리 추가와 무관하다. 이미 있으면 무시. "전체"는 예약어.
     */
    private fun appendMyCategoryFilter(categoryName: String) {
        if (categoryName == "전체") return
        _uiState.update { state ->
            state.copy(
                myRoutineFilters = state.myRoutineFilters.withAppendedCategory(categoryName),
            )
        }
    }

    private fun List<String>.withAppendedCategory(categoryName: String): List<String> {
        val filters = toMutableList()
        if (filters.none { it == "전체" }) {
            filters.add(0, "전체")
        }
        if (filters.none { it == categoryName }) {
            filters.add(categoryName)
        }
        return filters
    }

    /**
     * 개발/Preview용 상태 전환. 실제 데이터 연동 시 Repository 결과로 [uiState]를 갱신한다.
     */
    fun setPreviewState(state: HomeUiState) {
        _uiState.value = state
    }

    private fun emitEvent(event: HomeUiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}
