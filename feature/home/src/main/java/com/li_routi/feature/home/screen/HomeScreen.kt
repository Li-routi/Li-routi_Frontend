package com.li_routi.feature.home.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.common.ui.nav.AppBottomNavBar
import com.li_routi.core.common.ui.nav.AppBottomTab
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.home.component.AddMenuBottomSheet
import com.li_routi.feature.home.component.HomeTopBar
import com.li_routi.feature.home.component.RoutineChecklistItemUiModel
import com.li_routi.feature.home.component.RoutineChecklistSection
import com.li_routi.feature.home.component.SampleGroupRoomFilters
import com.li_routi.feature.home.component.SampleGroupRoomItems
import com.li_routi.feature.home.component.SampleMyRoutineItems
import com.li_routi.feature.home.component.SampleMyRoutineItemsOnly
import com.li_routi.feature.home.component.ShopEntryCard
import com.li_routi.feature.home.component.SwipeHintLabel
import com.li_routi.feature.home.navigation.HomeScreenActions

/** Figma `Bottom Sheet` 상단의 드래그 힌트 바 (`h-[4px] w-[44px]`, 회색 pill). */
private val SheetDragHandleColor = androidx.compose.ui.graphics.Color(0xFFDEDEDE)
private val SheetPeekHeight = 370.dp

/**
 * 홈 화면 상태별 캐릭터 툴팁 문구 (Figma 스크린샷 기준).
 * - 처음 진입: 루틴 생성 유도
 * - 내 루틴 O / 그룹방 X: 방 만들기 유도
 * - 내 루틴 O / 그룹방 O: 인사
 */
internal fun homeTooltipMessage(hasActiveRoutine: Boolean, hasGroupRoom: Boolean): String = when {
    !hasActiveRoutine -> "상단 + 버튼을 눌러 나의 루틴을 생성해보세요!"
    !hasGroupRoom -> "상단 + 버튼을 눌러 친구와 방을 만들어봐요."
    else -> "반가워요!"
}

/**
 * 홈 화면.
 *
 * 하단은 Figma대로 "오늘의 루틴"/"그룹 루틴" 탭이 항상 보이는, 위로 끌어올릴 수 있는 바텀시트로
 * 구성한다([BottomSheetScaffold]). 각 탭의 실제 목록은 [RoutineChecklistSection]이 맡고, 데이터가
 * 없으면 그 안에서 탭별 empty 상태를 보여준다.
 *
 * 개인/그룹 루틴이 하나라도 있으면(스와이프 인증 대상이 있으면) "밀어서 빠른 인증" 힌트를 표시한다.
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
    myRoutineItems: List<RoutineChecklistItemUiModel> = if (hasGroupRoom) {
        SampleMyRoutineItems
    } else {
        SampleMyRoutineItemsOnly
    },
    groupRoomFilters: List<String> = SampleGroupRoomFilters,
    groupRoomItems: List<RoutineChecklistItemUiModel> = SampleGroupRoomItems,
    /** 개인 또는 그룹 루틴이 하나라도 있으면 true. 기본은 [hasActiveRoutine]과 동일. */
    showChecklist: Boolean = hasActiveRoutine || hasGroupRoom,
    isLoading: Boolean = false,
    loadError: Boolean = false,
    modifier: Modifier = Modifier,
) {
    var showAddMenuSheet by remember { mutableStateOf(false) }
    val tooltipMessage = homeTooltipMessage(hasActiveRoutine, hasGroupRoom)
    val showSwipeHint = hasActiveRoutine || hasGroupRoom
    val sheetScaffoldState = rememberBottomSheetScaffoldState()

    Box(modifier = modifier.fillMaxSize()) {
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 300.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(color = LiroutiTheme.colors.primaryNormal)
                        }
                    }
                    loadError && !showChecklist -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 300.dp)
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
                        Column {
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
                                groupRoomFilters = groupRoomFilters,
                                groupRoomItems = groupRoomItems,
                                onRoutineCameraClick = actions::onRoutineCameraClick,
                            )
                            // 하단 네비게이션 바(오버레이)에 가려지지 않도록 여백을 둔다.
                            Box(modifier = Modifier.height(80.dp))
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
                if (showSwipeHint) {
                    SwipeHintLabel()
                }
                ShopEntryCard(
                    nickname = nickname,
                    tooltipMessage = tooltipMessage,
                    onNavigateToShop = actions::onNavigateToShop,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        AppBottomNavBar(
            selectedTab = AppBottomTab.Home,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }

    if (showAddMenuSheet) {
        AddMenuBottomSheet(
            onDismissRequest = { showAddMenuSheet = false },
            onManageMyRoutineClick = actions::onManageMyRoutineClick,
            onCreateRoomClick = actions::onCreateRoomClick,
            onJoinRoomWithInviteCodeClick = actions::onJoinRoomWithInviteCodeClick,
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
    override fun onManageMyRoutineClick() = Unit
    override fun onCreateRoomClick() = Unit
    override fun onJoinRoomWithInviteCodeClick() = Unit
    override fun onRetryLoadClick() = Unit
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
        )
    }
}
