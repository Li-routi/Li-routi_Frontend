package com.li_routi.feature.grouproutine.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.li_routi.core.common.ui.routine.CategoryAddBottomSheet
import com.li_routi.core.common.ui.routine.RoutineChecklistItem
import com.li_routi.core.common.ui.routine.RoutineChecklistScreen
import com.li_routi.core.common.ui.routine.RoutineDeleteDialog
import com.li_routi.core.common.ui.routine.RoutineEditBottomSheet
import com.li_routi.feature.grouproutine.screen.InviteCodeScreen
import com.li_routi.feature.grouproutine.screen.JoinRoomConfirmDialog
import com.li_routi.feature.grouproutine.screen.MakeRoomScreen
import java.net.URLEncoder

const val MAKE_ROOM_ROUTE = "makeRoom"
const val INVITE_CODE_ROUTE = "inviteCode"
private const val ROOM_ROUTINE_ADD_ROUTE = "roomRoutineAdd/{roomName}"

private fun roomRoutineAddRoute(roomName: String): String {
    return "roomRoutineAdd/${URLEncoder.encode(roomName, "UTF-8")}"
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.grouproutineNavGraph(
    navController: NavHostController,
    onRoomCreated: () -> Unit,
    onJoinedRoom: () -> Unit,
) {
    composable(MAKE_ROOM_ROUTE) {
        var roomName by remember { mutableStateOf("") }

        MakeRoomScreen(
            roomName = roomName,
            onRoomNameChange = { roomName = it },
            onNextClick = { navController.navigate(roomRoutineAddRoute(roomName)) },
            onBackClick = { navController.popBackStack() },
            onCloseClick = { navController.popBackStack() },
        )
    }

    composable(
        route = ROOM_ROUTINE_ADD_ROUTE,
        arguments = listOf(navArgument("roomName") { type = NavType.StringType }),
    ) {
        // roomName nav arg is reserved for the create-room API call once core:domain/core:data exist.
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
            topBarTitle = "루틴 추가",
            heading = "함께할 루틴을 추가해보세요",
            description = "루틴을 누르면 세부 설정을 변경할 수 있어요",
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
            primaryButtonText = "방 만들기",
            onPrimaryButtonClick = onRoomCreated,
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

    composable(INVITE_CODE_ROUTE) {
        var code by remember { mutableStateOf("") }
        var showJoinDialog by remember { mutableStateOf(false) }

        InviteCodeScreen(
            code = code,
            onCodeChange = { code = it },
            errorMessage = null,
            isConfirmEnabled = code.isNotBlank(),
            onConfirmClick = { showJoinDialog = true },
            onBackClick = { navController.popBackStack() },
            onCloseClick = { navController.popBackStack() },
        )

        if (showJoinDialog) {
            JoinRoomConfirmDialog(
                roomName = "갓생살자",
                memberCount = 3,
                totalRoutineCount = 7,
                onDismissRequest = { showJoinDialog = false },
                onJoinClick = {
                    showJoinDialog = false
                    onJoinedRoom()
                },
            )
        }
    }
}
