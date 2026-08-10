package com.li_routi.feature.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.component.LiroutiBottomSheet
import com.li_routi.core.designsystem.component.WheelNumberPicker
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.launch

/** 연-월-일. [month]는 1..12. */
data class SimpleDate(val year: Int, val month: Int, val day: Int) {
    fun toDisplayLabel(): String = "${year}년 ${month}월 ${day}일"
}

fun todaySimpleDate(): SimpleDate {
    val calendar = Calendar.getInstance()
    return SimpleDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))
}

fun SimpleDate.plusDays(delta: Int): SimpleDate {
    val calendar = Calendar.getInstance().apply {
        set(year, month - 1, day)
        add(Calendar.DAY_OF_MONTH, delta)
    }
    return SimpleDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))
}

private fun stepMonth(year: Int, month: Int, delta: Int): Pair<Int, Int> {
    var y = year
    var m = month + delta
    while (m > 12) {
        m -= 12
        y += 1
    }
    while (m < 1) {
        m += 12
        y -= 1
    }
    return y to m
}

private fun daysInMonth(year: Int, month: Int): Int =
    Calendar.getInstance().apply { set(year, month - 1, 1) }.getActualMaximum(Calendar.DAY_OF_MONTH)

/** 해당 월 1일의 요일(0=일 .. 6=토). */
private fun firstWeekday(year: Int, month: Int): Int =
    Calendar.getInstance().apply { set(year, month - 1, 1) }.get(Calendar.DAY_OF_WEEK) - 1

private const val MonthGridWeekCount = 6
private const val MonthGridCellCount = MonthGridWeekCount * 7

/**
 * 달력 그리드용 셀 목록. 월에 따라 실제 필요한 주가 4~6주로 달라지는데, 그대로 두면 주 수만큼 시트
 * 높이가 들쭉날쭉해진다 — 항상 [MonthGridWeekCount]주(42칸)로 맞춰서 남는 주는 빈 칸(null)으로 채우면
 * 행 간격(spacedBy)은 그대로 유지하면서 시트 높이만 월과 무관하게 고정된다.
 */
private fun buildMonthCells(year: Int, month: Int): List<Int?> {
    val leading = firstWeekday(year, month)
    val total = daysInMonth(year, month)
    val cells = ArrayList<Int?>(MonthGridCellCount)
    repeat(leading) { cells.add(null) }
    (1..total).forEach { cells.add(it) }
    while (cells.size < MonthGridCellCount) cells.add(null)
    return cells
}

private enum class PickerMode { Day, MonthYear }

private val DayCellTextStyle = TextStyle(fontSize = 14.sp, lineHeight = 22.sp, letterSpacing = (-0.35).sp)
private val SelectedDayCellTextStyle = DayCellTextStyle.copy(fontWeight = FontWeight.SemiBold)
private val DayCellSize = 28.dp

/** Figma node `4869:37484` 기준 — 시트 상단 모서리 20dp (design-system 기본 [LiroutiBottomSheet] 6dp보다 큼). */
private val SheetTopRoundedShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

/**
 * 연/월 휠의 바깥 전체 높이. Figma는 일자 선택(달력) 모드와 시트 전체 높이가 같도록 위/아래 여백을 크게
 * 준다 — 단, NumberPicker는 보이는 3줄을 이 높이 전체에 균등 배분하므로, 그대로 늘리면 줄 사이 간격까지
 * 벌어져버린다(3줄만 있는데 늘어난 공백까지 나눠 가지므로). 그래서 [YearMonthWheelRowsHeight]로 줄
 * 자체는 원래 촘촘한 간격을 유지하고, 그 바깥을 이 높이의 Box로 감싸 위아래에 여백만 추가한다.
 */
private val YearMonthWheelHeight = 220.dp
private val YearMonthWheelRowsHeight = 90.dp

