package com.li_routi.feature.home.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.domain.home.GetHomeSummaryUseCase
import com.li_routi.feature.home.navigation.HomeScreenActions
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
    initialState: HomeUiState = HomeUiState(isLoading = true),
) : BaseViewModel(), HomeScreenActions {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<HomeUiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<HomeUiEvent> = _uiEvent.asSharedFlow()

    init {
        refresh()
    }

    /** 홈 요약을 다시 불러온다. 인증 업로드 성공 후 등에서 호출한다. */
    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadError = false) }
            when (val result = getHomeSummaryUseCase()) {
                is ResultState.Success -> _uiState.value = result.data.toHomeUiState()
                is ResultState.Error -> _uiState.update {
                    it.copy(isLoading = false, loadError = true)
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
        val canVerify = _uiState.value.myRoutineItems
            .asSequence()
            .plus(_uiState.value.groupRoomItems)
            .any { it.id == routineId && it.canVerify }
        if (!canVerify) return
        emitEvent(HomeUiEvent.NavigateToRoutineAuthCameraWithId(routineId))
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
