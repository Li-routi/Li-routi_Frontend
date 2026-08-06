package com.li_routi.feature.home.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.common.ui.nav.AppBottomNavBar
import com.li_routi.core.common.ui.nav.AppBottomTab
import com.li_routi.core.common.ui.routine.CategoryAddBottomSheet
import com.li_routi.core.common.ui.routine.CategoryColor
import com.li_routi.core.designsystem.component.LiroutiToast
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.home.component.AddMenuBottomSheet
import com.li_routi.feature.home.component.HomeTopBar
import com.li_routi.feature.home.component.RoutineChecklistItemUiModel
import com.li_routi.feature.home.component.RoutineChecklistSection
import com.li_routi.feature.home.component.SampleGroupRoomFilters
import com.li_routi.feature.home.component.SampleGroupRoomItems
import com.li_routi.feature.home.component.SampleMyRoutineFilters
import com.li_routi.feature.home.component.SampleMyRoutineItems
import com.li_routi.feature.home.component.SampleMyRoutineItemsOnly
import com.li_routi.feature.home.component.ShopEntryCard
import com.li_routi.feature.home.navigation.HomeScreenActions
import com.li_routi.feature.home.vm.HomeUiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/** Figma `Bottom Sheet` 상단의 드래그 힌트 바 (`h-[4px] w-[44px]`, 회색 pill). */
private val SheetDragHandleColor = androidx.compose.ui.graphics.Color(0xFFDEDEDE)
private val SheetPeekHeight = 370.dp

/**
 * 홈 화면 상태별 캐릭터 툴팁 문구 (Figma Design Page [1.1] `처음 진입 시` 기준).
 * - 처음 진입 / 내 루틴+그룹방: 인사
 * - 내 루틴 O / 그룹방 X: 방 만들기 유도
 */
internal fun homeTooltipMessage(hasActiveRoutine: Boolean, hasGroupRoom: Boolean): String = when {
    !hasActiveRoutine || hasGroupRoom -> "반가워요!"
    else -> "상단 + 버튼을 눌러 친구와 방을 만들어봐요."
}

