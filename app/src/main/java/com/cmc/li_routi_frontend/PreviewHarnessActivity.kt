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
import com.li_routi.core.common.ui.routine.RoutineDeleteDialog
import com.li_routi.core.common.ui.routine.RoutineEditBottomSheet
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreviewHarnessScreen() {
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
        Text(text = "Preview Harness (issue #18/#19/#20 확인용, 임시)")
        Button(onClick = { showRoutineSheet = true }) {
            Text(text = "루틴 추가/편집 바텀시트 (#18)")
        }
        Button(onClick = { showDeleteDialog = true }) {
            Text(text = "루틴 삭제 확인 다이얼로그 (#19)")
        }
        Button(onClick = { showCategorySheet = true }) {
            Text(text = "카테고리 추가 바텀시트 (#20)")
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
