package com.li_routi.feature.home.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.li_routi.feature.home.screen.RoutineAuthUploadScreen
import com.li_routi.feature.home.vm.RoutineAuthUploadUiEvent
import com.li_routi.feature.home.vm.RoutineAuthUploadUiState
import com.li_routi.feature.home.vm.RoutineAuthUploadViewModel

/**
 * 촬영 후 메모/루틴 선택(업로드) 화면 진입점.
 *
 * @param photoUri 카메라에서 촬영한 사진 URI.
 * @param onEvent Navigation 연결 콜백. 업로드 성공 시 [RoutineAuthUploadUiEvent.NavigateToHome] 수신.
 * @param upload 테스트/실패 재현용 업로드 처리. 기본은 성공.
 */
@Composable
fun RoutineAuthUploadRoute(
    photoUri: Uri? = null,
    onEvent: (RoutineAuthUploadUiEvent) -> Unit = {},
    modifier: Modifier = Modifier,
    upload: suspend (memo: String, selectedRoutineIds: Set<String>) -> Result<Unit> =
        { _, _ -> Result.success(Unit) },
    viewModel: RoutineAuthUploadViewModel = viewModel(key = photoUri?.toString() ?: "no_photo") {
        RoutineAuthUploadViewModel(
            initialState = RoutineAuthUploadUiState(photoUri = photoUri),
            upload = upload,
        )
    },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            onEvent(event)
        }
    }

    RoutineAuthUploadScreen(
        actions = viewModel,
        memo = uiState.memo,
        routines = uiState.routines,
        selectedRoutineIds = uiState.selectedRoutineIds,
        isUploadEnabled = uiState.isUploadEnabled,
        isUploading = uiState.isUploading,
        showUploadFailedToast = uiState.showUploadFailedToast,
        photoUri = uiState.photoUri,
        modifier = modifier,
    )
}