/**
 * 홈 화면.
 *
 * 하단은 Figma대로 "오늘의 루틴"/"그룹 루틴" 탭이 항상 보이는, 위로 끌어올릴 수 있는 바텀시트로
 * 구성한다([BottomSheetScaffold]). 각 탭의 실제 목록은 [RoutineChecklistSection]이 맡고, 데이터가
 * 없으면 그 안에서 탭별 empty 상태를 보여준다.
 *
 * Design Page [1.1]: 메인 Home에는 "밀어서 빠른 인증" 라벨을 두지 않는다.
 * 실제 카메라 진입은 [com.li_routi.feature.home.navigation.HomeRoute] HorizontalPager로 처리.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    actions: HomeScreenActions,
    onTabSelected: (AppBottomTab) -> Unit,
    hasActiveRoutine: Boolean = false,
    hasGroupRoom: Boolean = false,
    nickname: String = "닉네임",
    myRoutineItems: List<RoutineChecklistItemUiModel> = when {
        !hasActiveRoutine -> emptyList()
        hasGroupRoom -> SampleMyRoutineItems
        else -> SampleMyRoutineItemsOnly
    },
    myRoutineFilters: List<String> = emptyList(),
    groupRoomFilters: List<String> = SampleGroupRoomFilters,
    groupRoomItems: List<RoutineChecklistItemUiModel> = SampleGroupRoomItems,
    /** 개인 또는 그룹 루틴이 하나라도 있으면 true. 기본은 [hasActiveRoutine]과 동일. */
    showChecklist: Boolean = hasActiveRoutine || hasGroupRoom,
    isLoading: Boolean = false,
    loadError: Boolean = false,
    /** 카테고리 생성 성공/실패 등 홈 일회성 UI 이벤트. */
    uiEvent: Flow<HomeUiEvent> = emptyFlow(),
    modifier: Modifier = Modifier,
) {
    var showAddMenuSheet by remember { mutableStateOf(false) }
    var showCategorySheet by remember { mutableStateOf(false) }
    var categoryName by remember { mutableStateOf("") }
    var categoryColor by remember { mutableStateOf<CategoryColor?>(null) }
    var categoryCreateError by remember { mutableStateOf<String?>(null) }
    val tooltipMessage = homeTooltipMessage(hasActiveRoutine, hasGroupRoom)
    val sheetScaffoldState = rememberBottomSheetScaffoldState()

    // 예전엔 카메라가 HorizontalPager의 별도 페이지라 스와이프가 저절로 됐지만, 카메라가 `app` 모듈이
    // 소유한 공유 오버레이로 옮겨가면서 페이지 자체가 없어졌다. 그 스와이프 진입 방식을 유지하기 위해
    // 왼쪽 가장자리에서 시작한 오른쪽 드래그만 감지해 [HomeScreenActions.onSwipeToVerification]을
    // 호출한다. 가장자리로 시작 지점을 제한해야 체크리스트 스크롤/사선 드래그 중 실수로 카메라가
    // 열리지 않는다.
    val density = LocalDensity.current
    val swipeThresholdPx = remember(density) { with(density) { 80.dp.toPx() } }
    val swipeEdgeStartPx = remember(density) { with(density) { 24.dp.toPx() } }

    LaunchedEffect(uiEvent) {
        uiEvent.collect { event ->
            when (event) {
                HomeUiEvent.CategoryCreated -> {
                    showCategorySheet = false
                    categoryName = ""
                    categoryColor = null
                    categoryCreateError = null
                }
                is HomeUiEvent.CategoryCreateFailed -> {
                    categoryCreateError = event.message
                }
                else -> Unit
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(actions) {
                var cumulativeDragPx = 0f
                var triggered = false
                var startedAtEdge = false
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        cumulativeDragPx = 0f
                        triggered = false
                        startedAtEdge = offset.x <= swipeEdgeStartPx
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        if (!startedAtEdge) return@detectHorizontalDragGestures
                        cumulativeDragPx += dragAmount
                        if (!triggered && cumulativeDragPx > swipeThresholdPx) {
                            triggered = true
                            actions.onSwipeToVerification()
                            change.consume()
                        }
                    },
                )
            },
    ) {
        BottomSheetScaffold(
            modifier = Modifier.fillMaxSize(),
            scaffoldState = sheetScaffoldState,
            topBar = {
                HomeTopBar(
                    onAddRoutineClick = { showAddMenuSheet = true },
                    onNotificationClick = actions::onNotificationClick,
                )
            },
            sheetPeekHeight = SheetPeekHeight,
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            sheetContainerColor = LiroutiTheme.colors.backgroundDefault,
            sheetDragHandle = { HomeSheetDragHandle() },
            containerColor = LiroutiTheme.colors.backgroundSecondary,
            sheetContent = {
                when {
                    isLoading && !showChecklist && !loadError -> {
                        // CodeRabbit 반영: fillMaxHeight()를 쓰면 시트가 접힌 peek 영역(SheetPeekHeight)
                        // 밖으로 내용이 밀려나 로딩 인디케이터가 초기 화면에 안 보일 수 있어 높이를 peek에 맞춤
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(SheetPeekHeight),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(color = LiroutiTheme.colors.primaryNormal)
                        }
                    }
                    loadError && !showChecklist -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(SheetPeekHeight)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = "홈 정보를 불러오지 못했습니다.",
                                style = LiroutiTheme.typography.body2,
                                color = LiroutiTheme.colors.labelDefault,
                            )
                            Text(
                                text = "다시 시도",
                                style = LiroutiTheme.typography.body2,
                                color = LiroutiTheme.colors.primaryNormal,
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .clickable(onClick = actions::onRetryLoadClick),
                            )
                        }
                    }
                    else -> {
                        // 시트 표면/드래그 가능 영역 자체가 상태바(그리고 그 안쪽 알림창 당겨내리기
                        // 제스처 영역)까지 올라가면 안 되므로, 콘텐츠에 여백을 주는 대신 시트 높이의
                        // 상한 자체를 "가용 높이 - 상태바 높이"로 캡핑한다. 콘텐츠 쪽 padding은 원래대로
                        // 그대로 둔다 — statusBarsPadding을 안쪽에 걸면 시트 표면은 그대로 상태바까지
                        // 올라가고 콘텐츠만 밀려 내려가는 것뿐이라 근본적인 해결이 안 된다.
                        val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    // 그룹 탭처럼 content가 짧아도 Expanded로 올라갈 수 있게 최대한
                                    // 확보하되, 상태바 높이만큼은 넘지 않게 상한을 둔다.
                                    .heightIn(max = maxHeight - statusBarHeight),
                            ) {
                                if (loadError) {
                                    Text(
                                        text = "최신 정보를 불러오지 못했습니다. 다시 시도",
                                        style = LiroutiTheme.typography.caption,
                                        color = LiroutiTheme.colors.primaryNormal,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(onClick = actions::onRetryLoadClick)
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                    )
                                }
                                RoutineChecklistSection(
                                    hasGroupRoom = hasGroupRoom,
                                    myRoutineItems = myRoutineItems,
                                    myRoutineFilters = myRoutineFilters,
                                    groupRoomFilters = groupRoomFilters,
                                    groupRoomItems = groupRoomItems,
                                    onRoutineCameraClick = actions::onRoutineCameraClick,
                                    onAddCategoryClick = {
                                        categoryName = ""
                                        categoryColor = null
                                        showCategorySheet = true
                                    },
                                    modifier = Modifier.weight(1f, fill = true),
                                )
                                // 하단 네비게이션 바(오버레이)에 가려지지 않도록 여백을 둔다.
                                Box(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                // Design Page [1.1]: 내 루틴 카드 없이 닉네임/캐릭터/상점가기 영역이 메인
                ShopEntryCard(
                    nickname = nickname,
                    tooltipMessage = tooltipMessage,
                    onNavigateToShop = actions::onNavigateToShop,
                    // Figma `처음 진입 시` 포함 홈 메인에서 대표 배지 노출
                    showRepresentativeBadge = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        AppBottomNavBar(
            selectedTab = AppBottomTab.Home,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )

        categoryCreateError?.let { message ->
            LiroutiToast(
                message = message,
                onCloseClick = { categoryCreateError = null },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 100.dp),
            )
        }
    }

    if (showAddMenuSheet) {
        AddMenuBottomSheet(
            onDismissRequest = { showAddMenuSheet = false },
            onManageMyRoutineClick = actions::onManageMyRoutineClick,
            onCreateRoomClick = actions::onCreateRoomClick,
            onJoinRoomWithInviteCodeClick = actions::onJoinRoomWithInviteCodeClick,
        )
    }

    if (showCategorySheet) {
        CategoryAddBottomSheet(
            name = categoryName,
            onNameChange = {
                categoryName = it.take(10)
                categoryCreateError = null
            },
            selectedColor = categoryColor,
            onColorSelected = { categoryColor = it },
            placeholder = "최대 10자",
            onConfirm = {
                // 시트는 CategoryCreated 수신 시에만 닫는다.
                actions.onCreateCategory(categoryName, categoryColor)
            },
            onDismissRequest = {
                showCategorySheet = false
                categoryCreateError = null
            },
        )
    }
}

@Composable
private fun HomeSheetDragHandle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(width = 44.dp, height = 4.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(SheetDragHandleColor),
        )
    }
}

