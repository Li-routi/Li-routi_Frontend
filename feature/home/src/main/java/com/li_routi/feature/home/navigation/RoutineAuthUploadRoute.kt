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
import com.li_routi.feature.home.vm.SampleRoutineAuthSelectables

/**
 * 촬영 후 메모/루틴 선택(업로드) 화면 진입점.
 *
 * @param photoUri 카메라에서 촬영한 사진 URI.
 * @param initialSelectedRoutineIds 체크리스트 카메라 진입 등으로 미리 선택할 루틴 id.
 * @param onEvent Navigation 연결 콜백. 업로드 성공 시 [RoutineAuthUploadUiEvent.NavigateToHome] 수신.
 * @param upload 테스트/실패 재현용 업로드 처리. 기본은 성공. 사진 URI를 첫 인자로 받는다.
 */
@Composable
fun RoutineAuthUploadRoute(
    photoUri: Uri? = null,
    initialSelectedRoutineIds: Set<String> = emptySet(),
    onEvent: (RoutineAuthUploadUiEvent) -> Unit = {},
    modifier: Modifier = Modifier,
    upload: suspend (photoUri: Uri, memo: String, selectedRoutineIds: Set<String>) -> Result<Unit> =
        { _, _, _ -> Result.success(Unit) },
    viewModel: RoutineAuthUploadViewModel = viewModel(key = photoUri?.toString() ?: "no_photo") {
        val selectableIds = SampleRoutineAuthSelectables.map { it.id }.toSet()
        RoutineAuthUploadViewModel(
            initialState = RoutineAuthUploadUiState(
                photoUri = photoUri,
                // 홈에서 넘긴 id가 업로드 목록에 있을 때만 미리 선택한다.
                selectedRoutineIds = initialSelectedRoutineIds.intersect(selectableIds),
            ),
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