/**
 * "내 인증" 날짜 선택 바텀시트. Figma node `4869:37021`(일자 선택)/`4869:37419`(월 선택) 기준.
 *
 * 헤더의 "YYYY년 MM월" 라벨을 탭하면 달력 그리드(일자 선택)와 연/월 휠(월 선택) 모드를 오간다.
 * 일자 선택은 탭 즉시 확정돼 시트가 닫히고, 연/월 선택은 취소/확인 버튼으로 명시적으로 확정한다
 * (확정해도 시트는 닫히지 않고 달력 그리드로 돌아간다 — 이어서 일자를 골라야 하기 때문).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyVerificationDatePickerBottomSheet(
    initialDate: SimpleDate,
    onDismissRequest: () -> Unit,
    onDateSelected: (SimpleDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    var mode by remember { mutableStateOf(PickerMode.Day) }
    var displayYear by remember { mutableIntStateOf(initialDate.year) }
    var displayMonth by remember { mutableIntStateOf(initialDate.month) }
    var wheelYear by remember { mutableIntStateOf(initialDate.year) }
    var wheelMonth by remember { mutableIntStateOf(initialDate.month) }

    // 일자 탭처럼 코드에서 직접 닫을 때 onDismissRequest()를 바로 부르면(= sheetState를 거치지 않고
    // 시트를 컴포지션에서 즉시 제거) ModalBottomSheet 내부 애니메이션/윈도우 상태가 미처 정리되지 않아
    // 다음 입력이 초점 없는 창으로 가면서 ANR("Input dispatching timed out ... no focused window")이
    // 났다 — sheetState.hide()로 접는 애니메이션을 먼저 끝내고 그 완료 콜백에서 onDismissRequest()를
    // 부르는 걸로 고쳤다(ModalBottomSheet가 스와이프/뒤로가기로 닫힐 때 쓰는 것과 동일한 경로).
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true, confirmValueChange = { true })
    val coroutineScope = rememberCoroutineScope()
    val dismiss: () -> Unit = {
        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion { onDismissRequest() }
    }

    LiroutiBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier,
        shape = SheetTopRoundedShape,
        primaryButtonText = if (mode == PickerMode.MonthYear) "확인" else null,
        onPrimaryButtonClick = {
            displayYear = wheelYear
            displayMonth = wheelMonth
            mode = PickerMode.Day
        },
        secondaryButtonText = if (mode == PickerMode.MonthYear) "취소" else null,
        onSecondaryButtonClick = {
            wheelYear = displayYear
            wheelMonth = displayMonth
            mode = PickerMode.Day
        },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            ReportPeriodHeader(
                label = String.format(Locale.KOREA, "%d년 %02d월", displayYear, displayMonth),
                onPreviousClick = {
                    val (y, m) = stepMonth(displayYear, displayMonth, -1)
                    displayYear = y
                    displayMonth = m
                    wheelYear = y
                    wheelMonth = m
                },
                onNextClick = {
                    val (y, m) = stepMonth(displayYear, displayMonth, 1)
                    displayYear = y
                    displayMonth = m
                    wheelYear = y
                    wheelMonth = m
                },
                onLabelClick = { mode = if (mode == PickerMode.Day) PickerMode.MonthYear else PickerMode.Day },
            )

            if (mode == PickerMode.Day) {
                DayGrid(
                    year = displayYear,
                    month = displayMonth,
                    selectedDay = initialDate.day.takeIf { displayYear == initialDate.year && displayMonth == initialDate.month },
                    onDayClick = { day ->
                        onDateSelected(SimpleDate(displayYear, displayMonth, day))
                        dismiss()
                    },
                )
            } else {
                YearMonthWheel(
                    year = wheelYear,
                    month = wheelMonth,
                    onYearChange = { wheelYear = it },
                    onMonthChange = { wheelMonth = it },
                )
            }
        }
    }
}

@Composable
private fun DayGrid(
    year: Int,
    month: Int,
    selectedDay: Int?,
    onDayClick: (Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ReportWeekdayLabelsRow()
        val cells = remember(year, month) { buildMonthCells(year, month) }
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            cells.chunked(7).forEach { week ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                    week.forEachIndexed { columnIndex, day ->
                        // 빈 칸(day == null)도 DayCellSize만큼 높이를 잡아야 그 주 전체가 실제 요일이 있는
                        // 주와 같은 높이를 유지한다 — 안 그러면 weight(1f)만으로는 세로 크기가 0으로
                        // 찌그러져서, 6주로 패딩해도 시트 높이가 완전히 통일되지 않는다.
                        Box(modifier = Modifier.weight(1f).height(DayCellSize), contentAlignment = Alignment.Center) {
                            if (day != null) {
                                DayCell(
                                    day = day,
                                    columnIndex = columnIndex,
                                    isSelected = day == selectedDay,
                                    onClick = { onDayClick(day) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(day: Int, columnIndex: Int, isSelected: Boolean, onClick: () -> Unit) {
    val textColor = when {
        isSelected -> LiroutiTheme.colors.labelReverse
        columnIndex == 0 -> LiroutiTheme.colors.dangerText
        columnIndex == 6 -> LiroutiTheme.colors.primaryNormal
        else -> LiroutiTheme.colors.labelDefault
    }
    Box(
        modifier = Modifier
            .size(DayCellSize)
            .clip(RoundedCornerShape(6.dp))
            .let { if (isSelected) it.background(LiroutiTheme.colors.primaryNormal) else it }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = day.toString(), style = if (isSelected) SelectedDayCellTextStyle else DayCellTextStyle, color = textColor)
    }
}

@Composable
private fun YearMonthWheel(
    year: Int,
    month: Int,
    onYearChange: (Int) -> Unit,
    onMonthChange: (Int) -> Unit,
) {
    val baseYear = remember { Calendar.getInstance().get(Calendar.YEAR) }
    val years = remember { (baseYear - 10..baseYear + 10).map { "${it}년" }.toTypedArray() }
    val months = remember { (1..12).map { "${it}월" }.toTypedArray() }
    val selectedTextColorArgb = LiroutiTheme.colors.labelDefault.toArgb()

    Box(
        modifier = Modifier.fillMaxWidth().height(YearMonthWheelHeight),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(YearMonthWheelRowsHeight),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(LiroutiTheme.colors.borderAlternative),
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                WheelNumberPicker(
                    values = years,
                    selectedIndex = years.indexOf("${year}년").coerceAtLeast(0),
                    onSelectedIndexChange = { index -> onYearChange(baseYear - 10 + index) },
                    selectedTextColorArgb = selectedTextColorArgb,
                    modifier = Modifier.weight(1f),
                    height = YearMonthWheelRowsHeight,
                )
                WheelNumberPicker(
                    values = months,
                    selectedIndex = (month - 1).coerceIn(0, 11),
                    onSelectedIndexChange = { index -> onMonthChange(index + 1) },
                    selectedTextColorArgb = selectedTextColorArgb,
                    modifier = Modifier.weight(1f),
                    height = YearMonthWheelRowsHeight,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, heightDp = 700)
@Composable
private fun MyVerificationDatePickerBottomSheetPreview() {
    LiroutiFrontendTheme {
        MyVerificationDatePickerBottomSheet(
            initialDate = SimpleDate(2026, 9, 2),
            onDismissRequest = {},
            onDateSelected = {},
        )
    }
}
