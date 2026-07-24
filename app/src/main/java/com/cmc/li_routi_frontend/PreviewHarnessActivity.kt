package com.cmc.li_routi_frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.li_routi.core.common.ui.routine.CategoryAddBottomSheet
import com.li_routi.core.common.ui.routine.RoutineChecklistItem
import com.li_routi.core.common.ui.routine.RoutineChecklistScreen
import com.li_routi.core.common.ui.routine.RoutineDeleteDialog
import com.li_routi.core.common.ui.routine.RoutineEditBottomSheet
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.feature.grouproutine.screen.InviteCodeScreen
import com.li_routi.feature.grouproutine.screen.JoinRoomConfirmDialog
import com.li_routi.feature.grouproutine.screen.MakeRoomScreen
import com.li_routi.feature.home.screen.MyRoutineScreen

class PreviewHarnessActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiroutiFrontendTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PreviewHarnessScreen()
                }
            }
        }
    }
}

private enum class HarnessScreen {
    Menu,
    MyRoutine,
    RoutineChecklist,
    MakeRoom,
    InviteCode,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreviewHarnessScreen() {
    var currentScreen by remember { mutableStateOf(HarnessScreen.Menu) }

    var showRoutineSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCategorySheet by remember { mutableStateOf(false) }
    var showJoinDialog by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var selectedDays by remember { mutableStateOf(setOf(1, 2, 3, 4, 5)) }
    var categoryName by remember { mutableStateOf("") }
    var query by remember { mutableStateOf("") }
    var roomName by remember { mutableStateOf("") }
    var inviteCode by remember { mutableStateOf("") }
    var checklistItems by remember {
        mutableStateOf(
            listOf(
                RoutineChecklistItem("1", "물 마시기", true),
                RoutineChecklistItem("2", "비타민 먹기", false),
                RoutineChecklistItem("3", "스트레칭 하기", false),
                RoutineChecklistItem("4", "명상 하기", false),
            ),
        )
    }
    var selectedCategory by remember { mutableStateOf("전체") }

    when (currentScreen) {
        HarnessScreen.Menu -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(text = "Preview Harness (#18~#25 확인용, 임시)")
                Button(onClick = { showRoutineSheet = true }) {
                    Text(text = "루틴 추가/편집 바텀시트 (#18)")
                }
                Button(onClick = { showDeleteDialog = true }) {
                    Text(text = "루틴 삭제 확인 다이얼로그 (#19)")
                }
                Button(onClick = { showCategorySheet = true }) {
                    Text(text = "카테고리 추가 바텀시트 (#20)")
                }
                Button(onClick = { currentScreen = HarnessScreen.MyRoutine }) {
                    Text(text = "내 루틴 화면 (#22)")
                }
                Button(onClick = { currentScreen = HarnessScreen.RoutineChecklist }) {
                    Text(text = "루틴 체크리스트 화면 (#23)")
                }
                Button(onClick = { currentScreen = HarnessScreen.MakeRoom }) {
                    Text(text = "방 만들기 화면 (#24)")
                }
                Button(onClick = { currentScreen = HarnessScreen.InviteCode }) {
                    Text(text = "초대코드 입력 화면 (#25)")
                }
            }
        }

        HarnessScreen.MyRoutine -> {
            MyRoutineScreen(
                query = query,
                onQueryChange = { query = it },
                routines = listOf("비타민 먹기", "스트레칭 하기", "명상 하기"),
                onAddRoutineClick = { showRoutineSheet = true },
                onBackClick = { currentScreen = HarnessScreen.Menu },
                onCloseClick = { currentScreen = HarnessScreen.Menu },
            )
        }

        HarnessScreen.RoutineChecklist -> {
            RoutineChecklistScreen(
                topBarTitle = "루틴 추가",
                heading = "함께할 루틴을 추가해보세요",
                description = "루틴을 누르면 세부 설정을 변경할 수 있어요",
                categories = listOf("전체", "건강", "운동", "공부"),
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                onAddCategoryClick = { showCategorySheet = true },
                items = checklistItems,
                onItemCheckedChange = { id, checked ->
                    checklistItems = checklistItems.map { if (it.id == id) it.copy(checked = checked) else it }
                },
                allSelected = checklistItems.all { it.checked },
                onSelectAllChange = { checked -> checklistItems = checklistItems.map { it.copy(checked = checked) } },
                onAddRoutineClick = { showRoutineSheet = true },
                primaryButtonText = "방 만들기",
                onPrimaryButtonClick = {},
                onBackClick = { currentScreen = HarnessScreen.Menu },
                onCloseClick = { currentScreen = HarnessScreen.Menu },
            )
        }

        HarnessScreen.MakeRoom -> {
            MakeRoomScreen(
                roomName = roomName,
                onRoomNameChange = { roomName = it },
                onNextClick = { currentScreen = HarnessScreen.RoutineChecklist },
                onBackClick = { currentScreen = HarnessScreen.Menu },
                onCloseClick = { currentScreen = HarnessScreen.Menu },
            )
        }

        HarnessScreen.InviteCode -> {
            InviteCodeScreen(
                code = inviteCode,
                onCodeChange = { inviteCode = it },
                errorMessage = null,
                isConfirmEnabled = inviteCode.isNotBlank(),
                onConfirmClick = { showJoinDialog = true },
                onBackClick = { currentScreen = HarnessScreen.Menu },
                onCloseClick = { currentScreen = HarnessScreen.Menu },
            )
        }
    }

    if (showRoutineSheet) {
        RoutineEditBottomSheet(
            name = name,
            onNameChange = { name = it },
            deadlineText = "오후 11:00",
            repeatText = "주중",
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

    if (showCategorySheet) {
        CategoryAddBottomSheet(
            name = categoryName,
            onNameChange = { categoryName = it },
            onConfirm = { showCategorySheet = false },
            onDismissRequest = { showCategorySheet = false },
        )
    }

    if (showJoinDialog) {
        JoinRoomConfirmDialog(
            roomName = "갓생살자",
            memberCount = 3,
            totalRoutineCount = 7,
            onDismissRequest = { showJoinDialog = false },
            onJoinClick = { showJoinDialog = false },
        )
    }
}
