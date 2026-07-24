package com.li_routi.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.foundation.typography.Pretendard
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

private val DayTextStyle = TextStyle(
    fontFamily = Pretendard,
    fontSize = 14.sp,
    lineHeight = 22.sp,
    letterSpacing = (-0.025f).em,
)

val LiroutiDaySelectorDefaultDays = listOf("일", "월", "화", "수", "목", "금", "토")

@Composable
fun LiroutiDaySelector(
    selectedDays: Set<Int>,
    onDayClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    dayLabels: List<String> = LiroutiDaySelectorDefaultDays,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        dayLabels.forEachIndexed { index, label ->
            val selected = index in selectedDays
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(if (selected) LiroutiTheme.colors.chipSelectedBackground else LiroutiTheme.colors.backgroundAlternative)
                    .border(
                        width = 1.dp,
                        color = if (selected) LiroutiTheme.colors.chipSelectedBackground else LiroutiTheme.colors.backgroundStrong,
                        shape = CircleShape,
                    )
                    .clickable { onDayClick(index) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = DayTextStyle,
                    color = if (selected) LiroutiTheme.colors.labelReverse else LiroutiTheme.colors.labelInfo,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiDaySelectorPreview() {
    LiroutiFrontendTheme {
        LiroutiDaySelector(
            selectedDays = setOf(1, 3),
            onDayClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
