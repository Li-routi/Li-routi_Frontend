package com.li_routi.feature.grouproutine.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.li_routi.core.designsystem.component.LiroutiBottomSheet
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.grouproutine.R
import com.li_routi.feature.grouproutine.vm.CertificationPostUiModel
import com.li_routi.feature.grouproutine.vm.CreateRoutineOptionUiModel
import com.li_routi.feature.grouproutine.vm.GroupMemberUiModel
import com.li_routi.feature.grouproutine.vm.GroupRoutineScreenMode
import com.li_routi.feature.grouproutine.vm.GroupRoutineUiModel
import com.li_routi.feature.grouproutine.vm.GroupRoutineUiState
import com.li_routi.feature.grouproutine.vm.GroupRoutineViewModel
import com.li_routi.feature.grouproutine.vm.GroupTodoUiModel

private val ScreenBackground = Color(0xFFF4F7FB)
private val FillBackground = Color(0xFFFAFAFA)
private val LabelDefault = Color(0xFF171719)
private val LabelSub = Color(0xFF46474C)
private val LabelInfo = Color(0xFF878A93)
private val BorderDefault = Color(0xFFDBDCDF)
private val BorderStrong = Color(0xFFAEB0B6)
private val PrimaryNormal = Color(0xFF338AFF)
private val PrimaryActive = Color(0xFF296ECC)
private val SecondaryNormal = Color(0xFF00AAD2)
private val CompleteText = Color(0xFF008C51)
private val CompleteBackground = Color(0xFFE0F8E9)
private val DangerBase = Color(0xFFFF6363)

@Composable
fun GroupRoutineRoute(
    viewModel: GroupRoutineViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    GroupRoutineScreen(
        uiState = uiState,
        onRoutineClick = viewModel::onRoutineClick,
        onBackClick = viewModel::onBackClick,
        onAddClick = viewModel::onAddClick,
        onDismissActionSheet = viewModel::onDismissActionSheet,
        onCreateRoomClick = viewModel::onCreateRoomClick,
        onJoinByCodeClick = viewModel::onJoinByCodeClick,
        onRoomNameChange = viewModel::onRoomNameChange,
        onCreateRoomNextClick = viewModel::onCreateRoomNextClick,
        onCreateRoutineOptionClick = viewModel::onCreateRoutineOptionClick,
        onCreateRoutineSelectAllClick = viewModel::onCreateRoutineSelectAllClick,
        onCategoryClick = viewModel::onCategoryClick,
        onCategoryAddClick = viewModel::onCategoryAddClick,
        onDismissCategorySheet = viewModel::onDismissCategorySheet,
        onCategoryInputChange = viewModel::onCategoryInputChange,
        onCategoryConfirmClick = viewModel::onCategoryConfirmClick,
        onRoutineAddClick = viewModel::onRoutineAddClick,
        onRoutineSettingClick = viewModel::onRoutineSettingClick,
        onDismissRoutineSettingSheet = viewModel::onDismissRoutineSettingSheet,
        onRoutineDraftNameChange = viewModel::onRoutineDraftNameChange,
        onRepeatDayClick = viewModel::onRepeatDayClick,
        onRoutineSettingConfirmClick = viewModel::onRoutineSettingConfirmClick,
        onRoutineDeleteClick = viewModel::onRoutineDeleteClick,
        onDismissDeleteRoutineDialog = viewModel::onDismissDeleteRoutineDialog,
        onConfirmDeleteRoutineClick = viewModel::onConfirmDeleteRoutineClick,
        onCreateRoomDoneClick = viewModel::onCreateRoomDoneClick,
        onTodoCheckedChange = viewModel::onTodoCheckedChange,
        onCertificationTabClick = viewModel::onCertificationTabClick,
        onCertificationSummaryClick = viewModel::onCertificationSummaryClick,
        onChatClick = viewModel::onChatClick,
        onSettingsClick = viewModel::onSettingsClick,
    )
}

