package com.li_routi.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme


@Composable
fun LiroutiBottomSheetListItem(
    label: String,
    modifier: Modifier = Modifier,
    leadingChecked: Boolean = true,
    trailingChecked: Boolean = true,
    showChevron: Boolean = true,
    onChevronClick: () -> Unit = {},
    switchChecked: Boolean = true,
    onSwitchCheckedChange: (Boolean) -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingChecked) {
            LiroutiCheckmarkIcon(
                modifier = Modifier.size(20.dp),
                color = LiroutiTheme.colors.labelInfo,
            )
        }
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = LiroutiTheme.typography.body1Regular,
            color = LiroutiTheme.colors.labelDefault,
        )
        if (trailingChecked) {
            LiroutiCheckmarkIcon(
                modifier = Modifier.size(20.dp),
                color = LiroutiTheme.colors.labelInfo,
            )
        }
        if (showChevron) {
            LiroutiChevronRightIcon(
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = onChevronClick),
                color = LiroutiTheme.colors.labelDefault,
            )
        }
        LiroutiSwitch(
            checked = switchChecked,
            onCheckedChange = onSwitchCheckedChange,
        )
    }
}

@Composable
fun LiroutiBottomSheetInfoRow(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        LiroutiDotMarkIcon(
            modifier = Modifier.size(16.dp),
            color = LiroutiTheme.colors.labelInfo,
        )
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            style = InfoTextStyle,
            color = LiroutiTheme.colors.labelInfo,
        )
    }
}

@Composable
fun LiroutiBottomSheetField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    trailing: (@Composable RowScope.() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(LiroutiTheme.colors.backgroundFill, RoundedCornerShape(4.dp))
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, style = FieldLabelTextStyle, color = LiroutiTheme.colors.labelSub)
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = value, style = FieldValueTextStyle, color = LiroutiTheme.colors.labelSub)
            trailing?.invoke(this)
        }
    }
}

@Composable
fun LiroutiBottomSheetRepeatField(
    value: String,
    selectedDays: Set<Int>,
    onDayClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "반복",
    /** null이면 헤더 클릭 없음. 접기/펼치기용. */
    onHeaderClick: (() -> Unit)? = null,
    headerTrailingExpanded: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(LiroutiTheme.colors.backgroundFill, RoundedCornerShape(4.dp))
            .padding(horizontal = 12.dp)
            .padding(top = 6.dp, bottom = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (onHeaderClick != null) Modifier.clickable(onClick = onHeaderClick)
                    else Modifier,
                )
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = label, style = FieldLabelTextStyle, color = LiroutiTheme.colors.labelSub)
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = value, style = FieldValueTextStyle, color = LiroutiTheme.colors.labelSub)
                if (onHeaderClick != null) {
                    if (headerTrailingExpanded) {
                        LiroutiChevronUpIcon(
                            modifier = Modifier.size(16.dp),
                            color = LiroutiTheme.colors.labelSub,
                        )
                    } else {
                        LiroutiChevronDownIcon(
                            modifier = Modifier.size(16.dp),
                            color = LiroutiTheme.colors.labelSub,
                        )
                    }
                }
            }
        }
        LiroutiDivider(color = LiroutiTheme.colors.borderSub)
        LiroutiDaySelector(
            selectedDays = selectedDays,
            onDayClick = onDayClick,
            modifier = Modifier.padding(horizontal = 12.dp),
        )
    }
}

/**
 * Figma DS Bottom Sheet `시작시간` / `마감시간` 필드.
 * 펼침 시 헤더 + Divider + 타임휠이 하나의 fill 카드 안에 들어간다.
 */
