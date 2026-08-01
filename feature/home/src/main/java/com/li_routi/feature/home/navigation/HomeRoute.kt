package com.li_routi.feature.home.navigation

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.li_routi.core.common.ui.nav.AppBottomTab
import com.li_routi.core.data.di.HomeContainer
import com.li_routi.core.data.di.RoutineContainer
import com.li_routi.feature.home.screen.HomeScreen
import com.li_routi.feature.home.screen.RoutineAuthCameraScreen
import com.li_routi.feature.home.vm.HomeUiEvent
import com.li_routi.feature.home.vm.HomeUiState
import com.li_routi.feature.home.vm.HomeViewModel
import com.li_routi.feature.home.vm.RoutineAuthCameraUiEvent
import com.li_routi.feature.home.vm.RoutineAuthCameraViewModel
import com.li_routi.feature.home.vm.RoutineAuthUploadUiEvent
import com.li_routi.feature.home.vm.toAuthSelectables

/** Pager: 카메라(왼쪽) ← 스와이프 → 홈(오른쪽, 초기 페이지) */
private const val PageCamera = 0
private const val PageHome = 1

/**
 * [Uri]는 Bundle에 바로 못 넣으므로 문자열로 저장/복원한다.
 * 구성 변경(회전 등) 후에도 업로드 화면이 유지되도록 한다.
 */
private val NullableUriSaver = Saver<Uri?, String>(
    save = { uri -> uri?.toString().orEmpty() },
    restore = { saved -> saved.takeIf { it.isNotEmpty() }?.let(Uri::parse) },
)

/**
 * 홈 화면 진입점. [HomeViewModel]과 [HomeScreen]을 연결한다.
 *
 * 루틴 또는 그룹 루틴방이 있으면 HorizontalPager로 홈↔카메라 스와이프를 제공한다.
 * 체크리스트 카메라 아이콘 / 셔터 촬영 성공 시 카메라·업로드 화면으로 이어진다.
 *
 * @param onEvent 홈 일회성 UI 이벤트 콜백.
 * @param onCameraEvent 카메라 일회성 이벤트 보조 콜백(선택). 업로드 전환 시에도 전달된다.
 */
@Composable
fun HomeRoute(
    onEvent: (HomeUiEvent) -> Unit = {},
    onCameraEvent: (RoutineAuthCameraUiEvent) -> Unit = {},
    onTabSelected: (AppBottomTab) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel {
        HomeViewModel(
            getHomeSummaryUseCase = HomeContainer.getHomeSummaryUseCase,
            createRoutineCategoryUseCase = RoutineContainer.createRoutineCategoryUseCase,
        )
    },
    cameraViewModel: RoutineAuthCameraViewModel = viewModel { RoutineAuthCameraViewModel() },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val canSwipeToAuth = uiState.hasActiveRoutine || uiState.hasGroupRoom
    var capturedPhotoUri by rememberSaveable(stateSaver = NullableUriSaver) {
        mutableStateOf(null)
    }
    var pendingPagerPage by rememberSaveable { mutableStateOf<Int?>(null) }
    /** 체크리스트 카메라 아이콘으로 진입 시, 업로드 화면에서 미리 선택할 루틴 id. */
    var pendingAuthRoutineId by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                HomeUiEvent.NavigateToRoutineAuthCamera -> {
                    pendingPagerPage = PageCamera
                }
                is HomeUiEvent.NavigateToRoutineAuthCameraWithId -> {
                    pendingAuthRoutineId = event.routineId
                    pendingPagerPage = PageCamera
                }
                else -> Unit
            }
            onEvent(event)
        }
    }

    val photoUri = capturedPhotoUri
    if (photoUri != null) {
        val preselected = pendingAuthRoutineId?.let { setOf(it) }.orEmpty()
        // 미완료 개인·그룹 루틴을 업로드 선택 목록으로 넘긴다.
        val authRoutines = (uiState.myRoutineItems + uiState.groupRoomItems).toAuthSelectables()
        RoutineAuthUploadRoute(
            photoUri = photoUri,
            initialSelectedRoutineIds = preselected,
            routines = authRoutines,
            onEvent = { event ->
                when (event) {
                    RoutineAuthUploadUiEvent.NavigateBack -> {
                        capturedPhotoUri = null
                        pendingPagerPage = PageCamera
                    }
                    RoutineAuthUploadUiEvent.NavigateClose -> {
                        capturedPhotoUri = null
                        pendingAuthRoutineId = null
                        pendingPagerPage = PageHome
                    }
                    RoutineAuthUploadUiEvent.NavigateToHome -> {
                        capturedPhotoUri = null
                        pendingAuthRoutineId = null
                        pendingPagerPage = PageHome
                        viewModel.refresh()
                    }
                }
            },
            modifier = modifier.fillMaxSize(),
        )
        return
    }

    if (canSwipeToAuth) {
        val pagerState = rememberPagerState(
            initialPage = PageHome,
            pageCount = { 2 },
        )

        LaunchedEffect(pendingPagerPage) {
            val page = pendingPagerPage ?: return@LaunchedEffect
            pagerState.animateScrollToPage(page)
            pendingPagerPage = null
        }

        LaunchedEffect(cameraViewModel, pagerState) {
            cameraViewModel.uiEvent.collect { event ->
                when (event) {
                    RoutineAuthCameraUiEvent.NavigateBack -> {
                        pendingAuthRoutineId = null
                        pagerState.animateScrollToPage(PageHome)
                    }
                    is RoutineAuthCameraUiEvent.NavigateToRoutineAuthUpload -> {
                        capturedPhotoUri = event.photoUri
                        onCameraEvent(event)
                    }
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = modifier.fillMaxSize(),
            beyondViewportPageCount = 0,
        ) { page ->
            when (page) {
                PageCamera -> {
                    RoutineAuthCameraScreen(
                        actions = cameraViewModel,
                        modifier = Modifier.fillMaxSize(),
                        isCameraActive = pagerState.currentPage == PageCamera,
                    )
                }
                else -> {
                    HomeScreenContent(
                        viewModel = viewModel,
                        uiState = uiState,
                        onTabSelected = onTabSelected,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    } else {
        HomeScreenContent(
            viewModel = viewModel,
            uiState = uiState,
            onTabSelected = onTabSelected,
            modifier = modifier,
        )
    }
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
        groupRoomFilters = uiState.groupRoomFilters,
        groupRoomItems = uiState.groupRoomItems,
        showChecklist = uiState.showChecklist,
        isLoading = uiState.isLoading,
        loadError = uiState.loadError,
        modifier = modifier,
    )
}
