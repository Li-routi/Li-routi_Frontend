package com.li_routi.feature.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/** 월간 달력 한 칸. [day]가 null이면 이전/다음 달로 넘어가는 빈 칸이다. */
data class MonthlyDayUiModel(
    val day: Int?,
    val dayColor: Color,
    val ratio: Float = 0f,
)

private val CellSize = 28.dp
private val DayNumberTextStyle = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 14.sp)

/**
 * 리포트 "월간" 카드. Figma node `205:18423`("Component 1") 기준 —
 * 월 선택(◀ 2026.09 ▶) + 요일 라벨 + 날짜별 달성률 달력.
 *
 * [days]는 7의 배수(주 단위 행)여야 하며, 해당 월 1일 이전/말일 이후는 [MonthlyDayUiModel.day]를
 * null로 채운 빈 칸으로 표현한다.
 */
@Composable
fun MonthlyReportCard(
    monthLabel: String,
    days: List<MonthlyDayUiModel>,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(LiroutiTheme.colors.backgroundDefault, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp)
            .padding(top = 12.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        ReportPeriodHeader(label = monthLabel, onPreviousClick = onPreviousMonthClick, onNextClick = onNextMonthClick)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ReportWeekdayLabelsRow()
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                days.chunked(7).forEach { week ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                        week.forEach { cell ->
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                MonthlyDayCell(cell)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthlyDayCell(cell: MonthlyDayUiModel) {
    if (cell.day == null) {
        Box(modifier = Modifier.size(CellSize))
        return
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier = Modifier
                .size(CellSize)
                .background(LiroutiTheme.colors.borderSub, RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Box(
                modifier = Modifier
                    .size(width = CellSize, height = CellSize * cell.ratio.coerceIn(0f, 1f))
                    .background(LiroutiTheme.colors.primaryNormal, RoundedCornerShape(6.dp)),
            )
        }
        Text(text = cell.day.toString(), style = DayNumberTextStyle, color = cell.dayColor)
    }
}

private val RedDay = Color(0xFFFF4242)
private val BlueDay = Color(0xFF338AFF)
private val BlackDay = Color(0xFF171719)

/** Figma 목업(2026년 9월, 1일=화요일)을 그대로 옮긴 샘플 데이터. */
private val SampleMonthlyDays = listOf(
    null to 0f, null to 0f, 1 to 1f, 2 to 0f, 3 to 0f, 4 to 0f, 5 to (12f / 28f),
    6 to 0f, 7 to 0f, 8 to (19f / 28f), 9 to 0f, 10 to 0f, 11 to 0f, 12 to 0f,
    13 to 0f, 14 to 0f, 15 to 0f, 16 to 0f, 17 to 0f, 18 to 0f, 19 to 0f,
    20 to 0f, 21 to 0f, 22 to 0f, 23 to 0f, 24 to 0f, 25 to 0f, 26 to 0f,
    27 to 0f, 28 to 0f, 29 to 0f, 30 to 0f, null to 0f, null to 0f, null to 0f,
).mapIndexed { index, (day, ratio) ->
    val dayColor = when (index % 7) {
        0 -> RedDay
        6 -> BlueDay
        else -> BlackDay
    }
    MonthlyDayUiModel(day = day, dayColor = dayColor, ratio = ratio)
}

@Preview(showBackground = true)
@Composable
private fun MonthlyReportCardPreview() {
    LiroutiFrontendTheme {
        MonthlyReportCard(
            monthLabel = "2026.09",
            days = SampleMonthlyDays,
            onPreviousMonthClick = {},
            onNextMonthClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
