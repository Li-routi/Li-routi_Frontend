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
import com.li_routi.core.common.ui.routine.CategoryAddBottomSheet
import com.li_routi.core.common.ui.routine.RoutineChecklistItem
import com.li_routi.core.common.ui.routine.RoutineChecklistScreen
import com.li_routi.core.common.ui.routine.RoutineDeleteDialog
import com.li_routi.core.common.ui.routine.RoutineEditBottomSheet
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.feature.home.screen.MyRoutineScreen
import com.li_routi.feature.home.vm.HomeUiEvent

private const val RouteHomeMain = "home_main"
private const val RouteMyRoutine = "myRoutine"
private const val RouteRoutineManage = "routineManage"

/**
 * 홈 피처 내비게이션 그래프.
 *
 * 홈 화면 → "내 루틴" 카드 탭(내 루틴 화면) / `+` 메뉴의 "내 루틴 관리"(루틴 관리 화면)까지는
 * 이 NavHost 안에서 자체 처리하고, 방 만들기·초대코드 참여(다른 feature)는
 * [onCreateRoomClick]/[onJoinRoomWithInviteCodeClick]로 호출부(앱 전체 내비게이션)에 위임한다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeNavHost(
    onCreateRoomClick: () -> Unit = {},
    onJoinRoomWithInviteCodeClick: () -> Unit = {},
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
                        // TODO: 상점/알림/체크리스트 카메라 진입 연결은 이번 범위 밖.
                        HomeUiEvent.NavigateToShop,
                        HomeUiEvent.NavigateToNotification,
                        HomeUiEvent.NavigateToRoutineAuthCamera,
                        is HomeUiEvent.NavigateToRoutineAuthCameraWithId,
                        -> Unit
                    }
                },
            )
        }

        composable(RouteMyRoutine) {
            var query by remember { mutableStateOf("") }
            var showRoutineSheet by remember { mutableStateOf(false) }
            var showDeleteDialog by remember { mutableStateOf(false) }
            var routineName by remember { mutableStateOf("") }
            var selectedDays by remember { mutableStateOf(emptySet<Int>()) }

            MyRoutineScreen(
                query = query,
                onQueryChange = { query = it },
                routines = listOf("비타민 먹기", "스트레칭 하기", "명상 하기"),
                onAddRoutineClick = { showRoutineSheet = true },
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack() },
            )

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
            var selectedCategory by remember { mutableStateOf("전체") }
            var items by remember {
                mutableStateOf(
                    listOf(
                        RoutineChecklistItem("1", "물 마시기", true),
                        RoutineChecklistItem("2", "비타민 먹기", false),
                        RoutineChecklistItem("3", "스트레칭 하기", false),
                        RoutineChecklistItem("4", "명상 하기", false),
                    ),
                )
            }
            var showRoutineSheet by remember { mutableStateOf(false) }
            var showCategorySheet by remember { mutableStateOf(false) }
            var showDeleteDialog by remember { mutableStateOf(false) }
            var routineName by remember { mutableStateOf("") }
            var categoryName by remember { mutableStateOf("") }
            var selectedDays by remember { mutableStateOf(emptySet<Int>()) }

            RoutineChecklistScreen(
                topBarTitle = "루틴 관리",
                categories = listOf("전체", "건강", "운동", "공부"),
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                onAddCategoryClick = { showCategorySheet = true },
                items = items,
                onItemCheckedChange = { id, checked ->
                    items = items.map { if (it.id == id) it.copy(checked = checked) else it }
                },
                allSelected = items.all { it.checked },
                onSelectAllChange = { checked -> items = items.map { it.copy(checked = checked) } },
                onAddRoutineClick = { showRoutineSheet = true },
                primaryButtonText = "완료",
                onPrimaryButtonClick = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack() },
            )

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

            if (showCategorySheet) {
                CategoryAddBottomSheet(
                    name = categoryName,
                    onNameChange = { categoryName = it },
                    onConfirm = { showCategorySheet = false },
                    onDismissRequest = { showCategorySheet = false },
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
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun HomeNavHostPreview() {
    LiroutiFrontendTheme {
        HomeNavHost()
    }
}