@Composable
private fun GroupRoutineScreen(
    uiState: GroupRoutineUiState,
    onRoutineClick: (Long) -> Unit,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onDismissActionSheet: () -> Unit,
    onCreateRoomClick: () -> Unit,
    onJoinByCodeClick: () -> Unit,
    onRoomNameChange: (String) -> Unit,
    onCreateRoomNextClick: () -> Unit,
    onCreateRoutineOptionClick: (Long) -> Unit,
    onCreateRoutineSelectAllClick: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onCategoryAddClick: () -> Unit,
    onDismissCategorySheet: () -> Unit,
    onCategoryInputChange: (String) -> Unit,
    onCategoryConfirmClick: () -> Unit,
    onRoutineAddClick: () -> Unit,
    onRoutineSettingClick: (Long) -> Unit,
    onDismissRoutineSettingSheet: () -> Unit,
    onRoutineDraftNameChange: (String) -> Unit,
    onRepeatDayClick: (String) -> Unit,
    onRoutineSettingConfirmClick: () -> Unit,
    onRoutineDeleteClick: () -> Unit,
    onDismissDeleteRoutineDialog: () -> Unit,
    onConfirmDeleteRoutineClick: () -> Unit,
    onCreateRoomDoneClick: () -> Unit,
    onTodoCheckedChange: (Long, Boolean) -> Unit,
    onCertificationTabClick: (Boolean) -> Unit,
    onCertificationSummaryClick: () -> Unit,
    onChatClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (uiState.screenMode) {
            GroupRoutineScreenMode.List -> GroupRoutineListScreen(
                uiState = uiState,
                onRoutineClick = onRoutineClick,
                onAddClick = onAddClick,
            )

            GroupRoutineScreenMode.Detail -> GroupRoutineDetailScreen(
                uiState = uiState,
                onBackClick = onBackClick,
                onTodoCheckedChange = onTodoCheckedChange,
                onCertificationTabClick = onCertificationTabClick,
                onCertificationSummaryClick = onCertificationSummaryClick,
                onChatClick = onChatClick,
                onSettingsClick = onSettingsClick,
            )

            GroupRoutineScreenMode.CertificationCollection -> CertificationCollectionScreen(
                uiState = uiState,
                onBackClick = onBackClick,
                onCertificationTabClick = onCertificationTabClick,
            )

            GroupRoutineScreenMode.GroupChat -> GroupChatScreen(
                uiState = uiState,
                onBackClick = onBackClick,
            )

            GroupRoutineScreenMode.GroupSettings -> GroupSettingsScreen(
                uiState = uiState,
                onBackClick = onBackClick,
            )

            GroupRoutineScreenMode.CreateRoomName -> CreateRoomNameScreen(
                roomName = uiState.roomNameInput,
                onRoomNameChange = onRoomNameChange,
                onBackClick = onBackClick,
                onNextClick = onCreateRoomNextClick,
            )

            GroupRoutineScreenMode.CreateRoutineSelect -> CreateRoutineSelectScreen(
                uiState = uiState,
                selectedCount = uiState.selectedCreateRoutineCount,
                onBackClick = onBackClick,
                onOptionClick = onCreateRoutineOptionClick,
                onSelectAllClick = onCreateRoutineSelectAllClick,
                onCategoryClick = onCategoryClick,
                onCategoryAddClick = onCategoryAddClick,
                onRoutineAddClick = onRoutineAddClick,
                onRoutineSettingClick = onRoutineSettingClick,
                onDoneClick = onCreateRoomDoneClick,
            )
        }

        uiState.actionMessage?.let { message ->
            Text(
                text = message,
                color = Color.White,
                style = LiroutiTheme.typography.body3,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 92.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(Color(0xCC171719))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            )
        }
    }

    if (uiState.isActionSheetVisible) {
        GroupRoutineActionSheet(
            onDismissRequest = onDismissActionSheet,
            onCreateRoomClick = onCreateRoomClick,
            onJoinByCodeClick = onJoinByCodeClick,
        )
    }

    if (uiState.isRoutineSettingSheetVisible) {
        RoutineSettingSheet(
            isEditing = uiState.editingRoutineId != null,
            routineName = uiState.routineDraftName,
            repeatDays = uiState.routineDraftRepeatDays,
            onDismissRequest = onDismissRoutineSettingSheet,
            onNameChange = onRoutineDraftNameChange,
            onRepeatDayClick = onRepeatDayClick,
            onDeleteClick = onRoutineDeleteClick,
            onConfirmClick = onRoutineSettingConfirmClick,
        )
    }

    if (uiState.isDeleteRoutineDialogVisible) {
        DeleteRoutineDialog(
            onDismissRequest = onDismissDeleteRoutineDialog,
            onConfirmClick = onConfirmDeleteRoutineClick,
        )
    }

    if (uiState.isCategorySheetVisible) {
        CategoryAddSheet(
            category = uiState.categoryInput,
            onDismissRequest = onDismissCategorySheet,
            onCategoryChange = onCategoryInputChange,
            onConfirmClick = onCategoryConfirmClick,
        )
    }
}

