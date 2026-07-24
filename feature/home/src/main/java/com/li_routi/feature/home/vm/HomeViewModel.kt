package com.li_routi.feature.home.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.feature.home.navigation.HomeScreenActions
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 홈 화면 ViewModel.
 *
 * - [uiState]: 화면 렌더링에 필요한 상태 (루틴/그룹방 유무, 리스트 등)
 * - [uiEvent]: 클릭으로 발생하는 일회성 이벤트 (네비게이션 등). [HomeRoute]에서 collect한다.
 *
 * [HomeScreenActions]를 구현해 Screen의 버튼 이벤트를 여기서 처리한다.
 * 빠른 인증 화면 진입은 [HomeRoute] HorizontalPager 스와이프로 처리한다.
 * 실제 API/Repository 연동은 이후 단계에서 추가한다.
 *
 * @param initialState Preview/개발 확인용 초기 상태. 기본값은 처음 진입(empty).
 */
class HomeViewModel(
    initialState: HomeUiState = HomeUiState.empty(),
) : BaseViewModel(), HomeScreenActions {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<HomeUiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<HomeUiEvent> = _uiEvent.asSharedFlow()

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

    override fun onManageMyRoutineClick() {
        emitEvent(HomeUiEvent.NavigateToManageMyRoutine)
    }

    override fun onCreateRoomClick() {
        emitEvent(HomeUiEvent.NavigateToCreateRoom)
    }

    override fun onJoinRoomWithInviteCodeClick() {
        emitEvent(HomeUiEvent.NavigateToJoinRoomWithInviteCode)
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
