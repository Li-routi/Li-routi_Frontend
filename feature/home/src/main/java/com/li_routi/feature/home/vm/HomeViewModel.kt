package com.li_routi.feature.home.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.common.ui.routine.CategoryColor
import com.li_routi.core.common.ui.routine.toApiColor
import com.li_routi.core.data.appearance.FallbackCharacterId
import com.li_routi.core.data.appearance.MemberAppearanceStore
import com.li_routi.core.data.di.NotificationContainer
import com.li_routi.core.data.di.ShopContainer
import com.li_routi.core.data.profile.MemberProfileCache
import com.li_routi.core.domain.home.GetHomeSummaryUseCase
import com.li_routi.core.domain.notification.GetNotificationsUseCase
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
    private val appearanceStore: MemberAppearanceStore = ShopContainer.memberAppearanceStore,
    private val getNotificationsUseCase: GetNotificationsUseCase = NotificationContainer.getNotificationsUseCase,
    // 마지막으로 알던 닉네임/뱃지 값으로 먼저 그려서, 탭 전환마다 ViewModel이 새로 만들어져도
    // placeholder("닉네임")나 숨겨진 뱃지가 잠깐 보였다 실제 값으로 바뀌는 깜빡임을 없앤다.
    initialState: HomeUiState = HomeUiState(
        isLoading = true,
        nickname = MemberProfileCache.nickname.value ?: "닉네임",
        hasUnreadNotification = MemberProfileCache.hasUnreadNotification.value,
    ),
) : BaseViewModel(), HomeScreenActions {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<HomeUiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<HomeUiEvent> = _uiEvent.asSharedFlow()

    init {
        // refresh()가 loadUnreadNotificationStatus()도 같이 호출하므로 여기서 따로 부르지 않는다
        // — 안 그러면 최초 진입 시 같은 조회가 두 번 나간다.
        refresh()
        observeAppearance()
    }

    /**
     * 종 아이콘 뱃지용 "안 읽은 알림 있음" 여부. 서버에 별도 개수 API가 없어 목록 첫 페이지를
     * 받아 [com.li_routi.core.domain.notification.AppNotification.read]가 false인 게 있는지로
     * 판단한다 — 알림 화면과 같은 기본 페이지 크기(20개)를 쓴다.
     */
    private fun loadUnreadNotificationStatus() {
        viewModelScope.launch {
            val result = getNotificationsUseCase(category = null, cursor = null)
            if (result is ResultState.Success) {
                val hasUnread = result.data.notifications.any { n -> !n.read }
                MemberProfileCache.hasUnreadNotification.value = hasUnread
                _uiState.update { it.copy(hasUnreadNotification = hasUnread) }
            }
        }
    }

    /**
     * 앱에 착장/캐릭터가 없으면 받아서 기억하고, 이후엔 상점이 저장한 로컬 상태를 그대로 그림.
     * 화면마다 GET 하면 상점에서 갈아입고 돌아와도 이 ViewModel이 옛값을 들고 있음
     *
     * 구독을 [MemberAppearanceStore.ensureLoaded] 완료 후로 미루지 않고 바로 시작한다 —
     * ensureLoaded 안에서 로컬 캐릭터 값이 착장 네트워크 조회보다 먼저 반영되는데, 구독 자체가
     * ensureLoaded 전체가 끝나기를 기다리면 그 빠른 중간 갱신을 놓치고 결국 네트워크 응답과
     * 동시에야 화면이 갱신돼 기본 캐릭터가 잠깐 보이는 깜빡임이 생긴다.
     */
    private fun observeAppearance() {
        viewModelScope.launch {
            appearanceStore.appearance.collect { appearance ->
                val urls = equippedImageUrlsOf(
                    appearance.equipped.associate { it.slot to it.imageUrl },
                )
                _uiState.update {
                    it.copy(
                        equippedImageUrls = urls,
                        characterId = appearance.characterId.ifBlank { FallbackCharacterId },
                    )
                }
            }
        }
        viewModelScope.launch {
            appearanceStore.ensureLoaded()
        }
    }

    /**
     * 홈이 다시 보일 때 서버 착장을 다시 받음.
     * 상점에서 저장하고 돌아왔는데 화면이 옛 옷을 들고 있는 걸 막음
     */
    fun reloadAppearance() {
        viewModelScope.launch {
            appearanceStore.reloadAvatar()
        }
    }

    /** 홈 요약을 다시 불러온다. 인증 업로드 성공 후 등에서 호출한다. */
    fun refresh() {
        loadUnreadNotificationStatus()
        viewModelScope.launch {
            // 첫 GET이 실패했으면 홈 재시도에서 착장도 다시 받아 둠
            appearanceStore.ensureLoaded()
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
                    MemberProfileCache.nickname.value = next.nickname
                    // 요약으로 상태를 통째로 새로 만들기 때문에, 요약 응답에 없는 필드(착장/캐릭터,
                    // 알림 뱃지)는 여기서 명시적으로 다시 채워 넣지 않으면 next의 기본값(예: 뱃지
                    // false)으로 덮어써진다 — loadUnreadNotificationStatus()가 먼저 true로 반영해도
                    // 뒤이어 이 요약 응답이 도착하면서 다시 꺼지는 깜빡임이 있었다.
                    _uiState.update {
                        val appearance = appearanceStore.appearance.value
                        next.copy(
                            equippedImageUrls = equippedImageUrlsOf(
                                appearance.equipped.associate { it.slot to it.imageUrl },
                            ),
                            characterId = appearance.characterId.ifBlank { FallbackCharacterId },
                            hasUnreadNotification = it.hasUnreadNotification,
                        )
                    }
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
