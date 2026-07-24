package com.li_routi.feature.home.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.DsPlaceholder
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.home.component.AddMenuBottomSheet
import com.li_routi.feature.home.component.EmptyRoutineSection
import com.li_routi.feature.home.component.HomeBottomNav
import com.li_routi.feature.home.component.HomeTopBar
import com.li_routi.feature.home.component.MyRoutineCard
import com.li_routi.feature.home.component.RoutineChecklistItemUiModel
import com.li_routi.feature.home.component.RoutineChecklistSection
import com.li_routi.feature.home.component.SampleGroupRoomFilters
import com.li_routi.feature.home.component.SampleGroupRoomItems
import com.li_routi.feature.home.component.SampleMyRoutineItems
import com.li_routi.feature.home.component.SampleMyRoutineItemsOnly
import com.li_routi.feature.home.component.ShopEntryCard
import com.li_routi.feature.home.component.SwipeHintLabel
import com.li_routi.feature.home.navigation.HomeScreenActions

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
 * 홈 화면. Figma 기준 3가지 상태:
 *
 * 1. **처음 진입** (`hasActiveRoutine=false`, `hasGroupRoom=false`): 스와이프 인증 비활성
 * 2. **내 루틴만** / **내 루틴 + 그룹방**: "밀어서 빠른 인증" 힌트 표시.
 *    실제 카메라 진입은 [com.li_routi.feature.home.navigation.HomeRoute] HorizontalPager로 처리.
 *
 * Design System instance는 [DsPlaceholder]로 둔다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    actions: HomeScreenActions,
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
    modifier: Modifier = Modifier,
) {
    var showAddMenuSheet by remember { mutableStateOf(false) }
    val tooltipMessage = homeTooltipMessage(hasActiveRoutine, hasGroupRoom)
    val showSwipeHint = hasActiveRoutine || hasGroupRoom

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = LiroutiTheme.colors.backgroundAlternative,
        topBar = {
            HomeTopBar(
                onAddRoutineClick = { showAddMenuSheet = true },
                onNotificationClick = actions::onNotificationClick,
            )
        },
        bottomBar = { HomeBottomNav() },
    ) { innerPadding ->
        if (hasActiveRoutine) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    if (showSwipeHint) {
                        SwipeHintLabel()
                    }
                    MyRoutineCard(
                        onClick = actions::onMyRoutineClick,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    ShopEntryCard(
                        nickname = nickname,
                        tooltipMessage = tooltipMessage,
                        onNavigateToShop = actions::onNavigateToShop,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                DsPlaceholder(
                    componentName = "Divider",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                )
                RoutineChecklistSection(
                    hasGroupRoom = hasGroupRoom,
                    myRoutineItems = myRoutineItems,
                    groupRoomFilters = groupRoomFilters,
                    groupRoomItems = groupRoomItems,
                    onRoutineCameraClick = actions::onRoutineCameraClick,
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    if (showSwipeHint) {
                        SwipeHintLabel()
                    }
                    MyRoutineCard(
                        onClick = actions::onMyRoutineClick,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    ShopEntryCard(
                        nickname = nickname,
                        tooltipMessage = tooltipMessage,
                        onNavigateToShop = actions::onNavigateToShop,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                DsPlaceholder(
                    componentName = "Divider",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                )
                EmptyRoutineSection(modifier = Modifier.weight(1f))
            }
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
}

private object PreviewHomeScreenActions : HomeScreenActions {
    override fun onNotificationClick() = Unit
    override fun onNavigateToShop() = Unit
    override fun onMyRoutineClick() = Unit
    override fun onRoutineCameraClick(routineId: String) = Unit
    override fun onManageMyRoutineClick() = Unit
    override fun onCreateRoomClick() = Unit
    override fun onJoinRoomWithInviteCodeClick() = Unit
}

@Preview(showBackground = true, heightDp = 800, name = "1. 처음 진입")
@Composable
private fun HomeScreenEmptyPreview() {
    LiroutiFrontendTheme {
        HomeScreen(actions = PreviewHomeScreenActions, hasActiveRoutine = false)
    }
}

@Preview(showBackground = true, heightDp = 900, name = "2. 내 루틴 O / 그룹방 X")
@Composable
private fun HomeScreenRoutineOnlyPreview() {
    LiroutiFrontendTheme {
        HomeScreen(
            actions = PreviewHomeScreenActions,
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
            hasActiveRoutine = true,
            hasGroupRoom = true,
            myRoutineItems = SampleMyRoutineItems,
        )
    }
}
