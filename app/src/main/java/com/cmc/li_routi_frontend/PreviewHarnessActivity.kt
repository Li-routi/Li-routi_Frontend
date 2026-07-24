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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.li_routi.core.common.ui.routine.CategoryAddBottomSheet
import com.li_routi.core.common.ui.routine.RoutineDeleteDialog
import com.li_routi.core.common.ui.routine.RoutineEditBottomSheet
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.feature.grouproutine.navigation.INVITE_CODE_ROUTE
import com.li_routi.feature.grouproutine.navigation.MAKE_ROOM_ROUTE
import com.li_routi.feature.grouproutine.navigation.grouproutineNavGraph
import com.li_routi.feature.home.navigation.MY_ROUTINE_ROUTE
import com.li_routi.feature.home.navigation.ROUTINE_MANAGE_ROUTE
import com.li_routi.feature.home.navigation.homeNavGraph

private const val MENU_ROUTE = "menu"

class PreviewHarnessActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiroutiFrontendTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PreviewHarnessNavHost()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreviewHarnessNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = MENU_ROUTE) {
        composable(MENU_ROUTE) {
            var showRoutineSheet by remember { mutableStateOf(false) }
            var showDeleteDialog by remember { mutableStateOf(false) }
            var showCategorySheet by remember { mutableStateOf(false) }
            var name by remember { mutableStateOf("") }
            var selectedDays by remember { mutableStateOf(setOf(1, 2, 3, 4, 5)) }
            var categoryName by remember { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(text = "Preview Harness (#18~#25 확인용, 임시 · 실제 NavHost)")
                Button(onClick = { showRoutineSheet = true }) {
                    Text(text = "루틴 추가/편집 바텀시트 (#18)")
                }
                Button(onClick = { showDeleteDialog = true }) {
                    Text(text = "루틴 삭제 확인 다이얼로그 (#19)")
                }
                Button(onClick = { showCategorySheet = true }) {
                    Text(text = "카테고리 추가 바텀시트 (#20)")
                }
                Button(onClick = { navController.navigate(MY_ROUTINE_ROUTE) }) {
                    Text(text = "내 루틴 화면 (#22)")
                }
                Button(onClick = { navController.navigate(ROUTINE_MANAGE_ROUTE) }) {
                    Text(text = "루틴 관리 화면 (#23, 내 루틴 관리 진입)")
                }
                Button(onClick = { navController.navigate(MAKE_ROOM_ROUTE) }) {
                    Text(text = "방 만들기 → 루틴 추가 (#23/#24)")
                }
                Button(onClick = { navController.navigate(INVITE_CODE_ROUTE) }) {
                    Text(text = "초대코드 입력 화면 (#25)")
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
        }

        homeNavGraph(navController)
        grouproutineNavGraph(
            navController = navController,
            onRoomCreated = { navController.popBackStack(MENU_ROUTE, inclusive = false) },
            onJoinedRoom = { navController.popBackStack(MENU_ROUTE, inclusive = false) },
        )
    }
}
