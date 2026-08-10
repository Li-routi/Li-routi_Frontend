package com.li_routi.feature.home.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.li_routi.core.common.ui.nav.AppBottomTab
import com.li_routi.core.data.di.HomeContainer
import com.li_routi.core.data.di.RoutineContainer
import com.li_routi.feature.home.screen.HomeScreen
import com.li_routi.feature.home.vm.HomeUiEvent
import com.li_routi.feature.home.vm.HomeUiState
import com.li_routi.feature.home.vm.HomeViewModel

/**
 * 홈 화면 진입점. [HomeViewModel]과 [HomeScreen]을 연결한다.
 *
 * 카메라/촬영 후 메모·루틴선택은 이 화면 로컬이 아니라 `app` 모듈이 소유한 공유 인증 플로우로
 * 넘어간다(개인/그룹/챌린지 어디서 시작하든 같은 화면을 쓰기 위함) — 체크리스트 카메라 아이콘 클릭은
 * [HomeUiEvent.NavigateToRoutineAuthCamera]/[HomeUiEvent.NavigateToRoutineAuthCameraWithId]로
 * [onEvent]를 통해 그대로 위(`HomeNavHost` → `AppNavHost`)로 전달된다.
 *
 * @param onEvent 홈 일회성 UI 이벤트 콜백.
 */
@Composable
fun HomeRoute(
    onEvent: (HomeUiEvent) -> Unit = {},
    onTabSelected: (AppBottomTab) -> Unit = {},
    /** 루틴 관리 완료 등 외부에서 홈 요약을 다시 불러오라는 신호. */
    requestRefresh: Boolean = false,
    onRefreshHandled: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel {
        HomeViewModel(
            getHomeSummaryUseCase = HomeContainer.getHomeSummaryUseCase,
            createRoutineCategoryUseCase = RoutineContainer.createRoutineCategoryUseCase,
            getRoutineCategoriesUseCase = RoutineContainer.getRoutineCategoriesUseCase,
            updateRoutineCategoryUseCase = RoutineContainer.updateRoutineCategoryUseCase,
            deleteRoutineCategoryUseCase = RoutineContainer.deleteRoutineCategoryUseCase,
        )
    },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(requestRefresh) {
        if (requestRefresh) {
            viewModel.refresh()
            onRefreshHandled()
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            // 카테고리 CRUD 결과는 HomeScreen이 직접 collect한다.
            when (event) {
                HomeUiEvent.CategoryCreated,
                is HomeUiEvent.CategoryCreateFailed,
                HomeUiEvent.CategoryUpdated,
                is HomeUiEvent.CategoryUpdateFailed,
                HomeUiEvent.CategoryDeleted,
                is HomeUiEvent.CategoryDeleteFailed,
                -> Unit
                else -> onEvent(event)
            }
        }
    }

    HomeScreenContent(
        viewModel = viewModel,
        uiState = uiState,
        onTabSelected = onTabSelected,
        modifier = modifier,
    )
}

@Composable
private fun HomeScreenContent(
    viewModel: HomeViewModel,
    uiState: HomeUiState,
    onTabSelected: (AppBottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    HomeScreen(
        actions = viewModel,
        onTabSelected = onTabSelected,
        hasActiveRoutine = uiState.hasActiveRoutine,
        hasGroupRoom = uiState.hasGroupRoom,
        nickname = uiState.nickname,
        myRoutineItems = uiState.myRoutineItems,
        myRoutineFilters = uiState.myRoutineFilters,
        groupRoomFilters = uiState.groupRoomFilters,
        groupRoomItems = uiState.groupRoomItems,
        showChecklist = uiState.showChecklist,
        isLoading = uiState.isLoading,
        loadError = uiState.loadError,
        uiEvent = viewModel.uiEvent,
        findEditableCategory = viewModel::editableCategoryByName,
        addCategoryEnabled = uiState.addCategoryEnabled,
        modifier = modifier,
    )
}
