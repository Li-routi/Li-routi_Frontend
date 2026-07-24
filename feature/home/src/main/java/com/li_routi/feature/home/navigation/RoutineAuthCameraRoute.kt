package com.li_routi.feature.home.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.li_routi.feature.home.screen.RoutineAuthCameraScreen
import com.li_routi.feature.home.vm.RoutineAuthCameraUiEvent
import com.li_routi.feature.home.vm.RoutineAuthCameraViewModel

/**
 * 루틴 인증 카메라 화면 진입점.
 *
 * [onEvent]에서 뒤로가기/촬영 후 업로드 화면 이동 이벤트를 받아 Navigation을 연결한다.
 */
@Composable
fun RoutineAuthCameraRoute(
    onEvent: (RoutineAuthCameraUiEvent) -> Unit = {},
    modifier: Modifier = Modifier,
    isCameraActive: Boolean = true,
    viewModel: RoutineAuthCameraViewModel = viewModel { RoutineAuthCameraViewModel() },
) {
    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            onEvent(event)
        }
    }

    RoutineAuthCameraScreen(
        actions = viewModel,
        modifier = modifier,
        isCameraActive = isCameraActive,
    )
}
