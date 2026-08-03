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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.li_routi.core.common.ui.routine.CategoryAddBottomSheet
import com.li_routi.core.common.ui.routine.CategoryColor
import com.li_routi.core.common.ui.routine.RoutineChecklistScreen
import com.li_routi.core.common.ui.routine.RoutineEditBottomSheet
import com.li_routi.core.data.di.RoutineContainer
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.home.vm.RoutineManageUiEvent
import com.li_routi.feature.home.vm.RoutineManageViewModel

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
            getRoutineTemplatesUseCase = RoutineContainer.getRoutineTemplatesUseCase,
            createMemberRoutinesUseCase = RoutineContainer.createMemberRoutinesUseCase,
        )
    },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCategorySheet by remember { mutableStateOf(false) }
    var showRoutineSheet by remember { mutableStateOf(false) }
    var categoryName by remember { mutableStateOf("") }
    var categoryColor by remember { mutableStateOf<CategoryColor?>(null) }
    var routineName by remember { mutableStateOf("") }
    var selectedDays by remember { mutableStateOf(emptySet<Int>()) }

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                RoutineManageUiEvent.NavigateBack -> onNavigateBack()
                RoutineManageUiEvent.SubmitSuccess -> onSubmitSuccess()
            }
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
                    categoryName = ""
                    categoryColor = null
                    viewModel.clearError()
                    showCategorySheet = true
                }
            },
            addCategoryEnabled = uiState.addCategoryEnabled,
            items = uiState.checklistItems,
            onItemCheckedChange = viewModel::onItemCheckedChange,
            allSelected = uiState.allSelectableSelected,
            onSelectAllChange = viewModel::onSelectAllChange,
            onAddRoutineClick = {
                routineName = ""
                selectedDays = emptySet()
                showRoutineSheet = true
            },
            primaryButtonText = if (uiState.isSubmitting) "등록 중..." else "완료",
            primaryButtonEnabled = uiState.canSubmit,
            onPrimaryButtonClick = viewModel::onSubmit,
            onBackClick = viewModel::onBack,
            onCloseClick = viewModel::onBack,
        )

        if (uiState.isLoading) {
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

    if (showRoutineSheet) {
        RoutineEditBottomSheet(
            name = routineName,
            onNameChange = { routineName = it.take(20) },
            deadlineText = "오후 11:59",
            repeatText = "매일",
            selectedDays = selectedDays,
            onDayClick = { index ->
                selectedDays = if (index in selectedDays) selectedDays - index else selectedDays + index
            },
            alarmText = "없음",
            onAlarmClick = {},
            onDeleteClick = { showRoutineSheet = false },
            onConfirm = {
                viewModel.onAddCustomRoutine(
                    name = routineName,
                    categoryId = uiState.selectedCategoryId,
                )
                showRoutineSheet = false
            },
            onDismissRequest = { showRoutineSheet = false },
        )
    }
}
