package com.li_routi.feature.home.vm

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.feature.home.navigation.RoutineAuthCameraScreenActions
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * 루틴 인증 카메라 화면 ViewModel.
 *
 * 촬영은 Screen/CameraX에서 수행하고, 성공 시 [onCaptureSuccess]로 URI를 받아
 * [RoutineAuthCameraUiEvent.NavigateToRoutineAuthUpload]를 발행한다.
 */
class RoutineAuthCameraViewModel : BaseViewModel(), RoutineAuthCameraScreenActions {

    private val _uiEvent = MutableSharedFlow<RoutineAuthCameraUiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<RoutineAuthCameraUiEvent> = _uiEvent.asSharedFlow()

    override fun onBackClick() {
        emitEvent(RoutineAuthCameraUiEvent.NavigateBack)
    }

    override fun onCaptureSuccess(photoUri: Uri) {
        emitEvent(RoutineAuthCameraUiEvent.NavigateToRoutineAuthUpload(photoUri))
    }

    private fun emitEvent(event: RoutineAuthCameraUiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}