@Composable
private fun GroupRoutineListScreen(
    uiState: GroupRoutineUiState,
    onRoutineClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBackground),
    ) {
        if (uiState.visibleRoutines.isEmpty()) {
            GroupRoutineEmptyState(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, top = 112.dp, end = 16.dp, bottom = 112.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { GroupRoutineSearchField() }
                items(uiState.visibleRoutines) { routine ->
                    GroupRoutineCard(routine = routine, onClick = { onRoutineClick(routine.id) })
                }
            }
        }

        GroupRoutineTopBar(
            title = "그룹 루틴",
            showAdd = true,
            onAddClick = onAddClick,
            modifier = Modifier.align(Alignment.TopCenter),
        )
        GroupRoutineBottomBar(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun CreateRoomNameScreen(
    roomName: String,
    onRoomNameChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp, start = 16.dp, end = 16.dp, bottom = 104.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "어떤 방을 만들까요?",
                color = LabelDefault,
                style = LiroutiTheme.typography.heading2.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = "친구들이 방 목록에서 이 이름으로 보게 돼요.",
                color = LabelInfo,
                style = LiroutiTheme.typography.body3,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "방이름",
                color = LabelDefault,
                style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Bold),
            )
            BasicInputBox(
                value = roomName,
                onValueChange = onRoomNameChange,
                placeholder = "최대 20자",
                showClear = false,
            )
        }

        GroupRoutineTopBar(
            title = "방 만들기",
            showBack = true,
            onBackClick = onBackClick,
            modifier = Modifier.align(Alignment.TopCenter),
        )
        BottomFixedButton(
            text = "다음",
            enabled = roomName.isNotBlank(),
            onClick = onNextClick,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun CreateRoutineSelectScreen(
    uiState: GroupRoutineUiState,
    selectedCount: Int,
    onBackClick: () -> Unit,
    onOptionClick: (Long) -> Unit,
    onSelectAllClick: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onCategoryAddClick: () -> Unit,
    onRoutineAddClick: () -> Unit,
    onRoutineSettingClick: (Long) -> Unit,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBackground),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, top = 100.dp, end = 16.dp, bottom = 132.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "함께할 루틴을 추가해보세요",
                        color = LabelDefault,
                        style = LiroutiTheme.typography.heading2.copy(fontWeight = FontWeight.Bold),
                    )
                    Text(
                        text = "루틴을 누르면 세부 설정을 변경할 수 있어요",
                        color = LabelInfo,
                        style = LiroutiTheme.typography.body3,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CategoryChipRow(
                        categories = uiState.categories,
                        selectedCategory = uiState.selectedCategory,
                        onCategoryClick = onCategoryClick,
                        onCategoryAddClick = onCategoryAddClick,
                    )
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 390.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White)
                        .border(1.dp, BorderDefault, RoundedCornerShape(6.dp))
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                ) {
                    SelectAllRoutineRow(
                        checked = uiState.allVisibleRoutineOptionsSelected,
                        onClick = onSelectAllClick,
                    )
                    HorizontalDivider(color = BorderDefault)
                    uiState.visibleRoutineOptions.forEach { option ->
                        CreateRoutineOptionRow(
                            option = option,
                            onCheckClick = { onOptionClick(option.id) },
                            onSettingClick = { onRoutineSettingClick(option.id) },
                        )
                    }
                }
            }
            item {
                DashedRoutineAddButton(onClick = onRoutineAddClick)
            }
        }

        GroupRoutineTopBar(
            title = "루틴 추가",
            showBack = true,
            onBackClick = onBackClick,
            modifier = Modifier.align(Alignment.TopCenter),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "총 ${selectedCount}개 선택됨",
                color = LabelSub,
                style = LiroutiTheme.typography.body3,
                modifier = Modifier.align(Alignment.End),
            )
            PrimaryButton(text = "완료", enabled = selectedCount > 0, onClick = onDoneClick)
        }
    }
}

@Composable
private fun CategoryChipRow(
    categories: List<String>,
    selectedCategory: String,
    onCategoryClick: (String) -> Unit,
    onCategoryAddClick: () -> Unit,
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { label ->
            val selected = label == selectedCategory
            Text(
                text = if (selected) "✓ $label" else label,
                color = if (selected) Color.White else LabelSub,
                style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(if (selected) PrimaryNormal else Color.White)
                    .border(1.dp, if (selected) PrimaryNormal else BorderDefault, RoundedCornerShape(100.dp))
                    .clickable { onCategoryClick(label) }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            )
        }
        item {
            Text(
                text = "+",
                color = LabelSub,
                style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Medium),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .size(width = 42.dp, height = 42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .border(1.dp, BorderDefault, RoundedCornerShape(10.dp))
                    .clickable(onClick = onCategoryAddClick)
                    .padding(top = 9.dp),
            )
        }
    }
}

@Composable
private fun SelectAllRoutineRow(
    checked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SmallSquareCheckbox(checked = checked)
        Text(
            text = "전체 선택",
            color = LabelDefault,
            style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Bold),
        )
    }
}

