package com.li_routi.core.common.ui.routine

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.LiroutiBottomSheet
import com.li_routi.core.designsystem.component.LiroutiBottomSheetCloseButton
import com.li_routi.core.designsystem.component.LiroutiBottomSheetDeleteButton
import com.li_routi.core.designsystem.component.LiroutiBottomSheetField
import com.li_routi.core.designsystem.component.LiroutiBottomSheetInfoRow
import com.li_routi.core.designsystem.component.LiroutiBottomSheetRepeatField
import com.li_routi.core.designsystem.component.LiroutiChevronRightIcon
import com.li_routi.core.designsystem.component.LiroutiTextField
import com.li_routi.core.designsystem.theme.LiroutiTheme

private val ContentPadding = PaddingValues(top = 30.dp, start = 16.dp, end = 16.dp, bottom = 32.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineEditBottomSheet(
    name: String,
    onNameChange: (String) -> Unit,
    deadlineText: String,
    repeatText: String,
    selectedDays: Set<Int>,
    onDayClick: (Int) -> Unit,
    alarmText: String,
    onAlarmClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    namePlaceholder: String = "루틴 이름",
) {
    LiroutiBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        contentPadding = ContentPadding,
        primaryButtonText = "확인",
        onPrimaryButtonClick = onConfirm,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LiroutiBottomSheetCloseButton(onClick = onDismissRequest)
                LiroutiBottomSheetDeleteButton(onClick = onDeleteClick)
            }

            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                LiroutiTextField(
                    value = name,
                    onValueChange = onNameChange,
                    placeholder = namePlaceholder,
                    showLabel = false,
                    showHelper = false,
                )
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    LiroutiBottomSheetField(label = "마감시간", value = deadlineText)
                    LiroutiBottomSheetRepeatField(
                        value = repeatText,
                        selectedDays = selectedDays,
                        onDayClick = onDayClick,
                    )
                    LiroutiBottomSheetField(
                        label = "알람 시간",
                        value = alarmText,
                        modifier = Modifier.clickable(onClick = onAlarmClick),
                        trailing = {
                            LiroutiChevronRightIcon(
                                modifier = Modifier.size(20.dp),
                                color = LiroutiTheme.colors.labelSub,
                            )
                        },
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LiroutiBottomSheetInfoRow(
                    text = "다시 알림을 켜면 설정한 시작 시각부터 선택한 간격마다 완료하거나 마감될 때까지 알림이 와요.",
                )
                LiroutiBottomSheetInfoRow(text = "이 설정은 방 멤버 모두에게 동일하게 적용돼요.")
            }
        }
    }
}
