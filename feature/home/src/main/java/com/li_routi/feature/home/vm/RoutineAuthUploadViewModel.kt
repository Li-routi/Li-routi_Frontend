package com.li_routi.feature.home.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.feature.home.navigation.RoutineAuthUploadScreenActions
import com.li_routi.feature.home.navigation.RoutineAuthUploadScreenActions.Companion.MEMO_MAX_LENGTH
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 촬영 후 메모/루틴 선택(업로드) 화면 ViewModel.
 *
 * - 루틴 1개 이상 선택 시 업로드 버튼 활성
 * - 뒤로가기/X → 저장 없이 즉시 Navigate 이벤트 발행 (로컬 초안 폐기)
 * - 업로드 성공 → [RoutineAuthUploadUiEvent.NavigateToHome]
 * - 업로드 실패 → 토스트 표시 ([RoutineAuthUploadUiState.showUploadFailedToast])
 *
 * @param upload 실제 API 연동 전까지 주입 가능한 업로드 처리. 기본은 성공.
 */
class RoutineAuthUploadViewModel(
    initialState: RoutineAuthUploadUiState = RoutineAuthUploadUiState(),
    private val upload: suspend (
        memo: String,
        selectedRoutineIds: Set<String>,
    ) -> Result<Unit> = { _, _ -> Result.success(Unit) },
) : BaseViewModel(), RoutineAuthUploadScreenActions {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<RoutineAuthUploadUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<RoutineAuthUploadUiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<RoutineAuthUploadUiEvent> = _uiEvent.asSharedFlow()

    override fun onBackClick() {
        emitEvent(RoutineAuthUploadUiEvent.NavigateBack)
    }

    override fun onCloseClick() {
        emitEvent(RoutineAuthUploadUiEvent.NavigateClose)
    }

    override fun onMemoChange(memo: String) {
        _uiState.update { it.copy(memo = memo.take(MEMO_MAX_LENGTH)) }
    }

    override fun onRoutineToggle(routineId: String) {
        _uiState.update { state ->
            val next = state.selectedRoutineIds.toMutableSet()
            if (!next.add(routineId)) next.remove(routineId)
            state.copy(selectedRoutineIds = next)
        }
    }

    override fun onUploadClick() {
        val state = _uiState.value
        if (!state.isUploadEnabled) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(isUploading = true, showUploadFailedToast = false)
            }
            val result = upload(state.memo, state.selectedRoutineIds)
            _uiState.update { it.copy(isUploading = false) }
            if (result.isSuccess) {
                emitEvent(RoutineAuthUploadUiEvent.NavigateToHome)
            } else {
                _uiState.update { it.copy(showUploadFailedToast = true) }
            }
        }
    }

    override fun onDismissUploadFailedToast() {
        _uiState.update { it.copy(showUploadFailedToast = false) }
    }

    private fun emitEvent(event: RoutineAuthUploadUiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}
