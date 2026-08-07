package com.li_routi.feature.home.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.li_routi.core.common.ui.routine.CategoryAddBottomSheet
import com.li_routi.core.common.ui.routine.CategoryColor
import com.li_routi.core.common.ui.routine.RoutineDeleteDialog
import com.li_routi.core.common.ui.routine.RoutineEditBottomSheet
import com.li_routi.core.data.di.RoutineContainer
import com.li_routi.core.designsystem.component.LiroutiClockTime
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.home.screen.MyRoutineScreen
import com.li_routi.feature.home.vm.MyRoutineUiEvent
import com.li_routi.feature.home.vm.MyRoutineViewModel
import com.li_routi.feature.home.vm.toDayIndexes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRoutineRoute(
    onNavigateBack: () -> Unit,
    onNavigateToAddRoutine: () -> Unit,
    /** 루틴 수정/삭제 성공 후 홈 요약 재조회용. */
    onRoutinesChanged: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: MyRoutineViewModel = viewModel {
        MyRoutineViewModel(
            getMemberRoutinesUseCase = RoutineContainer.getMemberRoutinesUseCase,
            getRoutineCategoriesUseCase = RoutineContainer.getRoutineCategoriesUseCase,
            createRoutineCategoryUseCase = RoutineContainer.createRoutineCategoryUseCase,
            updateMemberRoutineUseCase = RoutineContainer.updateMemberRoutineUseCase,
            deleteMemberRoutineUseCase = RoutineContainer.deleteMemberRoutineUseCase,
        )
    },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCategorySheet by remember { mutableStateOf(false) }
    var categoryName by remember { mutableStateOf("") }
    var categoryColor by remember { mutableStateOf<CategoryColor?>(null) }
    var editingRoutineId by remember { mutableStateOf<Long?>(null) }
    var editName by remember { mutableStateOf("") }
    var editStartTime by remember { mutableStateOf(LiroutiClockTime.DefaultMorning) }
    var editEndTime by remember { mutableStateOf(LiroutiClockTime.DefaultEvening) }
    var editSelectedDays by remember { mutableStateOf(emptySet<Int>()) }
    var editAlarmTime by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val editingRoutine = editingRoutineId?.let { uiState.routineById(it) }

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                MyRoutineUiEvent.NavigateBack -> onNavigateBack()
                MyRoutineUiEvent.NavigateToAddRoutine -> onNavigateToAddRoutine()
                MyRoutineUiEvent.EditCompleted -> {
                    editingRoutineId = null
                    showDeleteDialog = false
                    onRoutinesChanged()
                }
                MyRoutineUiEvent.DeleteCompleted -> {
                    editingRoutineId = null
                    showDeleteDialog = false
                    onRoutinesChanged()
                }
            }
        }
    }

    // 루틴 추가 화면에서 돌아온 뒤 목록을 다시 맞춘다.
    LaunchedEffect(lifecycleOwner, viewModel) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.refresh()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        MyRoutineScreen(
            query = uiState.query,
            onQueryChange = viewModel::onQueryChange,
            categories = uiState.categoryLabels,
            selectedCategory = uiState.selectedCategoryName,
            onCategorySelected = viewModel::onCategorySelected,
            onAddCategoryClick = {
                if (uiState.addCategoryEnabled) {
                    categoryName = ""
                    categoryColor = null
                    viewModel.clearError()
                    showCategorySheet = true
                }
            },
            addCategoryEnabled = uiState.addCategoryEnabled,
            routines = uiState.visibleRoutines,
            onRoutineClick = { id ->
                val routineId = id.toLongOrNull() ?: return@MyRoutineScreen
                val routine = uiState.routineById(routineId) ?: return@MyRoutineScreen
                editingRoutineId = routineId
                editName = routine.name
                editStartTime = LiroutiClockTime.DefaultMorning
                editEndTime = LiroutiClockTime.fromApiHHmm(
                    value = routine.endTime,
                    fallback = LiroutiClockTime.DefaultEvening,
                )
                editSelectedDays = routine.toDayIndexes()
                editAlarmTime = routine.alarmTime
                viewModel.clearError()
            },
            onAddRoutineClick = viewModel::onAddRoutineClick,
            onBackClick = viewModel::onBackClick,
            onCloseClick = viewModel::onCloseClick,
        )

        if (uiState.isLoading || uiState.isSaving) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = LiroutiTheme.colors.primaryNormal,
            )
        }

        uiState.errorMessage?.let { message ->
            Text(
                text = message,
                style = LiroutiTheme.typography.caption,
                color = LiroutiTheme.colors.primaryNormal,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .clickable(onClick = viewModel::clearError)
                    .padding(16.dp),
            )
        }
    }

    if (showCategorySheet) {
        CategoryAddBottomSheet(
            name = categoryName,
            onNameChange = { categoryName = it.take(10) },
            selectedColor = categoryColor,
            onColorSelected = { categoryColor = it },
            placeholder = "최대 10자",
            onConfirm = {
                viewModel.onCreateCategory(categoryName, categoryColor)
                showCategorySheet = false
            },
            onDismissRequest = { showCategorySheet = false },
        )
    }

    if (editingRoutine != null && editingRoutineId != null) {
        RoutineEditBottomSheet(
            name = editName,
            onNameChange = { editName = it.take(20) },
            startTime = editStartTime,
            // API에 startTime 필드 없음 — UI만 유지, 저장은 endTime만 전송
            onStartTimeChange = { editStartTime = it },
            endTime = editEndTime,
            onEndTimeChange = { editEndTime = it },
            selectedDays = editSelectedDays,
            onDayClick = { index ->
                editSelectedDays =
                    if (index in editSelectedDays) editSelectedDays - index else editSelectedDays + index
            },
            // Figma Design Page [1.1] 편집 시트에 알람 행 없음 (`3610:26830`)
            showAlarmSection = false,
            showRoomInfo = false,
            onDeleteClick = { showDeleteDialog = true },
            onConfirm = {
                viewModel.onUpdateRoutine(
                    routineId = editingRoutineId!!,
                    name = editName,
                    endTime = editEndTime.toApiHHmm(),
                    selectedDayIndexes = editSelectedDays,
                    // UI에서 편집하지 않음 — 기존 값 유지
                    alarmTime = editAlarmTime,
                )
            },
            onDismissRequest = {
                if (!uiState.isSaving) {
                    editingRoutineId = null
                    showDeleteDialog = false
                }
            },
        )
    }

    if (showDeleteDialog && editingRoutineId != null) {
        RoutineDeleteDialog(
            message = "이 루틴을 삭제할까요?",
            onDismissRequest = { showDeleteDialog = false },
            onConfirmDelete = {
                viewModel.onDeleteRoutine(editingRoutineId!!)
            },
        )
    }
}
