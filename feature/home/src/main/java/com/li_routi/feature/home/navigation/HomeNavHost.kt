package com.li_routi.feature.home.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.li_routi.core.common.ui.nav.AppBottomTab
import com.li_routi.core.common.ui.routine.CategoryAddBottomSheet
import com.li_routi.core.common.ui.routine.CategoryColor
import com.li_routi.core.common.ui.routine.RoutineChecklistItem
import com.li_routi.core.common.ui.routine.RoutineDeleteDialog
import com.li_routi.core.common.ui.routine.RoutineEditBottomSheet
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.feature.home.screen.MyRoutineScreen
import com.li_routi.feature.home.vm.HomeUiEvent
import com.li_routi.feature.home.vm.NotificationUiEvent
import com.li_routi.feature.shopping.navigation.ShoppingRoute

private const val RouteHomeMain = "home_main"
private const val RouteMyRoutine = "myRoutine"
private const val RouteRoutineManage = "routineManage"
private const val RouteNotification = "notification"
private const val RouteNotificationSettings = "notification_settings"
private const val RouteShop = "shop"

/**
 * 홈 피처 내비게이션 그래프.
 *
 * 홈 화면 → "내 루틴" / 알림 / 상점가기 / `+` 메뉴의 "내 루틴 관리"까지는
 * 이 NavHost 안에서 자체 처리하고, 방 만들기·초대코드 참여(다른 feature)는
 * [onCreateRoomClick]/[onJoinRoomWithInviteCodeClick]로 호출부(앱 전체 내비게이션)에 위임한다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeNavHost(
    onCreateRoomClick: () -> Unit = {},
    onJoinRoomWithInviteCodeClick: () -> Unit = {},
    onTabSelected: (AppBottomTab) -> Unit = {},
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = RouteHomeMain,
        modifier = modifier,
    ) {
        composable(RouteHomeMain) {
            HomeRoute(
                onEvent = { event ->
                    when (event) {
                        HomeUiEvent.NavigateToMyRoutine -> navController.navigate(RouteMyRoutine)
                        HomeUiEvent.NavigateToManageMyRoutine -> navController.navigate(RouteRoutineManage)
                        HomeUiEvent.NavigateToCreateRoom -> onCreateRoomClick()
                        HomeUiEvent.NavigateToJoinRoomWithInviteCode -> onJoinRoomWithInviteCodeClick()
                        HomeUiEvent.NavigateToNotification -> navController.navigate(RouteNotification)
                        HomeUiEvent.NavigateToShop -> navController.navigate(RouteShop)
                        // TODO: 체크리스트 카메라 진입은 HomeRoute HorizontalPager에서 처리.
                        HomeUiEvent.NavigateToRoutineAuthCamera,
                        is HomeUiEvent.NavigateToRoutineAuthCameraWithId,
                        HomeUiEvent.CategoryCreated,
                        is HomeUiEvent.CategoryCreateFailed,
                        -> Unit
                    }
                },
                onTabSelected = onTabSelected,
            )
        }

        composable(RouteNotification) {
            NotificationRoute(
                onEvent = { event ->
                    when (event) {
                        NotificationUiEvent.NavigateBack -> navController.popBackStack()
                        NotificationUiEvent.NavigateToSettings -> {
                            navController.navigate(RouteNotificationSettings)
                        }
                    }
                },
            )
        }

        composable(RouteNotificationSettings) {
            NotificationSettingsRoute(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(RouteMyRoutine) {
            var query by remember { mutableStateOf("") }
            var selectedCategory by remember { mutableStateOf("전체") }
            var showRoutineSheet by remember { mutableStateOf(false) }
            var showCategorySheet by remember { mutableStateOf(false) }
            var showDeleteDialog by remember { mutableStateOf(false) }
            var routineName by remember { mutableStateOf("") }
            var categoryName by remember { mutableStateOf("") }
            var categoryColor by remember { mutableStateOf<CategoryColor?>(null) }
            var selectedDays by remember { mutableStateOf(emptySet<Int>()) }

            MyRoutineScreen(
                query = query,
                onQueryChange = { query = it },
                categories = listOf("전체", "건강", "운동", "공부"),
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                onAddCategoryClick = { showCategorySheet = true },
                routines = listOf(
                    RoutineChecklistItem("1", "물 마시기", false, "마감 22:00", "건강", "주중"),
                    RoutineChecklistItem("2", "물 마시기", false, "마감 22:00", "건강", "월,수,금"),
                    RoutineChecklistItem("3", "물 마시기", false, "마감 22:00", "건강", "금요일마다"),
                ),
                onAddRoutineClick = { showRoutineSheet = true },
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack() },
            )

            if (showCategorySheet) {
                CategoryAddBottomSheet(
                    name = categoryName,
                    onNameChange = { categoryName = it },
                    selectedColor = categoryColor,
                    onColorSelected = { categoryColor = it },
                    onConfirm = { showCategorySheet = false },
                    onDismissRequest = { showCategorySheet = false },
                )
            }

            if (showRoutineSheet) {
                RoutineEditBottomSheet(
                    name = routineName,
                    onNameChange = { routineName = it },
                    deadlineText = "오후 11:00",
                    repeatText = "없음",
                    selectedDays = selectedDays,
                    onDayClick = { index ->
                        selectedDays = if (index in selectedDays) selectedDays - index else selectedDays + index
                    },
                    alarmText = "없음",
                    onAlarmClick = {},
                    onDeleteClick = { showDeleteDialog = true },
                    onConfirm = { showRoutineSheet = false },
                    onDismissRequest = { showRoutineSheet = false },
                )
            }

            if (showDeleteDialog) {
                RoutineDeleteDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    onConfirmDelete = {
                        showDeleteDialog = false
                        showRoutineSheet = false
                    },
                )
            }
        }

        composable(RouteRoutineManage) {
            RoutineManageRoute(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(RouteShop) {
            ShoppingRoute(
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun HomeNavHostPreview() {
    LiroutiFrontendTheme {
        HomeNavHost()
    }
}
