package com.li_routi.core.common.ui.routine

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.LiroutiBottomSheetCloseButton
import com.li_routi.core.designsystem.component.LiroutiBottomSheetDeleteButton
import com.li_routi.core.designsystem.component.LiroutiBottomSheetField
import com.li_routi.core.designsystem.component.LiroutiBottomSheetInfoRow
import com.li_routi.core.designsystem.component.LiroutiBottomSheetRepeatField
import com.li_routi.core.designsystem.component.LiroutiBottomSheetTimeField
import com.li_routi.core.designsystem.component.LiroutiChevronRightIcon
import com.li_routi.core.designsystem.component.LiroutiClockTime
import com.li_routi.core.designsystem.component.LiroutiTextField
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

private val ContentPadding = PaddingValues(top = 30.dp, start = 16.dp, end = 16.dp, bottom = 32.dp)
private val SheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

private enum class RoutineEditExpandedSection {
    None,
    StartTime,
    EndTime,
    Repeat,
}

/**
 * 바깥 탭·시스템 뒤로가기로 [SheetValue.Hidden]으로 전환되려 할 때, 초안이 있으면
 * `confirmValueChange`에서 전환을 막고(false 반환) 대신 [onDismissRequest]만 호출한다
 * (실제 닫기 여부는 그쪽의 이탈 확인 다이얼로그가 결정). 초안이 없으면 평소처럼 닫히게 둔다.
 *
 * `confirmValueChange` 람다는 [rememberModalBottomSheetState] 내부에서 `rememberSaveable`의
 * key로도 쓰이므로, 매 재구성마다 새 람다를 넘기면 SheetState가 계속 재생성돼 애니메이션이 깨진다.
 * 그래서 람다 자체는 `remember`로 한 번만 만들고, 최신 [hasDraft]/[onDismissRequest] 값은
 * [rememberUpdatedState]로 참조한다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun rememberRoutineEditSheetState(
    hasDraft: Boolean,
    onDismissRequest: () -> Unit,
): SheetState {
    val currentHasDraft = rememberUpdatedState(hasDraft)
    val currentOnDismissRequest = rememberUpdatedState(onDismissRequest)
    val confirmValueChange = remember {
        { target: SheetValue ->
            if (target == SheetValue.Hidden && currentHasDraft.value) {
                currentOnDismissRequest.value()
                false
            } else {
                true
            }
        }
    }
    return rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = confirmValueChange,
    )
}

/**
 * 루틴 생성/편집 Bottom Sheet.
 *
 * Figma `루틴 추가` 편집 시트: 이름 + 시작시간/마감시간(인라인 휠) + 반복(요일) + 확인.
 * Design Page [1.1]에는 알람 행이 없다. [showAlarmSection] 기본값 false.
 * 프리뷰: 「내 루틴 편집」/「루틴 관리 추가」.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineEditBottomSheet(
    name: String,
    onNameChange: (String) -> Unit,
    startTime: LiroutiClockTime,
    onStartTimeChange: (LiroutiClockTime) -> Unit,
    endTime: LiroutiClockTime,
    onEndTimeChange: (LiroutiClockTime) -> Unit,
    selectedDays: Set<Int>,
    onDayClick: (Int) -> Unit,
    onDeleteClick: () -> Unit,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    /**
     * 작성 중인 이름/시간/반복 등 초안이 있는지. true인 상태로 바깥 탭·시스템 뒤로가기가 들어오면
     * 시트가 그대로 닫히지 않고 [onDismissRequest](이탈 확인 다이얼로그 분기)만 호출한다.
     */
    hasDraft: Boolean = false,
    sheetState: SheetState = rememberRoutineEditSheetState(hasDraft, onDismissRequest),
    namePlaceholder: String = "루틴 이름",
    alarmText: String = "없음",
    onAlarmClick: (() -> Unit)? = null,
    showAlarmSection: Boolean = false,
    showRoomInfo: Boolean = false,
) {
    // design-system LiroutiBottomSheet radius(6)와 달리 Figma `3610:26830`은 top 20
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        shape = SheetShape,
        containerColor = LiroutiTheme.colors.backgroundDefault,
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ContentPadding),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            RoutineEditBottomSheetBody(
                name = name,
                onNameChange = onNameChange,
                startTime = startTime,
                onStartTimeChange = onStartTimeChange,
                endTime = endTime,
                onEndTimeChange = onEndTimeChange,
                selectedDays = selectedDays,
                onDayClick = onDayClick,
                onDeleteClick = onDeleteClick,
                onDismissRequest = onDismissRequest,
                namePlaceholder = namePlaceholder,
                alarmText = alarmText,
                onAlarmClick = onAlarmClick,
                showAlarmSection = showAlarmSection,
                showRoomInfo = showRoomInfo,
            )
            // 반복 요일을 하나도 고르지 않으면 저장할 수 없으므로 확인 버튼을 비활성화한다.
            val confirmEnabled = name.isNotBlank() && selectedDays.isNotEmpty()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (confirmEnabled) {
                            LiroutiTheme.colors.primaryNormal
                        } else {
                            LiroutiTheme.colors.primaryDisabled
                        },
                    )
                    .clickable(enabled = confirmEnabled, onClick = onConfirm),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "확인",
                    style = LiroutiTheme.typography.body2Medium,
                    color = LiroutiTheme.colors.backgroundAlternative,
                )
            }
        }
    }
}