private object PreviewHomeScreenActions : HomeScreenActions {
    override fun onNotificationClick() = Unit
    override fun onNavigateToShop() = Unit
    override fun onMyRoutineClick() = Unit
    override fun onRoutineCameraClick(routineId: String) = Unit
    override fun onSwipeToVerification() = Unit
    override fun onManageMyRoutineClick() = Unit
    override fun onCreateRoomClick() = Unit
    override fun onJoinRoomWithInviteCodeClick() = Unit
    override fun onRetryLoadClick() = Unit
    override fun onCreateCategory(name: String, color: CategoryColor?) = Unit
}

@Preview(showBackground = true, heightDp = 800, name = "1. 처음 진입")
@Composable
private fun HomeScreenEmptyPreview() {
    LiroutiFrontendTheme {
        HomeScreen(actions = PreviewHomeScreenActions, onTabSelected = {}, hasActiveRoutine = false)
    }
}

@Preview(showBackground = true, heightDp = 900, name = "2. 내 루틴 O / 그룹방 X")
@Composable
private fun HomeScreenRoutineOnlyPreview() {
    LiroutiFrontendTheme {
        HomeScreen(
            actions = PreviewHomeScreenActions,
            onTabSelected = {},
            hasActiveRoutine = true,
            hasGroupRoom = false,
            myRoutineItems = SampleMyRoutineItemsOnly,
            myRoutineFilters = SampleMyRoutineFilters,
        )
    }
}

@Preview(showBackground = true, heightDp = 1000, name = "3. 내 루틴 O / 그룹방 O")
@Composable
private fun HomeScreenRoutineAndGroupRoomPreview() {
    LiroutiFrontendTheme {
        HomeScreen(
            actions = PreviewHomeScreenActions,
            onTabSelected = {},
            hasActiveRoutine = true,
            hasGroupRoom = true,
            myRoutineItems = SampleMyRoutineItems,
            myRoutineFilters = SampleMyRoutineFilters,
        )
    }
}
