package com.li_routi.feature.home.vm

import android.net.Uri
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
 * - 사진 + 루틴 1개 이상 선택 시 업로드 버튼 활성
 * - 뒤로가기/X → 이탈 확인 다이얼로그 (Figma)
 * - 업로드 중 → 버튼 스피너
 * - 업로드 실패 → 하단 토스트에 실패 사유(서버 메시지, 없으면 「업로드 실패」) 표시(버튼 위 16dp)
 * - 업로드 성공 → 토스트「업로드가 완료되었습니다」+ 버튼「완료」→ 탭 시 홈
 */
class RoutineAuthUploadViewModel(
    initialState: RoutineAuthUploadUiState = RoutineAuthUploadUiState(),
    private val upload: suspend (
        photoUri: Uri,
        memo: String,
        selectedRoutineIds: Set<String>,
        routines: List<RoutineAuthSelectableUiModel>,
    ) -> Result<Unit>,
) : BaseViewModel(), RoutineAuthUploadScreenActions {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<RoutineAuthUploadUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<RoutineAuthUploadUiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<RoutineAuthUploadUiEvent> = _uiEvent.asSharedFlow()

    override fun onBackClick() {
        requestExit(navigateBack = true)
    }

    override fun onCloseClick() {
        requestExit(navigateBack = false)
    }

    override fun onMemoChange(memo: String) {
        _uiState.update { it.copy(memo = memo.take(MEMO_MAX_LENGTH)) }
    }

    override fun onRoutineToggle(routineId: String) {
        _uiState.update { state ->
            if (state.isUploading || state.isUploadCompleted) return@update state
            val next = state.selectedRoutineIds.toMutableSet()
            if (!next.add(routineId)) next.remove(routineId)
            state.copy(selectedRoutineIds = next)
        }
    }

    override fun onUploadClick() {
        val state = _uiState.value
        if (state.isUploadCompleted) {
            emitEvent(RoutineAuthUploadUiEvent.NavigateToHome)
            return
        }
        if (!state.isUploadEnabled) return
        val photoUri = state.photoUri ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(isUploading = true, toastMessage = null)
            }
            val result = upload(
                photoUri,
                state.memo,
                state.selectedRoutineIds,
                state.routines,
            )
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isUploading = false,
                        isUploadCompleted = true,
                        toastMessage = "업로드가 완료되었습니다!",
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isUploading = false,
                        toastMessage = result.exceptionOrNull()?.message ?: "업로드 실패",
                    )
                }
            }
        }
    }

    override fun onDismissUploadFailedToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun onDismissExitConfirmDialog() {
        _uiState.update { it.copy(showExitConfirmDialog = false) }
    }

    fun onConfirmExit() {
        val navigateBack = _pendingNavigateBack
        _pendingNavigateBack = true
        _uiState.update { it.copy(showExitConfirmDialog = false) }
        emitEvent(
            if (navigateBack) {
                RoutineAuthUploadUiEvent.NavigateBack
            } else {
                RoutineAuthUploadUiEvent.NavigateClose
            },
        )
    }

    private var _pendingNavigateBack: Boolean = true

    private fun requestExit(navigateBack: Boolean) {
        val state = _uiState.value
        if (state.isUploading) return
        if (state.isUploadCompleted) {
            emitEvent(RoutineAuthUploadUiEvent.NavigateToHome)
            return
        }
        _pendingNavigateBack = navigateBack
        _uiState.update { it.copy(showExitConfirmDialog = true) }
    }

    private fun emitEvent(event: RoutineAuthUploadUiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}