@Composable
fun LiroutiBottomSheetTimeField(
    label: String,
    time: LiroutiClockTime,
    expanded: Boolean,
    onHeaderClick: () -> Unit,
    onTimeChange: (LiroutiClockTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (expanded) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(LiroutiTheme.colors.backgroundFill, RoundedCornerShape(4.dp))
                .padding(horizontal = 12.dp)
                .padding(top = 5.dp, bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onHeaderClick)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = label,
                    style = FieldLabelTextStyle,
                    color = LiroutiTheme.colors.labelSub,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = time.toDisplayText(),
                        style = FieldValueTextStyle,
                        color = LiroutiTheme.colors.labelSub,
                    )
                    LiroutiChevronUpIcon(
                        modifier = Modifier.size(16.dp),
                        color = LiroutiTheme.colors.labelSub,
                    )
                }
            }
            LiroutiDivider(color = LiroutiTheme.colors.borderSub)
            LiroutiTimeWheelPicker(
                value = time,
                onValueChange = onTimeChange,
            )
        }
    } else {
        LiroutiBottomSheetField(
            label = label,
            value = time.toDisplayText(),
            modifier = modifier.clickable(onClick = onHeaderClick),
            trailing = {
                LiroutiChevronDownIcon(
                    modifier = Modifier.size(16.dp),
                    color = LiroutiTheme.colors.labelSub,
                )
            },
        )
    }
}

@Composable
fun LiroutiBottomSheetDeleteButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "삭제",
) {
    Text(
        text = text,
        modifier = modifier.clickable(onClick = onClick),
        style = LiroutiTheme.typography.body1Regular,
        color = LiroutiTheme.colors.dangerText,
    )
}

@Preview(showBackground = true, name = "ListItem")
@Composable
private fun LiroutiBottomSheetListItemPreview() {
    LiroutiFrontendTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            repeat(2) {
                LiroutiBottomSheetListItem(label = "6시간")
            }
        }
    }
}

@Preview(showBackground = true, name = "TimeField 접힘")
@Composable
private fun LiroutiBottomSheetTimeFieldCollapsedPreview() {
    LiroutiFrontendTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            LiroutiBottomSheetTimeField(
                label = "시작시간",
                time = LiroutiClockTime.DefaultMorning,
                expanded = false,
                onHeaderClick = {},
                onTimeChange = {},
            )
            LiroutiBottomSheetTimeField(
                label = "마감시간",
                time = LiroutiClockTime.DefaultEvening,
                expanded = false,
                onHeaderClick = {},
                onTimeChange = {},
            )
            LiroutiBottomSheetField(
                label = "반복",
                value = "없음",
                trailing = {
                    LiroutiChevronDownIcon(
                        modifier = Modifier.size(16.dp),
                        color = LiroutiTheme.colors.labelSub,
                    )
                },
            )
        }
    }
}

@Preview(showBackground = true, name = "TimeField 시작시간 펼침")
@Composable
private fun LiroutiBottomSheetTimeFieldStartExpandedPreview() {
    LiroutiFrontendTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            LiroutiBottomSheetTimeField(
                label = "시작시간",
                time = LiroutiClockTime.DefaultMorning,
                expanded = true,
                onHeaderClick = {},
                onTimeChange = {},
            )
            LiroutiBottomSheetTimeField(
                label = "마감시간",
                time = LiroutiClockTime.DefaultEvening,
                expanded = false,
                onHeaderClick = {},
                onTimeChange = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "TimeField 마감시간 펼침")
@Composable
private fun LiroutiBottomSheetTimeFieldEndExpandedPreview() {
    LiroutiFrontendTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            LiroutiBottomSheetTimeField(
                label = "시작시간",
                time = LiroutiClockTime.DefaultMorning,
                expanded = false,
                onHeaderClick = {},
                onTimeChange = {},
            )
            LiroutiBottomSheetTimeField(
                label = "마감시간",
                time = LiroutiClockTime.DefaultEvening,
                expanded = true,
                onHeaderClick = {},
                onTimeChange = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Repeat 펼침")
@Composable
private fun LiroutiBottomSheetRepeatFieldExpandedPreview() {
    LiroutiFrontendTheme {
        LiroutiBottomSheetRepeatField(
            value = "월요일마다",
            selectedDays = setOf(1),
            onDayClick = {},
            onHeaderClick = {},
            headerTrailingExpanded = true,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, name = "DeleteButton")
@Composable
private fun LiroutiBottomSheetDeleteButtonPreview() {
    LiroutiFrontendTheme {
        LiroutiBottomSheetDeleteButton(onClick = {}, modifier = Modifier.padding(16.dp))
    }
}
