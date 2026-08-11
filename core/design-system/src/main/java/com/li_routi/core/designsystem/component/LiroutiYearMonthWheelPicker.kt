package com.li_routi.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.foundation.color.Neutral10
import com.li_routi.core.designsystem.foundation.color.Neutral98
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import java.util.Calendar

data class LiroutiYearMonth(
    val year: Int,
    val month: Int,
)

/**
 * 연도·월을 세로로 돌려서 고르는 2열 텀블러. [LiroutiTimeWheelPicker]와 같은 부품([WheelNumberPicker])을
 * 재사용하지만, 시간과는 별개 화면(캘린더 날짜 선택)에 쓰이는 독립된 컴포넌트다.
 *
 * 연도는 [yearRange] 끝에서 순환하지 않고, 월(1~12)은 순환한다. [yearRange]의 기본값 상한은
 * 오늘 연도([Calendar]에서 읽음)라 해가 바뀌면 코드 수정 없이 자동으로 최신 연도까지 늘어난다.
 *
 * 글자색은 시간 휠과 동일하게 [Neutral10] 고정(밝은 배경 위 가독성).
 */
@Composable
fun LiroutiYearMonthWheelPicker(
    value: LiroutiYearMonth,
    onValueChange: (LiroutiYearMonth) -> Unit,
    modifier: Modifier = Modifier,
    yearRange: IntRange = 2000..Calendar.getInstance().get(Calendar.YEAR),
) {
    val years = yearRange.map { "${it}년도" }.toTypedArray()
    val months = (1..12).map { "${it}월" }.toTypedArray()
    val wheelTextColorArgb = Neutral10.toArgb()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Neutral98),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WheelNumberPicker(
                values = years,
                selectedIndex = (value.year - yearRange.first).coerceIn(0, years.lastIndex),
                onSelectedIndexChange = { index ->
                    onValueChange(value.copy(year = yearRange.first + index))
                },
                selectedTextColorArgb = wheelTextColorArgb,
                modifier = Modifier.weight(1f),
                wrapSelectorWheel = false,
            )
            WheelNumberPicker(
                values = months,
                selectedIndex = (value.month - 1).coerceIn(0, 11),
                onSelectedIndexChange = { index ->
                    onValueChange(value.copy(month = index + 1))
                },
                selectedTextColorArgb = wheelTextColorArgb,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Preview(showBackground = true
)
@Composable
private fun LiroutiYearMonthWheelPickerPreview() {
    LiroutiFrontendTheme {
        LiroutiYearMonthWheelPicker(
            value = LiroutiYearMonth(year = 2026, month = 8),
            onValueChange = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
