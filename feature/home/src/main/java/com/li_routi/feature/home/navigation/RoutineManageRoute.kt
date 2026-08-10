package com.li_routi.feature.home.navigation

import androidx.activity.compose.BackHandler
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.li_routi.core.common.ui.routine.CategoryAddBottomSheet
import com.li_routi.core.common.ui.routine.CategoryColor
import com.li_routi.core.common.ui.routine.RoutineChecklistScreen
import com.li_routi.core.common.ui.routine.RoutineDeleteDialog
import com.li_routi.core.common.ui.routine.RoutineEditBottomSheet
import com.li_routi.core.common.ui.routine.toCategoryColor
import com.li_routi.core.data.di.RoutineContainer
import com.li_routi.core.designsystem.component.LiroutiClockTime
import com.li_routi.core.designsystem.component.LiroutiConfirmDialog
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.home.vm.RoutineManageUiEvent
import com.li_routi.feature.home.vm.RoutineManageViewModel

/** LiroutiDaySelector 인덱스: 0=일 … 6=토 */
private val DayIndexToApi = listOf(
    "SUNDAY",
    "MONDAY",
    "TUESDAY",
    "WEDNESDAY",
    "THURSDAY",
    "FRIDAY",
    "SATURDAY",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineManageRoute(
    onNavigateBack: () -> Unit,
    /** 완료(등록 성공) 후 호출. 기본은 [onNavigateBack]과 동일. */
    onSubmitSuccess: () -> Unit = onNavigateBack,
    modifier: Modifier = Modifier,
    viewModel: RoutineManageViewModel = viewModel {
        RoutineManageViewModel(
            getRoutineCategoriesUseCase = RoutineContainer.getRoutineCategoriesUseCase,
            createRoutineCategoryUseCase = RoutineContainer.createRoutineCategoryUseCase,
            updateRoutineCategoryUseCase = RoutineContainer.updateRoutineCategoryUseCase,
            deleteRoutineCategoryUseCase = RoutineContainer.deleteRoutineCategoryUseCase,
            getRoutineTemplatesUseCase = RoutineContainer.getRoutineTemplatesUseCase,
            createMemberRoutinesUseCase = RoutineContainer.createMemberRoutinesUseCase,
        )
    },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCategorySheet by remember { mutableStateOf(false) }
    var showCategoryDeleteDialog by remember { mutableStateOf(false) }
    var showRoutineSheet by remember { mutableStateOf(false) }
    var showSheetDeleteDialog by remember { mutableStateOf(false) }
    var showExitConfirmDialog by remember { mutableStateOf(false) }
    var pendingExitAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var editingCategoryId by remember { mutableStateOf<Long?>(null) }
    var categoryName by remember { mutableStateOf("") }
    var categoryColor by remember { mutableStateOf<CategoryColor?>(null) }
    var routineName by remember { mutableStateOf("") }
    var selectedDays by remember { mutableStateOf(emptySet<Int>()) }
    var startTime by remember { mutableStateOf(LiroutiClockTime.DefaultMorning) }
    var endTime by remember { mutableStateOf(LiroutiClockTime.DefaultEvening) }

    val hasRoutineSheetDraft =
        routineName.isNotBlank() ||
            selectedDays.isNotEmpty() ||
            startTime != LiroutiClockTime.DefaultMorning ||
            endTime != LiroutiClockTime.DefaultEvening

    fun askDiscardConfirm(onConfirm: () -> Unit) {
        pendingExitAction = onConfirm
        showExitConfirmDialog = true
    }

    fun closeRoutineSheet() {
        showRoutineSheet = false
        showSheetDeleteDialog = false
        routineName = ""
        selectedDays = emptySet()
        startTime = LiroutiClockTime.DefaultMorning
        endTime = LiroutiClockTime.DefaultEvening
    }

    fun requestExit() {
        when {
            showRoutineSheet && hasRoutineSheetDraft -> {
                askDiscardConfirm {
                    closeRoutineSheet()
                    viewModel.onBack()
                }
            }
            showRoutineSheet -> closeRoutineSheet()
            uiState.hasDraftChanges -> askDiscardConfirm { viewModel.onBack() }
            else -> viewModel.onBack()
        }
    }

    fun requestRoutineSheetDismiss() {
        if (hasRoutineSheetDraft) {
            askDiscardConfirm { closeRoutineSheet() }
        } else {
            closeRoutineSheet()
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                RoutineManageUiEvent.NavigateBack -> onNavigateBack()
                RoutineManageUiEvent.SubmitSuccess -> onSubmitSuccess()
                RoutineManageUiEvent.CategorySaved,
                RoutineManageUiEvent.CategoryDeleted,
                -> {
                    showCategorySheet = false
                    showCategoryDeleteDialog = false
                    editingCategoryId = null
                }
            }
        }
    }

    BackHandler(onBack = ::requestExit)

    Box(modifier = modifier.fillMaxSize()) {
        RoutineChecklistScreen(
            topBarTitle = "루틴 추가",
            heading = "내 루틴을 추가해 보세요",
            description = "루틴을 누르면 세부 설정을 변경할 수 있어요",
            categories = uiState.categoryLabels,
            selectedCategory = uiState.selectedCategoryName,
            onCategorySelected = viewModel::onCategorySelected,
            onAddCategoryClick = {
                if (uiState.addCategoryEnabled) {
                    editingCategoryId = null
                    categoryName = ""
                    categoryColor = null
                    viewModel.clearError()
                    showCategorySheet = true
                }
            },
            onCategoryLongClick = { name ->
                val category = viewModel.categoryByName(name) ?: return@RoutineChecklistScreen
                editingCategoryId = category.categoryId
                categoryName = category.name
                categoryColor = category.color.toCategoryColor()
                viewModel.clearError()
                showCategorySheet = true
            },
            addCategoryEnabled = uiState.addCategoryEnabled,
            items = uiState.checklistItems,
            onItemCheckedChange = viewModel::onItemCheckedChange,
            allSelected = uiState.allSelectableSelected,
            onSelectAllChange = viewModel::onSelectAllChange,
            onAddRoutineClick = {
                routineName = ""
                selectedDays = emptySet()
                startTime = LiroutiClockTime.DefaultMorning
                endTime = LiroutiClockTime.DefaultEvening
                showRoutineSheet = true
            },
            primaryButtonText = if (uiState.isSubmitting) "등록 중..." else "완료",
            primaryButtonEnabled = uiState.canSubmit,
            onPrimaryButtonClick = viewModel::onSubmit,
            onBackClick = ::requestExit,
            onCloseClick = ::requestExit,
        )

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = LiroutiTheme.colors.primaryNormal,
            )
        }

        uiState.errorMessage?.takeIf { !showCategorySheet }?.let { message ->
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
            onNameChange = {
                categoryName = it.take(10)
                viewModel.clearError()
            },
            selectedColor = categoryColor,
            onColorSelected = { categoryColor = it },
            placeholder = "최대 10자",
            errorMessage = uiState.categoryNameError ?: uiState.errorMessage,
            onConfirm = {
                val categoryId = editingCategoryId
                if (categoryId == null) {
                    viewModel.onCreateCategory(categoryName, categoryColor)
                } else {
                    viewModel.onUpdateCategory(categoryId, categoryName, categoryColor)
                }
            },
            onDeleteClick = {
                val categoryId = editingCategoryId
                if (categoryId == null) {
                    showCategorySheet = false
                } else {
                    showCategoryDeleteDialog = true
                }
            },
            onDismissRequest = {
                showCategorySheet = false
                editingCategoryId = null
                viewModel.clearError()
            },
        )
    }

    if (showCategoryDeleteDialog && editingCategoryId != null) {
        LiroutiConfirmDialog(
            title = "카테고리를 삭제할까요?",
            message = "이 카테고리에 속한 루틴이 있을 수 있어요.",
            confirmText = "삭제",
            onConfirm = {
                val categoryId = editingCategoryId ?: return@LiroutiConfirmDialog
                showCategoryDeleteDialog = false
                viewModel.onDeleteCategory(categoryId)
            },
            onDismissRequest = { showCategoryDeleteDialog = false },
        )
    }

    if (showRoutineSheet) {
        RoutineEditBottomSheet(
            name = routineName,
            onNameChange = { routineName = it.take(20) },
            startTime = startTime,
            // API에 startTime 필드 없음 — UI만 유지, 저장은 endTime만 전송
            onStartTimeChange = { startTime = it },
            endTime = endTime,
            onEndTimeChange = { endTime = it },
            selectedDays = selectedDays,
            onDayClick = { index ->
                selectedDays = if (index in selectedDays) selectedDays - index else selectedDays + index
            },
            showAlarmSection = false,
            showRoomInfo = false,
            onDeleteClick = { showSheetDeleteDialog = true },
            onConfirm = {
                val accepted = viewModel.onAddCustomRoutine(
                    name = routineName,
                    categoryId = uiState.selectedCategoryId,
                    endTime = endTime.toApiHHmm(),
                    repeatDays = selectedDays.toApiRepeatDays(),
                )
                if (accepted) {
                    closeRoutineSheet()
                }
            },
            onDismissRequest = ::requestRoutineSheetDismiss,
        )
    }

    if (showSheetDeleteDialog) {
        RoutineDeleteDialog(
            onDismissRequest = { showSheetDeleteDialog = false },
            onConfirmDelete = {
                showSheetDeleteDialog = false
                closeRoutineSheet()
            },
        )
    }

    if (showExitConfirmDialog) {
        LiroutiConfirmDialog(
            title = "화면을 나가시겠어요?",
            message = "작성 중인 내용이 사라져요.",
            confirmText = "나가기",
            isConfirmDestructive = false,
            onConfirm = {
                showExitConfirmDialog = false
                pendingExitAction?.invoke()
                pendingExitAction = null
            },
            onDismissRequest = {
                showExitConfirmDialog = false
                pendingExitAction = null
            },
        )
    }
}

private fun Set<Int>.toApiRepeatDays(): List<String> =
    sorted().mapNotNull { DayIndexToApi.getOrNull(it) }
