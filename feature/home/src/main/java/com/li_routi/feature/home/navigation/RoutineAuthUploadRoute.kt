package com.li_routi.feature.home.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.li_routi.feature.home.screen.RoutineAuthUploadScreen
import com.li_routi.feature.home.vm.RoutineAuthSelectableUiModel
import com.li_routi.feature.home.vm.RoutineAuthUploadUiEvent
import com.li_routi.feature.home.vm.RoutineAuthUploadUiState
import com.li_routi.feature.home.vm.RoutineAuthUploadViewModel
import com.li_routi.feature.home.vm.SampleRoutineAuthSelectables
import com.li_routi.feature.home.vm.submitRoutineAuthUpload

/**
 * 촬영 후 메모/루틴 선택(업로드) 화면 진입점.
 *
 * @param photoUri 카메라에서 촬영한 사진 URI.
 * @param initialSelectedRoutineIds 체크리스트 카메라 진입 등으로 미리 선택할 루틴 id.
 * @param routines 선택 가능한 루틴 목록. 기본은 샘플(Preview용).
 * @param onEvent Navigation 연결 콜백. 업로드 성공 시 [RoutineAuthUploadUiEvent.NavigateToHome] 수신.
 */
@Composable
fun RoutineAuthUploadRoute(
    photoUri: Uri? = null,
    initialSelectedRoutineIds: Set<String> = emptySet(),
    routines: List<RoutineAuthSelectableUiModel> = SampleRoutineAuthSelectables,
    onEvent: (RoutineAuthUploadUiEvent) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val appContext = LocalContext.current.applicationContext
    val upload: suspend (
        Uri,
        String,
        Set<String>,
        List<RoutineAuthSelectableUiModel>,
    ) -> Result<Unit> = remember(appContext) {
        { uri, memo, ids, routines ->
            submitRoutineAuthUpload(
                photoUri = uri,
                memo = memo,
                selectedRoutineIds = ids,
                routines = routines,
                context = appContext,
            )
        }
    }
    val viewModel: RoutineAuthUploadViewModel = viewModel(key = photoUri?.toString() ?: "no_photo") {
        val selectableIds = routines.map { it.id }.toSet()
        RoutineAuthUploadViewModel(
            initialState = RoutineAuthUploadUiState(
                photoUri = photoUri,
                routines = routines,
                selectedRoutineIds = initialSelectedRoutineIds.intersect(selectableIds),
            ),
            upload = upload,
        )
    }
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
        isUploadCompleted = uiState.isUploadCompleted,
        toastMessage = uiState.toastMessage,
        showExitConfirmDialog = uiState.showExitConfirmDialog,
        onDismissExitConfirmDialog = viewModel::onDismissExitConfirmDialog,
        onConfirmExit = viewModel::onConfirmExit,
        photoUri = uiState.photoUri,
        modifier = modifier,
    )
}
