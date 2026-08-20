package com.li_routi.feature.home.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.li_routi.core.common.ui.routine.CategoryAddBottomSheet
import com.li_routi.core.common.ui.routine.CategoryColor
import com.li_routi.core.common.ui.routine.RoutineChecklistScreen
import com.li_routi.core.common.ui.routine.RoutineDeleteDialog
import com.li_routi.core.common.ui.routine.RoutineEditBottomSheet
import com.li_routi.core.common.ui.routine.toCategoryColor
import com.li_routi.core.data.di.HomeContainer
import com.li_routi.core.data.di.RoutineContainer
import com.li_routi.core.designsystem.component.LiroutiClockTime
import com.li_routi.core.designsystem.component.LiroutiConfirmDialog
import com.li_routi.core.designsystem.component.LiroutiToast
import com.li_routi.core.designsystem.component.LiroutiToastStyle
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.home.vm.CustomIdPrefix
import com.li_routi.feature.home.vm.DefaultRoutineLockedMessage
import com.li_routi.feature.home.vm.RegisteredCustomIdPrefix
import com.li_routi.feature.home.vm.RoutineManageUiEvent
import com.li_routi.feature.home.vm.RoutineManageViewModel
import com.li_routi.feature.home.vm.toDayIndexes

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

/** [LiroutiClockTime]은 일반 data class라 기본 Saver가 못 다뤄서, HH:mm 왕복 변환으로 저장한다. */
private val LiroutiClockTimeSaver = Saver<LiroutiClockTime, String>(
    save = { it.toApiHHmm() },
    restore = { LiroutiClockTime.fromApiHHmm(it) },
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
            getMemberRoutinesUseCase = RoutineContainer.getMemberRoutinesUseCase,
            getHomeSummaryUseCase = HomeContainer.getHomeSummaryUseCase,
            createMemberRoutinesUseCase = RoutineContainer.createMemberRoutinesUseCase,
            updateMemberRoutineUseCase = RoutineContainer.updateMemberRoutineUseCase,
            deleteMemberRoutineUseCase = RoutineContainer.deleteMemberRoutineUseCase,
        )
    },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // 회전 등 구성 변경 시에도 작성 중이던 카테고리/루틴 폼이 사라지지 않도록 rememberSaveable을 쓴다.
    var showCategorySheet by rememberSaveable { mutableStateOf(false) }
    var showCategoryDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var showRoutineSheet by rememberSaveable { mutableStateOf(false) }
    var showSheetDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var showExitConfirmDialog by remember { mutableStateOf(false) }
    var pendingExitAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var editingCategoryId by rememberSaveable { mutableStateOf<Long?>(null) }
    var categoryName by rememberSaveable { mutableStateOf("") }
    var categoryColor by rememberSaveable { mutableStateOf<CategoryColor?>(null) }
    // null이면 새 커스텀 추가. 값이 있으면 이미 등록된 커스텀(routineId) 수정 중.
    var editingRoutineId by rememberSaveable { mutableStateOf<Long?>(null) }
    // null이 아니면 아직 제출 전인 세션 커스텀(custom_N) 수정 중.
    var editingCustomIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    var routineName by rememberSaveable { mutableStateOf("") }
    var selectedDays by rememberSaveable { mutableStateOf(emptySet<Int>()) }
    var startTime by rememberSaveable(stateSaver = LiroutiClockTimeSaver) {
        mutableStateOf(LiroutiClockTime.DefaultMorning)
    }
    var endTime by rememberSaveable(stateSaver = LiroutiClockTimeSaver) {
        mutableStateOf(LiroutiClockTime.DefaultEvening)
    }
    // 수정 모드로 열면(onItemClick) 기존 루틴 값이 그대로 채워져 들어오므로, 빈 기본값과 비교하면
    // 열자마자 "변경됨"으로 잡힌다. 시트를 열 때의 값을 기준선으로 따로 들고, draft 여부는 이
    // 기준선과의 차이로 판단한다.
    var baselineRoutineName by rememberSaveable { mutableStateOf("") }
    var baselineSelectedDays by rememberSaveable { mutableStateOf(emptySet<Int>()) }
    var baselineStartTime by rememberSaveable(stateSaver = LiroutiClockTimeSaver) {
        mutableStateOf(LiroutiClockTime.DefaultMorning)
    }
    var baselineEndTime by rememberSaveable(stateSaver = LiroutiClockTimeSaver) {
        mutableStateOf(LiroutiClockTime.DefaultEvening)
    }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    val hasRoutineSheetDraft =
        routineName != baselineRoutineName ||
            selectedDays != baselineSelectedDays ||
            startTime != baselineStartTime ||
            endTime != baselineEndTime

    // 시스템 뒤로가기/시트 바깥 배경 탭은 원래 "키보드가 떠 있으면 키보드만 내린다"는 게 안드로이드
    // 기본 동작인데, BackHandler와 ModalBottomSheet의 onDismissRequest가 이를 신경 쓰지 않고 바로
    // 화면/시트를 닫아버리는 문제가 있었다 — 루틴 이름 입력 중 키보드를 내리려던 게 화면 이탈로
    // 이어짐. 키보드가 떠 있으면 그것부터 내리고, 닫기/이탈 로직은 그 다음 탭부터 동작하게 한다.
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    // getBottom(density) 기반 높이 체크는 IME 애니메이션 도중 일시적으로 0을 반환할 수 있어(특히
    // 예측형 뒤로가기 제스처 중), 시맨틱 API인 isImeVisible로 대체한다.
    val isKeyboardVisible = WindowInsets.isImeVisible

    fun hideKeyboard() {
        keyboardController?.hide()
        focusManager.clearFocus()
    }

    fun askDiscardConfirm(onConfirm: () -> Unit) {
        pendingExitAction = onConfirm
        showExitConfirmDialog = true
    }

    fun closeRoutineSheet() {
        showRoutineSheet = false
        showSheetDeleteDialog = false
        editingRoutineId = null
        editingCustomIndex = null
        routineName = ""
        selectedDays = emptySet()
        startTime = LiroutiClockTime.DefaultMorning
        endTime = LiroutiClockTime.DefaultEvening
        baselineRoutineName = ""
        baselineSelectedDays = emptySet()
        baselineStartTime = LiroutiClockTime.DefaultMorning
        baselineEndTime = LiroutiClockTime.DefaultEvening
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

    fun requestCategorySheetDismiss() {
        if (isKeyboardVisible) {
            hideKeyboard()
            return
        }
        showCategorySheet = false
        editingCategoryId = null
        viewModel.clearError()
    }

    fun requestRoutineSheetDismiss() {
        if (isKeyboardVisible) {
            hideKeyboard()
            return
        }
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
                RoutineManageUiEvent.CustomRoutineSaved,
                RoutineManageUiEvent.CustomRoutineDeleted,
                -> closeRoutineSheet()
            }
        }
    }

    BackHandler {
        if (isKeyboardVisible) {
            hideKeyboard()
        } else {
            requestExit()
        }
    }

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
                editingRoutineId = null
                editingCustomIndex = null
                routineName = ""
                selectedDays = emptySet()
                startTime = LiroutiClockTime.DefaultMorning
                endTime = LiroutiClockTime.DefaultEvening
                baselineRoutineName = ""
                baselineSelectedDays = emptySet()
                baselineStartTime = LiroutiClockTime.DefaultMorning
                baselineEndTime = LiroutiClockTime.DefaultEvening
                showRoutineSheet = true
            },
            primaryButtonText = if (uiState.isSubmitting) "등록 중..." else "완료",
            primaryButtonEnabled = uiState.canSubmit,
            onPrimaryButtonClick = viewModel::onSubmit,
            onBackClick = ::requestExit,
            onCloseClick = ::requestExit,
            warningText = uiState.overLimitMessage,
            onItemClick = { id ->
                when {
                    id.startsWith(RegisteredCustomIdPrefix) -> {
                        val routineId = id.removePrefix(RegisteredCustomIdPrefix).toLongOrNull()
                        val routine = uiState.registeredCustomRoutines.firstOrNull { it.routineId == routineId }
                        if (routine != null) {
                            editingRoutineId = routine.routineId
                            editingCustomIndex = null
                            routineName = routine.name
                            selectedDays = routine.toDayIndexes()
                            startTime = LiroutiClockTime.fromApiHHmm(
                                value = routine.startTime,
                                fallback = LiroutiClockTime.DefaultMorning,
                            )
                            endTime = LiroutiClockTime.fromApiHHmm(routine.endTime ?: "23:59")
                            baselineRoutineName = routineName
                            baselineSelectedDays = selectedDays
                            baselineStartTime = startTime
                            baselineEndTime = endTime
                            showRoutineSheet = true
                        }
                    }
                    id.startsWith(CustomIdPrefix) -> {
                        val index = id.removePrefix(CustomIdPrefix).toIntOrNull()
                        val item = index?.let { uiState.customItems.getOrNull(it) }
                        if (index != null && item != null) {
                            editingRoutineId = null
                            editingCustomIndex = index
                            routineName = item.name
                            selectedDays = item.repeatDays.orEmpty().toDayIndexes()
                            startTime = LiroutiClockTime.fromApiHHmm(
                                value = item.startTime,
                                fallback = LiroutiClockTime.DefaultMorning,
                            )
                            endTime = LiroutiClockTime.fromApiHHmm(item.endTime ?: "23:59")
                            baselineRoutineName = routineName
                            baselineSelectedDays = selectedDays
                            baselineStartTime = startTime
                            baselineEndTime = endTime
                            showRoutineSheet = true
                        }
                    }
                    else -> {
                        toastMessage = DefaultRoutineLockedMessage
                    }
                }
            },
        )

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = LiroutiTheme.colors.primaryNormal,
            )
        }

        // 카테고리/루틴 시트가 열려 있으면 각 시트 안에서 에러를 보여준다(ModalBottomSheet는 별도
        // 창이라 이 Box에 그려도 시트에 가려 안 보임) — 여기서는 두 시트가 다 닫혀 있을 때만 보여준다.
        uiState.errorMessage?.takeIf { !showCategorySheet && !showRoutineSheet }?.let { message ->
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

        toastMessage?.let { message ->
            LiroutiToast(
                message = message,
                style = LiroutiToastStyle.Dimmer,
                onCloseClick = { toastMessage = null },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 92.dp)
                    .widthIn(max = 332.dp)
                    .fillMaxWidth()
                    .height(54.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
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
                    requestCategorySheetDismiss()
                } else {
                    showCategoryDeleteDialog = true
                }
            },
            onDismissRequest = ::requestCategorySheetDismiss,
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
            onStartTimeChange = { startTime = it },
            endTime = endTime,
            onEndTimeChange = { endTime = it },
            selectedDays = selectedDays,
            onDayClick = { index ->
                selectedDays = if (index in selectedDays) selectedDays - index else selectedDays + index
            },
            showAlarmSection = false,
            showRoomInfo = false,
            hasDraft = hasRoutineSheetDraft,
            errorMessage = uiState.errorMessage,
            onDeleteClick = { showSheetDeleteDialog = true },
            onConfirm = {
                val routineId = editingRoutineId
                val customIndex = editingCustomIndex
                when {
                    routineId != null -> {
                        // 성공 시 CustomRoutineSaved 이벤트에서 closeRoutineSheet()가 불린다.
                        viewModel.onUpdateCustomRoutine(
                            routineId = routineId,
                            name = routineName,
                            startTime = startTime.toApiHHmm(),
                            endTime = endTime.toApiHHmm(),
                            repeatDays = selectedDays.toApiRepeatDays(),
                        )
                    }
                    customIndex != null -> {
                        val accepted = viewModel.onUpdateDraftCustomRoutine(
                            index = customIndex,
                            name = routineName,
                            startTime = startTime.toApiHHmm(),
                            endTime = endTime.toApiHHmm(),
                            repeatDays = selectedDays.toApiRepeatDays(),
                        )
                        if (accepted) {
                            closeRoutineSheet()
                        }
                    }
                    else -> {
                        val accepted = viewModel.onAddCustomRoutine(
                            name = routineName,
                            categoryId = uiState.selectedCategoryId,
                            startTime = startTime.toApiHHmm(),
                            endTime = endTime.toApiHHmm(),
                            repeatDays = selectedDays.toApiRepeatDays(),
                        )
                        if (accepted) {
                            closeRoutineSheet()
                        }
                    }
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
                val routineId = editingRoutineId
                val customIndex = editingCustomIndex
                when {
                    routineId != null -> {
                        // 성공 시 CustomRoutineDeleted 이벤트에서 closeRoutineSheet()가 불린다.
                        viewModel.onDeleteCustomRoutine(routineId)
                    }
                    customIndex != null -> {
                        viewModel.onRemoveDraftCustomRoutine(customIndex)
                        closeRoutineSheet()
                    }
                    else -> closeRoutineSheet()
                }
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