@Composable
private fun CreateRoutineOptionRow(
    option: CreateRoutineOptionUiModel,
    onCheckClick: () -> Unit,
    onSettingClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onSettingClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(modifier = Modifier.clickable(onClick = onCheckClick)) {
            SmallSquareCheckbox(checked = option.isSelected)
        }
        Text(
            text = option.title,
            color = LabelDefault,
            style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SmallSquareCheckbox(checked: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(18.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(if (checked) PrimaryNormal else Color.White)
            .border(1.dp, if (checked) PrimaryNormal else BorderStrong, RoundedCornerShape(2.dp)),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Text(text = "✓", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DashedRoutineAddButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "루틴 추가  +",
        color = LabelDefault,
        style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Medium),
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(45.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White)
            .border(1.dp, BorderDefault, RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .padding(top = 12.dp),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoutineSettingSheet(
    isEditing: Boolean,
    routineName: String,
    repeatDays: Set<String>,
    onDismissRequest: () -> Unit,
    onNameChange: (String) -> Unit,
    onRepeatDayClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    LiroutiBottomSheet(
        onDismissRequest = onDismissRequest,
        contentPadding = PaddingValues(start = 20.dp, top = 24.dp, end = 20.dp, bottom = 28.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "×",
                    color = LabelDefault,
                    fontSize = 30.sp,
                    modifier = Modifier.clickable(onClick = onDismissRequest),
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "삭제",
                    color = DangerBase,
                    style = LiroutiTheme.typography.body2Long,
                    modifier = Modifier.clickable(onClick = onDeleteClick),
                )
            }
            BasicInputBox(
                value = routineName,
                onValueChange = onNameChange,
                placeholder = "루틴 이름",
                showClear = routineName.isNotEmpty(),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(FillBackground),
            ) {
                SettingInfoRow(label = "마감시간", value = "오후 11:00")
                SettingInfoRow(label = "반복", value = repeatDaysLabel(repeatDays))
                RepeatDayRow(selectedDays = repeatDays, onRepeatDayClick = onRepeatDayClick)
                SettingInfoRow(label = "알람 시간", value = "없음  >")
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "• 다시 알림을 켜면 설정한 시작 시각부터 선택한 간격마다 완료하거나 마감될 때까지 알림이 와요.",
                    color = LabelInfo,
                    style = LiroutiTheme.typography.caption,
                )
                Text(
                    text = "• 이 설정은 방 멤버 모두에게 동일하게 적용돼요.",
                    color = LabelInfo,
                    style = LiroutiTheme.typography.caption,
                )
            }
            PrimaryButton(text = "확인", enabled = routineName.isNotBlank(), onClick = onConfirmClick)
        }
    }
}

@Composable
private fun BasicInputBox(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    showClear: Boolean,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = LiroutiTheme.typography.body2Long.copy(color = LabelDefault),
        placeholder = {
            Text(text = placeholder, color = LabelInfo, style = LiroutiTheme.typography.body2Long)
        },
        trailingIcon = {
            if (showClear) {
                Text(
                    text = "×",
                    color = LabelInfo,
                    fontSize = 18.sp,
                    modifier = Modifier.clickable { onValueChange("") },
                )
            }
        },
        shape = RoundedCornerShape(6.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedIndicatorColor = BorderDefault,
            unfocusedIndicatorColor = BorderDefault,
            cursorColor = PrimaryNormal,
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
    )
}

@Composable
private fun SettingInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, color = LabelSub, style = LiroutiTheme.typography.body2Long)
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            color = LabelSub,
            style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Bold),
        )
    }
}

@Composable
private fun RepeatDayRow(
    selectedDays: Set<String>,
    onRepeatDayClick: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        listOf("일", "월", "화", "수", "목", "금", "토").forEach { day ->
            val selected = day in selectedDays
            Text(
                text = day,
                color = if (selected) Color.White else LabelInfo,
                style = LiroutiTheme.typography.body2Long,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(if (selected) LabelSub else Color.White)
                    .border(1.dp, if (selected) LabelSub else BorderDefault, CircleShape)
                    .clickable { onRepeatDayClick(day) }
                    .padding(top = 7.dp),
            )
        }
    }
}

private fun repeatDaysLabel(days: Set<String>): String {
    return when (days) {
        emptySet<String>() -> "없음"
        setOf("일", "월", "화", "수", "목", "금", "토") -> "매일"
        setOf("월", "화", "수", "목", "금") -> "주중"
        setOf("일", "토") -> "주말"
        else -> days.joinToString(" ")
    }
}

@Composable
private fun DeleteRoutineDialog(
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "삭제하기",
                    color = LabelDefault,
                    style = LiroutiTheme.typography.heading2.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f),
                )
                Text(text = "×", color = LabelDefault, fontSize = 28.sp, modifier = Modifier.clickable(onClick = onDismissRequest))
            }
            Text(
                text = "작성 중이던 루틴이 삭제됩니다.",
                color = LabelSub,
                style = LiroutiTheme.typography.body2Long,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onDismissRequest,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FillBackground, contentColor = LabelDefault),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                ) {
                    Text(text = "취소")
                }
                Button(
                    onClick = onConfirmClick,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE55454), contentColor = Color.White),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                ) {
                    Text(text = "삭제")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryAddSheet(
    category: String,
    onDismissRequest: () -> Unit,
    onCategoryChange: (String) -> Unit,
    onConfirmClick: () -> Unit,
) {
    LiroutiBottomSheet(
        onDismissRequest = onDismissRequest,
        contentPadding = PaddingValues(start = 20.dp, top = 30.dp, end = 20.dp, bottom = 28.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "×", color = LabelDefault, fontSize = 28.sp, modifier = Modifier.clickable(onClick = onDismissRequest))
            }
            Text(
                text = "카테고리",
                color = LabelDefault,
                style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Bold),
            )
            BasicInputBox(
                value = category,
                onValueChange = onCategoryChange,
                placeholder = "최대 20자",
                showClear = false,
            )
            PrimaryButton(text = "확인", enabled = category.isNotBlank(), onClick = onConfirmClick)
        }
    }
}

