package com.li_routi.feature.home.navigation

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.li_routi.feature.home.screen.HomeScreen
import com.li_routi.feature.home.screen.RoutineAuthCameraScreen
import com.li_routi.feature.home.vm.HomeUiEvent
import com.li_routi.feature.home.vm.HomeUiState
import com.li_routi.feature.home.vm.HomeViewModel
import com.li_routi.feature.home.vm.RoutineAuthCameraUiEvent
import com.li_routi.feature.home.vm.RoutineAuthCameraViewModel
import com.li_routi.feature.home.vm.RoutineAuthUploadUiEvent

/** Pager: 카메라(왼쪽) ← 스와이프 → 홈(오른쪽, 초기 페이지) */
private const val PageCamera = 0
private const val PageHome = 1

/**
 * 홈 화면 진입점. [HomeViewModel]과 [HomeScreen]을 연결한다.
 *
 * 루틴 또는 그룹 루틴방이 있으면 HorizontalPager로 홈↔카메라 스와이프를 제공한다.
 * 셔터 촬영 성공 시 업로드 화면을 표시한다.
 *
 * @param onEvent 홈 일회성 UI 이벤트 콜백.
 * @param onCameraEvent 카메라 일회성 이벤트 보조 콜백(선택). 업로드 전환 시에도 전달된다.
 */
@Composable
fun HomeRoute(
    onEvent: (HomeUiEvent) -> Unit = {},
    onCameraEvent: (RoutineAuthCameraUiEvent) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel {
        HomeViewModel(initialState = HomeUiState.empty())
    },
    cameraViewModel: RoutineAuthCameraViewModel = viewModel { RoutineAuthCameraViewModel() },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val canSwipeToAuth = uiState.hasActiveRoutine || uiState.hasGroupRoom
    var capturedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var pendingPagerPage by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            onEvent(event)
        }
    }

    val photoUri = capturedPhotoUri
    if (photoUri != null) {
        RoutineAuthUploadRoute(
            photoUri = photoUri,
            onEvent = { event ->
                when (event) {
                    RoutineAuthUploadUiEvent.NavigateBack -> {
                        capturedPhotoUri = null
                        pendingPagerPage = PageCamera
                    }
                    RoutineAuthUploadUiEvent.NavigateClose,
                    RoutineAuthUploadUiEvent.NavigateToHome,
                    -> {
                        capturedPhotoUri = null
                        pendingPagerPage = PageHome
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
            pagerState.scrollToPage(page)
            pendingPagerPage = null
        }

        LaunchedEffect(cameraViewModel, pagerState) {
            cameraViewModel.uiEvent.collect { event ->
                when (event) {
                    RoutineAuthCameraUiEvent.NavigateBack -> {
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
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    } else {
        HomeScreenContent(
            viewModel = viewModel,
            uiState = uiState,
            modifier = modifier,
        )
    }
}

@Composable
private fun HomeScreenContent(
    viewModel: HomeViewModel,
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
) {
    HomeScreen(
        actions = viewModel,
        hasActiveRoutine = uiState.hasActiveRoutine,
        hasGroupRoom = uiState.hasGroupRoom,
        nickname = uiState.nickname,
        myRoutineItems = uiState.myRoutineItems,
        groupRoomFilters = uiState.groupRoomFilters,
        groupRoomItems = uiState.groupRoomItems,
        modifier = modifier,
    )
}