@Composable
private fun RoutineEditBottomSheetBody(
    name: String,
    onNameChange: (String) -> Unit,
    startTime: LiroutiClockTime,
    onStartTimeChange: (LiroutiClockTime) -> Unit,
    endTime: LiroutiClockTime,
    onEndTimeChange: (LiroutiClockTime) -> Unit,
    selectedDays: Set<Int>,
    onDayClick: (Int) -> Unit,
    onDeleteClick: () -> Unit,
    onDismissRequest: () -> Unit,
    namePlaceholder: String,
    alarmText: String,
    onAlarmClick: (() -> Unit)?,
    showAlarmSection: Boolean,
    showRoomInfo: Boolean,
    initialExpanded: RoutineEditExpandedSection = RoutineEditExpandedSection.None,
) {
    var expanded by remember { mutableStateOf(initialExpanded) }
    val repeatText = selectedDays.toRepeatFieldLabel()

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
                    LiroutiBottomSheetTimeField(
                        label = "시작시간",
                        time = startTime,
                        expanded = expanded == RoutineEditExpandedSection.StartTime,
                        onHeaderClick = {
                            expanded = if (expanded == RoutineEditExpandedSection.StartTime) {
                                RoutineEditExpandedSection.None
                            } else {
                                RoutineEditExpandedSection.StartTime
                            }
                        },
                        onTimeChange = onStartTimeChange,
                    )
                    LiroutiBottomSheetTimeField(
                        label = "마감시간",
                        time = endTime,
                        expanded = expanded == RoutineEditExpandedSection.EndTime,
                        onHeaderClick = {
                            expanded = if (expanded == RoutineEditExpandedSection.EndTime) {
                                RoutineEditExpandedSection.None
                            } else {
                                RoutineEditExpandedSection.EndTime
                            }
                        },
                        onTimeChange = onEndTimeChange,
                    )
                    if (expanded == RoutineEditExpandedSection.Repeat) {
                        LiroutiBottomSheetRepeatField(
                            value = repeatText,
                            selectedDays = selectedDays,
                            onDayClick = onDayClick,
                            onHeaderClick = { expanded = RoutineEditExpandedSection.None },
                            headerTrailingExpanded = true,
                        )
                    } else {
                        LiroutiBottomSheetField(
                            label = "반복",
                            value = repeatText,
                            modifier = Modifier.clickable {
                                expanded = RoutineEditExpandedSection.Repeat
                            },
                            trailing = {
                                // Figma 접힘 반복 행: chevron--right (`3610:26830`)
                                LiroutiChevronRightIcon(
                                    modifier = Modifier.size(16.dp),
                                    color = LiroutiTheme.colors.labelSub,
                                )
                            },
                        )
                    }
                    if (showAlarmSection && onAlarmClick != null) {
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
        }

        if (showRoomInfo) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LiroutiBottomSheetInfoRow(
                    text = "다시 알림을 켜면 설정한 시작 시각부터 선택한 간격마다 완료하거나 마감될 때까지 알림이 와요.",
                )
                LiroutiBottomSheetInfoRow(text = "이 설정은 방 멤버 모두에게 동일하게 적용돼요.")
            }
        }
    }
}

/** 요일 인덱스(0=일 … 6=토) → 반복 필드 라벨. */
fun Set<Int>.toRepeatFieldLabel(): String {
    if (isEmpty()) return "없음"
    if (size == 7) return "매일"
    val labels = listOf("일", "월", "화", "수", "목", "금", "토")
    val shorts = sorted().mapNotNull { labels.getOrNull(it) }
    if (shorts.toSet() == setOf("월", "화", "수", "목", "금")) return "주중"
    if (shorts.toSet() == setOf("토", "일")) return "주말"
    if (shorts.size == 1) return "${shorts.first()}요일마다"
    return shorts.joinToString(",")
}

@Preview(showBackground = true, name = "내 루틴 편집 (Figma)")
@Composable
private fun RoutineEditBottomSheetMyRoutineEditPreview() {
    // MyRoutineRoute · Figma `3610:26830` — 알람 행 없음
    RoutineEditPreviewScaffold {
        RoutineEditBottomSheetBody(
            name = "물 마시기",
            onNameChange = {},
            startTime = LiroutiClockTime.DefaultMorning,
            onStartTimeChange = {},
            endTime = LiroutiClockTime.DefaultEvening,
            onEndTimeChange = {},
            selectedDays = emptySet(),
            onDayClick = {},
            onDeleteClick = {},
            onDismissRequest = {},
            namePlaceholder = "루틴 이름",
            alarmText = "없음",
            onAlarmClick = null,
            showAlarmSection = false,
            showRoomInfo = false,
        )
    }
}