@Composable
private fun BottomFixedButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .navigationBarsPadding()
            .padding(16.dp),
    ) {
        PrimaryButton(text = text, enabled = enabled, onClick = onClick)
    }
}

@Composable
private fun PrimaryButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryNormal,
            disabledContainerColor = Color(0xFFC8D9F3),
            contentColor = Color.White,
            disabledContentColor = Color.White,
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
    ) {
        Text(text = text, style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Bold))
    }
}

@Composable
private fun GroupRoutineSearchField(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .border(1.dp, BorderDefault, RoundedCornerShape(6.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "⌕", color = LabelDefault, fontSize = 24.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "그룹방 검색",
            color = LabelInfo,
            style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Medium),
        )
    }
}

@Composable
private fun GroupRoutineEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(bottom = 76.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(text = "!", color = LabelInfo, fontSize = 26.sp)
        Text(
            text = "아직 만들어진 방이 없어요!",
            color = LabelInfo,
            style = LiroutiTheme.typography.body3,
        )
    }
}

@Composable
private fun GroupRoutineCard(
    routine: GroupRoutineUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RoutineIconBox(size = 50)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = routine.title,
                        color = LabelDefault,
                        style = LiroutiTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = routine.lastActiveLabel,
                        color = PrimaryNormal,
                        style = LiroutiTheme.typography.caption,
                        maxLines = 1,
                    )
                }
                Text(
                    text = "멤버 ${routine.memberCount}명  |  루틴 ${routine.routineCount}개",
                    color = Color.Black,
                    style = LiroutiTheme.typography.caption.copy(fontSize = 11.sp, lineHeight = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            StatusBadge(label = routine.statusLabel, completed = routine.isCompleted)
        }

        HorizontalDivider(color = BorderDefault)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            AvatarStack()
            Text(
                text = "오늘 ${routine.todayCompletedCount}/${routine.todayTotalCount} 완료",
                color = LabelInfo,
                style = LiroutiTheme.typography.caption,
            )
        }
        RoutineStatsRow(routine = routine)
    }
}

@Composable
private fun StatusBadge(
    label: String,
    completed: Boolean,
    modifier: Modifier = Modifier,
) {
    Text(
        text = label,
        color = if (completed) CompleteText else SecondaryNormal,
        style = LiroutiTheme.typography.caption.copy(fontSize = 11.sp, lineHeight = 14.sp),
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (completed) CompleteBackground else ScreenBackground)
            .padding(horizontal = 7.dp, vertical = 3.dp),
    )
}

@Composable
private fun AvatarStack(modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8EAEB))
                    .border(0.6.dp, BorderDefault, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_group_routine_member),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

