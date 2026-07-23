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

/**
 * Matches the Figma `bottom_sheet/contents` list row: leading check state,
 * a label, a trailing check state, a chevron and a switch.
 */
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
            style = LiroutiTheme.typography.body1,
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

/** A dot-mark icon followed by a wrapping caption, matching the Figma `info-block` row. */
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

/** A label/value row on a filled background, matching the 마감시간 / 알람 시간 rows. */
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
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = value, style = FieldValueTextStyle, color = LiroutiTheme.colors.labelSub)
            trailing?.invoke(this)
        }
    }
}

/** The 반복 row combined with its day-of-week selector, sharing one filled background. */
@Composable
fun LiroutiBottomSheetRepeatField(
    value: String,
    selectedDays: Set<Int>,
    onDayClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "반복",
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
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = label, style = FieldLabelTextStyle, color = LiroutiTheme.colors.labelSub)
            Text(text = value, style = FieldValueTextStyle, color = LiroutiTheme.colors.labelSub)
        }
        LiroutiDivider(color = LiroutiTheme.colors.borderSub)
        LiroutiDaySelector(
            selectedDays = selectedDays,
            onDayClick = onDayClick,
            modifier = Modifier.padding(horizontal = 12.dp),
        )
    }
}

/** The "삭제" action shown in the Routine variant header. */
@Composable
fun LiroutiBottomSheetDeleteButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "삭제",
) {
    Text(
        text = text,
        modifier = modifier.clickable(onClick = onClick),
        style = LiroutiTheme.typography.body1,
        color = LiroutiTheme.colors.dangerText,
    )
}

@Preview(showBackground = true)
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

@Preview(showBackground = true)
@Composable
private fun LiroutiBottomSheetFieldPreview() {
    LiroutiFrontendTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            LiroutiBottomSheetField(label = "마감시간", value = "오후 11:00")
            LiroutiBottomSheetRepeatField(value = "없음", selectedDays = emptySet(), onDayClick = {})
            LiroutiBottomSheetField(
                label = "알람 시간",
                value = "없음",
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

@Preview(showBackground = true)
@Composable
private fun LiroutiBottomSheetDeleteButtonPreview() {
    LiroutiFrontendTheme {
        LiroutiBottomSheetDeleteButton(onClick = {}, modifier = Modifier.padding(16.dp))
    }
}