@Preview(showBackground = true, name = "루틴 관리 추가 (Figma)")
@Composable
private fun RoutineEditBottomSheetManageAddPreview() {
    // RoutineManageRoute · 동일 시트 (이름 placeholder)
    RoutineEditPreviewScaffold {
        RoutineEditBottomSheetBody(
            name = "",
            onNameChange = {},
            startTime = LiroutiClockTime.DefaultMorning,
            onStartTimeChange = {},
            endTime = LiroutiClockTime.DefaultEvening,
            onEndTimeChange = {},
            selectedDays = emptySet(),
            onDayClick = {},
            onDeleteClick = {},
            onDismissRequest = {},
            namePlaceholder = "루틴 이름",
            alarmText = "없음",
            onAlarmClick = null,
            showAlarmSection = false,
            showRoomInfo = false,
        )
    }
}

@Preview(showBackground = true, name = "루틴 추가 (접힘)")
@Composable
private fun RoutineEditBottomSheetCollapsedPreview() {
    RoutineEditPreviewScaffold {
        RoutineEditBottomSheetBody(
            name = "물 마시기",
            onNameChange = {},
            startTime = LiroutiClockTime.DefaultMorning,
            onStartTimeChange = {},
            endTime = LiroutiClockTime.DefaultEvening,
            onEndTimeChange = {},
            selectedDays = emptySet(),
            onDayClick = {},
            onDeleteClick = {},
            onDismissRequest = {},
            namePlaceholder = "루틴 이름",
            alarmText = "없음",
            onAlarmClick = null,
            showAlarmSection = false,
            showRoomInfo = false,
        )
    }
}

@Preview(showBackground = true, name = "마감시간 펼침")
@Composable
private fun RoutineEditBottomSheetEndTimeExpandedPreview() {
    RoutineEditPreviewScaffold {
        RoutineEditBottomSheetBody(
            name = "물 마시기",
            onNameChange = {},
            startTime = LiroutiClockTime.DefaultMorning,
            onStartTimeChange = {},
            endTime = LiroutiClockTime.DefaultEvening,
            onEndTimeChange = {},
            selectedDays = emptySet(),
            onDayClick = {},
            onDeleteClick = {},
            onDismissRequest = {},
            namePlaceholder = "루틴 이름",
            alarmText = "없음",
            onAlarmClick = null,
            showAlarmSection = false,
            showRoomInfo = false,
            initialExpanded = RoutineEditExpandedSection.EndTime,
        )
    }
}

@Preview(showBackground = true, name = "시작시간 펼침")
@Composable
private fun RoutineEditBottomSheetStartTimeExpandedPreview() {
    RoutineEditPreviewScaffold {
        RoutineEditBottomSheetBody(
            name = "물 마시기",
            onNameChange = {},
            startTime = LiroutiClockTime.DefaultMorning,
            onStartTimeChange = {},
            endTime = LiroutiClockTime.DefaultEvening,
            onEndTimeChange = {},
            selectedDays = emptySet(),
            onDayClick = {},
            onDeleteClick = {},
            onDismissRequest = {},
            namePlaceholder = "루틴 이름",
            alarmText = "없음",
            onAlarmClick = null,
            showAlarmSection = false,
            showRoomInfo = false,
            initialExpanded = RoutineEditExpandedSection.StartTime,
        )
    }
}

@Preview(showBackground = true, name = "반복 펼침")
@Composable
private fun RoutineEditBottomSheetRepeatExpandedPreview() {
    RoutineEditPreviewScaffold {
        RoutineEditBottomSheetBody(
            name = "물 마시기",
            onNameChange = {},
            startTime = LiroutiClockTime.DefaultMorning,
            onStartTimeChange = {},
            endTime = LiroutiClockTime.DefaultEvening,
            onEndTimeChange = {},
            selectedDays = setOf(1, 2, 3, 4, 5),
            onDayClick = {},
            onDeleteClick = {},
            onDismissRequest = {},
            namePlaceholder = "루틴 이름",
            alarmText = "없음",
            onAlarmClick = null,
            showAlarmSection = false,
            showRoomInfo = false,
            initialExpanded = RoutineEditExpandedSection.Repeat,
        )
    }
}

@Composable
private fun RoutineEditPreviewScaffold(
    content: @Composable () -> Unit,
) {
    LiroutiFrontendTheme {
        Surface(
            shape = SheetShape,
            color = LiroutiTheme.colors.backgroundDefault,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(ContentPadding),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                content()
                // 확인 버튼 자리 (LiroutiBottomSheet primary 영역 유사)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = LiroutiTheme.colors.primaryNormal,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "확인",
                            style = LiroutiTheme.typography.body2LongMedium,
                            color = LiroutiTheme.colors.labelReverse,
                        )
                    }
                }
            }
        }
    }
}