@Composable
private fun RoutineStatsRow(
    routine: GroupRoutineUiModel,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(74.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(FillBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        StatItem(value = "${routine.streakDays}일", label = "연속 달성")
        VerticalStatDivider()
        StatItem(value = "${routine.monthlyAchievementRate}%", label = "이번 달성률")
        VerticalStatDivider()
        StatItem(value = "${routine.todayCertificationCount}건", label = "오늘 인증")
    }
}

@Composable
private fun StatItem(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.width(76.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = value,
            color = LabelSub,
            style = LiroutiTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = LabelSub,
            style = LiroutiTheme.typography.body3,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}

@Composable
private fun VerticalStatDivider() {
    Box(
        modifier = Modifier
            .height(50.dp)
            .width(1.dp)
            .background(BorderDefault),
    )
}

@Composable
private fun GroupRoutineDetailScreen(
    uiState: GroupRoutineUiState,
    onBackClick: () -> Unit,
    onTodoCheckedChange: (Long, Boolean) -> Unit,
    onCertificationTabClick: (Boolean) -> Unit,
    onCertificationSummaryClick: () -> Unit,
    onChatClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val routine = uiState.selectedRoutine ?: return

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBackground),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 93.dp, bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            item {
                CertificationSummaryCard(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    streakLabel = "12일 연속 모든 루틴 달성!",
                    onClick = onCertificationSummaryClick,
                )
            }
            item {
                GroupMemberCard(title = routine.title, members = uiState.members, modifier = Modifier.padding(horizontal = 16.dp))
            }
            item {
                GroupTodoCard(
                    todos = uiState.todos,
                    progressLabel = uiState.todoProgressLabel,
                    onTodoCheckedChange = onTodoCheckedChange,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            item {
                CertificationFeedCard(
                    posts = if (uiState.showOnlyMyCertifications) uiState.posts.filter { it.isMine } else uiState.posts,
                    showOnlyMine = uiState.showOnlyMyCertifications,
                    onTabClick = onCertificationTabClick,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }

        GroupRoutineTopBar(
            title = routine.title,
            showBack = true,
            showActions = true,
            onBackClick = onBackClick,
            onChatClick = onChatClick,
            onSettingsClick = onSettingsClick,
            modifier = Modifier.align(Alignment.TopCenter),
        )
        GroupRoutineBottomBar(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun CertificationCollectionScreen(
    uiState: GroupRoutineUiState,
    onBackClick: () -> Unit,
    onCertificationTabClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBackground),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 69.dp, bottom = 104.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                CertificationFeedCard(
                    posts = if (uiState.showOnlyMyCertifications) uiState.posts.filter { it.isMine } else uiState.posts,
                    showOnlyMine = uiState.showOnlyMyCertifications,
                    onTabClick = onCertificationTabClick,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }

        GroupRoutineTopBar(
            title = "인증 모아보기",
            showBack = true,
            onBackClick = onBackClick,
            modifier = Modifier.align(Alignment.TopCenter),
        )
        GroupRoutineBottomBar(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun GroupChatScreen(
    uiState: GroupRoutineUiState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val routine = uiState.selectedRoutine ?: return
    val messages = listOf(
        Triple("민지", "오늘 물 마시기 인증했어요!", false),
        Triple("서현", "저녁 전에 스트레칭 같이 해요.", false),
        Triple("나", "좋아요. 9시에 체크할게요.", true),
        Triple("민지", "이번 주도 연속 달성 가봅시다!", false),
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBackground),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 72.dp, start = 16.dp, end = 16.dp, bottom = 104.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(
                    text = routine.title,
                    color = LabelInfo,
                    style = LiroutiTheme.typography.caption,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
            items(messages) { (name, message, isMine) ->
                ChatMessageBubble(name = name, message = message, isMine = isMine)
            }
        }

        ChatInputBar(modifier = Modifier.align(Alignment.BottomCenter))
        GroupRoutineTopBar(
            title = "그룹방 채팅",
            showBack = true,
            onBackClick = onBackClick,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}

@Composable
private fun ChatMessageBubble(
    name: String,
    message: String,
    isMine: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.76f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isMine) PrimaryNormal else Color.White)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = name,
                color = if (isMine) Color.White.copy(alpha = 0.82f) else LabelInfo,
                style = LiroutiTheme.typography.caption,
            )
            Text(
                text = message,
                color = if (isMine) Color.White else LabelDefault,
                style = LiroutiTheme.typography.body3,
            )
        }
    }
}

@Composable
private fun ChatInputBar(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .background(Color.White)
            .border(1.dp, BorderDefault)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(42.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(FillBackground)
                .border(1.dp, BorderDefault, RoundedCornerShape(6.dp))
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(text = "메시지 입력", color = LabelInfo, style = LiroutiTheme.typography.body3)
        }
        Text(text = "전송", color = PrimaryNormal, style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Bold))
    }
}

@Composable
private fun GroupSettingsScreen(
    uiState: GroupRoutineUiState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val routine = uiState.selectedRoutine ?: return

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBackground),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 70.dp, bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 20.dp, vertical = 22.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(text = routine.title, color = LabelDefault, style = LiroutiTheme.typography.heading2.copy(fontWeight = FontWeight.Bold))
                    Text(text = "멤버 ${routine.memberCount}명 · 루틴 ${routine.routineCount}개", color = LabelInfo, style = LiroutiTheme.typography.caption)
                }
            }
            item {
                Column(modifier = Modifier.background(Color.White)) {
                    SettingsMenuRow(label = "방 이름", value = routine.title)
                    SettingsMenuRow(label = "멤버 관리", value = "${uiState.members.size}명")
                    SettingsMenuRow(label = "초대코드", value = "공유")
                    SettingsMenuRow(label = "알림 설정", value = "켜짐")
                    SettingsMenuRow(label = "루틴 관리", value = "${uiState.todos.size}개")
                }
            }
            item {
                SettingsMenuRow(
                    label = "방 나가기",
                    value = "",
                    labelColor = DangerBase,
                    modifier = Modifier.background(Color.White),
                )
            }
        }

        GroupRoutineTopBar(
            title = "방 설정",
            showBack = true,
            onBackClick = onBackClick,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}

@Composable
private fun SettingsMenuRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    labelColor: Color = LabelDefault,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = label,
            color = labelColor,
            style = LiroutiTheme.typography.body2.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1f),
        )
        if (value.isNotBlank()) {
            Text(text = value, color = LabelInfo, style = LiroutiTheme.typography.body3)
        }
        Text(text = ">", color = LabelInfo, fontSize = 18.sp)
    }
}

@Composable
private fun CertificationSummaryCard(
    streakLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF3F4F5)),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "F", color = DangerBase, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(
                text = "인증 모아보기",
                color = LabelDefault,
                style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Bold),
            )
            Text(text = streakLabel, color = LabelInfo, style = LiroutiTheme.typography.caption)
        }
        Text(text = ">", color = LabelDefault, fontSize = 20.sp)
    }
}

@Composable
private fun GroupMemberCard(
    title: String,
    members: List<GroupMemberUiModel>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = title,
            color = LabelDefault,
            style = LiroutiTheme.typography.heading2.copy(fontWeight = FontWeight.Bold),
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .height(224.dp),
            userScrollEnabled = false,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            items(members) { member -> MemberSeat(member = member) }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextAction(label = "메시지 수정")
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .width(1.dp)
                    .height(12.dp)
                    .background(BorderDefault),
            )
            TextAction(label = "초대코드")
        }
    }
}

@Composable
private fun MemberSeat(member: GroupMemberUiModel) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = member.message,
                color = Color.White,
                fontSize = 11.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF878A93))
                    .padding(horizontal = 8.dp, vertical = 5.dp),
            )
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_group_routine_character),
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            if (member.isMe) StatusBadge(label = "나", completed = false)
            Text(
                text = member.name,
                color = LabelDefault,
                style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Medium),
                maxLines = 1,
            )
            Text(text = "F${member.streak}", color = DangerBase, fontSize = 10.sp)
        }
    }
}

@Composable
private fun GroupTodoCard(
    todos: List<GroupTodoUiModel>,
    progressLabel: String,
    onTodoCheckedChange: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "그룹 루틴",
            color = LabelDefault,
            style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Bold),
        )
        todos.forEach { todo ->
            TodoRow(todo = todo, onCheckedChange = { checked -> onTodoCheckedChange(todo.id, checked) })
        }
        HorizontalDivider(color = BorderDefault)
        Text(
            text = progressLabel,
            color = LabelSub,
            style = LiroutiTheme.typography.caption,
            modifier = Modifier.align(Alignment.End),
        )
    }
}

@Composable
private fun TodoRow(
    todo: GroupTodoUiModel,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Checkbox(
            checked = todo.isDone,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = PrimaryNormal, uncheckedColor = BorderStrong),
            modifier = Modifier.size(22.dp),
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = todo.title,
                color = if (todo.isDone) LabelInfo else LabelDefault,
                style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Medium),
                maxLines = 1,
            )
            if (!todo.isDone) {
                Text(
                    text = "마감 ${todo.deadline}  |  ${todo.category}",
                    color = LabelInfo,
                    style = LiroutiTheme.typography.caption,
                    maxLines = 1,
                )
            }
        }
        if (todo.isDone) {
            Text(
                text = "완료",
                color = LabelInfo,
                style = LiroutiTheme.typography.caption,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFEAEBEC))
                    .padding(horizontal = 7.dp, vertical = 3.dp),
            )
        } else {
            Text(text = "□", color = LabelInfo, fontSize = 14.sp)
        }
    }
}

@Composable
private fun CertificationFeedCard(
    posts: List<CertificationPostUiModel>,
    showOnlyMine: Boolean,
    onTabClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FeedTab(text = "인증", selected = !showOnlyMine, onClick = { onTabClick(false) })
            FeedTab(text = "내 인증 보기", selected = showOnlyMine, onClick = { onTabClick(true) })
        }
        posts.forEach { post -> CertificationPostItem(post = post) }
    }
}

@Composable
private fun FeedTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Text(
        text = text,
        color = if (selected) Color.White else LabelDefault,
        style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Medium),
        modifier = Modifier
            .clip(RoundedCornerShape(40.dp))
            .background(if (selected) PrimaryNormal else ScreenBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
    )
}

@Composable
private fun CertificationPostItem(
    post: CertificationPostUiModel,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDFF6EC)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = post.userName.take(1),
                    color = SecondaryNormal,
                    style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Bold),
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = post.userName,
                color = LabelDefault,
                style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Bold),
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "...", color = LabelInfo, fontSize = 18.sp)
        }
        Text(text = post.body, color = LabelDefault, style = LiroutiTheme.typography.body2Long)
        Image(
            painter = painterResource(id = R.drawable.img_group_routine_cert_water),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(144.dp)
                .clip(RoundedCornerShape(6.dp)),
        )
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Like ", color = LabelDefault, fontSize = 12.sp)
            Text(
                text = post.likeCount.toString(),
                color = LabelDefault,
                style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Medium),
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(text = post.timeAgo, color = LabelInfo, style = LiroutiTheme.typography.body3)
        }
    }
}

@Composable
private fun RoutineIconBox(
    size: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(ScreenBackground),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_group_routine_character),
            contentDescription = null,
            modifier = Modifier.size((size - 8).dp),
        )
    }
}

@Composable
private fun TextAction(label: String) {
    Text(
        text = label,
        color = LabelSub,
        style = LiroutiTheme.typography.body2Long,
        modifier = Modifier.padding(4.dp),
    )
}

@Composable
private fun GroupRoutineTopBar(
    title: String,
    modifier: Modifier = Modifier,
    showBack: Boolean = false,
    showAdd: Boolean = false,
    showActions: Boolean = false,
    onBackClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onChatClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = Color.White,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 16.dp),
        ) {
            if (showBack) {
                Text(
                    text = "<",
                    color = LabelDefault,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable(onClick = onBackClick),
                )
            }
            Text(
                text = title,
                color = LabelDefault,
                style = LiroutiTheme.typography.heading2.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.Center),
            )
            if (showAdd) {
                Text(
                    text = "+",
                    color = LabelDefault,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable(onClick = onAddClick),
                )
            }
            if (showActions) {
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "채팅",
                        color = LabelDefault,
                        fontSize = 11.sp,
                        modifier = Modifier.clickable(onClick = onChatClick),
                    )
                    Text(
                        text = "설정",
                        color = LabelDefault,
                        fontSize = 11.sp,
                        modifier = Modifier.clickable(onClick = onSettingsClick),
                    )
                }
            }
        }
    }
}

@Composable
private fun GroupRoutineBottomBar(
    modifier: Modifier = Modifier,
) {
    val items = listOf(
        BottomNavItem(R.drawable.ic_group_routine_home, "홈"),
        BottomNavItem(R.drawable.ic_group_routine_group_active, "그룹 루틴"),
        BottomNavItem(R.drawable.ic_group_routine_challenge, "챌린지"),
        BottomNavItem(R.drawable.ic_group_routine_my, "마이"),
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(1.dp, BorderDefault)
            .navigationBarsPadding()
            .height(80.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEach { item ->
            val active = item.label == "그룹 루틴"
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Image(
                    painter = painterResource(id = item.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.label,
                    color = if (active) PrimaryActive else LabelInfo,
                    style = LiroutiTheme.typography.caption.copy(fontSize = 10.sp, lineHeight = 14.sp),
                    maxLines = 1,
                )
            }
        }
    }
}

private data class BottomNavItem(
    @param:DrawableRes val iconRes: Int,
    val label: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GroupRoutineActionSheet(
    onDismissRequest: () -> Unit,
    onCreateRoomClick: () -> Unit,
    onJoinByCodeClick: () -> Unit,
) {
    LiroutiBottomSheet(
        onDismissRequest = onDismissRequest,
        contentPadding = PaddingValues(24.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            BottomSheetActionRow(label = "방 만들기", onClick = onCreateRoomClick)
            BottomSheetActionRow(label = "초대코드로 참여", onClick = onJoinByCodeClick)
        }
    }
}

@Composable
private fun BottomSheetActionRow(
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = label,
            color = LabelDefault,
            style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.weight(1f),
        )
        Text(text = ">", color = LabelDefault, fontSize = 20.sp)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun GroupRoutineListPreview() {
    LiroutiFrontendTheme {
        GroupRoutineScreen(
            uiState = GroupRoutineUiState(),
            onRoutineClick = {},
            onBackClick = {},
            onAddClick = {},
            onDismissActionSheet = {},
            onCreateRoomClick = {},
            onJoinByCodeClick = {},
            onRoomNameChange = {},
            onCreateRoomNextClick = {},
            onCreateRoutineOptionClick = {},
            onCreateRoutineSelectAllClick = {},
            onCategoryClick = {},
            onCategoryAddClick = {},
            onDismissCategorySheet = {},
            onCategoryInputChange = {},
            onCategoryConfirmClick = {},
            onRoutineAddClick = {},
            onRoutineSettingClick = {},
            onDismissRoutineSettingSheet = {},
            onRoutineDraftNameChange = {},
            onRepeatDayClick = {},
            onRoutineSettingConfirmClick = {},
            onRoutineDeleteClick = {},
            onDismissDeleteRoutineDialog = {},
            onConfirmDeleteRoutineClick = {},
              onCreateRoomDoneClick = {},
              onTodoCheckedChange = { _, _ -> },
              onCertificationTabClick = {},
              onCertificationSummaryClick = {},
              onChatClick = {},
              onSettingsClick = {},
          )
      }
  }

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun CreateRoomNamePreview() {
    LiroutiFrontendTheme {
        GroupRoutineScreen(
            uiState = GroupRoutineUiState(screenMode = GroupRoutineScreenMode.CreateRoomName, roomNameInput = "코딩"),
            onRoutineClick = {},
            onBackClick = {},
            onAddClick = {},
            onDismissActionSheet = {},
            onCreateRoomClick = {},
            onJoinByCodeClick = {},
            onRoomNameChange = {},
            onCreateRoomNextClick = {},
            onCreateRoutineOptionClick = {},
            onCreateRoutineSelectAllClick = {},
            onCategoryClick = {},
            onCategoryAddClick = {},
            onDismissCategorySheet = {},
            onCategoryInputChange = {},
            onCategoryConfirmClick = {},
            onRoutineAddClick = {},
            onRoutineSettingClick = {},
            onDismissRoutineSettingSheet = {},
            onRoutineDraftNameChange = {},
            onRepeatDayClick = {},
            onRoutineSettingConfirmClick = {},
            onRoutineDeleteClick = {},
            onDismissDeleteRoutineDialog = {},
            onConfirmDeleteRoutineClick = {},
              onCreateRoomDoneClick = {},
              onTodoCheckedChange = { _, _ -> },
              onCertificationTabClick = {},
              onCertificationSummaryClick = {},
              onChatClick = {},
              onSettingsClick = {},
          )
      }
  }
