package com.li_routi.core.common.ui.calendar

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.LiroutiBottomSheet
import com.li_routi.core.designsystem.component.WheelNumberPicker
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale
import kotlinx.coroutines.launch

private enum class CalendarPickerMode { Day, MonthYear }

private val DayCellTextStyle = TextStyle(fontSize = 14.sp, lineHeight = 22.sp, letterSpacing = (-0.35).sp)
private val SelectedDayCellTextStyle = DayCellTextStyle.copy(fontWeight = FontWeight.SemiBold)
private val WeekdayLabelTextStyle = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 14.sp)
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

private const val MonthGridWeekCount = 6
private const val MonthGridCellCount = MonthGridWeekCount * 7

/**
 * 날짜 선택 바텀시트. 마이페이지 "내 인증" 날짜 필터(Figma node `4869:37021`/`4869:37419`)와 그룹
 * 채팅의 "날짜로 이동" 캘린더가 함께 쓰는 공용 컴포넌트다.
 *
 * 헤더의 "YYYY년 MM월" 라벨을 탭하면 달력 그리드(일자 선택)와 연/월 휠(월 선택) 모드를 오간다.
 * 일자 선택은 탭 즉시 확정돼 시트가 닫히고, 연/월 선택은 취소/확인 버튼으로 명시적으로 확정한다
 * (확정해도 시트는 닫히지 않고 달력 그리드로 돌아간다 — 이어서 일자를 골라야 하기 때문).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiroutiCalendarBottomSheet(
    initialDate: LocalDate,
    onDismissRequest: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    var mode by remember { mutableStateOf(CalendarPickerMode.Day) }
    var displayYearMonth by remember { mutableStateOf(YearMonth.from(initialDate)) }
    var wheelYearMonth by remember { mutableStateOf(displayYearMonth) }

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
        primaryButtonText = if (mode == CalendarPickerMode.MonthYear) "확인" else null,
        onPrimaryButtonClick = {
            displayYearMonth = wheelYearMonth
            mode = CalendarPickerMode.Day
        },
        secondaryButtonText = if (mode == CalendarPickerMode.MonthYear) "취소" else null,
        onSecondaryButtonClick = {
            wheelYearMonth = displayYearMonth
            mode = CalendarPickerMode.Day
        },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            CalendarPeriodHeader(
                label = String.format(Locale.KOREA, "%d년 %02d월", displayYearMonth.year, displayYearMonth.monthValue),
                onPreviousClick = {
                    displayYearMonth = displayYearMonth.minusMonths(1)
                    wheelYearMonth = displayYearMonth
                },
                onNextClick = {
                    displayYearMonth = displayYearMonth.plusMonths(1)
                    wheelYearMonth = displayYearMonth
                },
                onLabelClick = {
                    mode = if (mode == CalendarPickerMode.Day) {
                        CalendarPickerMode.MonthYear
                    } else {
                        CalendarPickerMode.Day
                    }
                },
            )

            if (mode == CalendarPickerMode.Day) {
                CalendarDayGrid(
                    yearMonth = displayYearMonth,
                    selectedDay = initialDate.dayOfMonth.takeIf { YearMonth.from(initialDate) == displayYearMonth },
                    onDayClick = { day ->
                        onDateSelected(displayYearMonth.atDay(day))
                        dismiss()
                    },
                )
            } else {
                CalendarYearMonthWheel(
                    yearMonth = wheelYearMonth,
                    onYearMonthChange = { wheelYearMonth = it },
                )
            }
        }
    }
}

/** "◀ YYYY년 MM월 ▶" 이전/다음 이동 행. 라벨을 탭하면 [onLabelClick]으로 연/월 휠 모드로 전환한다. */
@Composable
private fun CalendarPeriodHeader(
    label: String,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onLabelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = R.drawable.chevron__left),
            contentDescription = "이전",
            modifier = Modifier.size(16.dp).clickable(onClick = onPreviousClick),
        )
        Text(
            text = label,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = (-0.08).sp),
            color = LiroutiTheme.colors.labelStrong,
            modifier = Modifier.clickable(onClick = onLabelClick),
        )
        Image(
            painter = painterResource(id = R.drawable.chevron__right),
            contentDescription = "다음",
            modifier = Modifier.size(16.dp).clickable(onClick = onNextClick),
        )
    }
}

@Composable
private fun CalendarWeekdayLabelsRow(modifier: Modifier = Modifier) {
    val labels = listOf(
        "일" to LiroutiTheme.colors.dangerText,
        "월" to LiroutiTheme.colors.labelDefault,
        "화" to LiroutiTheme.colors.labelDefault,
        "수" to LiroutiTheme.colors.labelDefault,
        "목" to LiroutiTheme.colors.labelDefault,
        "금" to LiroutiTheme.colors.labelDefault,
        "토" to LiroutiTheme.colors.primaryNormal,
    )
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        labels.forEach { (label, color) ->
            Text(text = label, style = WeekdayLabelTextStyle, color = color)
        }
    }
}

/**
 * 달력 그리드용 셀 목록. 월에 따라 실제 필요한 주가 4~6주로 달라지는데, 그대로 두면 주 수만큼 시트
 * 높이가 들쭉날쭉해진다 — 항상 [MonthGridWeekCount]주(42칸)로 맞춰서 남는 주는 빈 칸(null)으로 채우면
 * 행 간격(spacedBy)은 그대로 유지하면서 시트 높이만 월과 무관하게 고정된다.
 */
private fun buildMonthCells(yearMonth: YearMonth): List<Int?> {
    // DayOfWeek.value: 월=1 ... 일=7 이라 %7 하면 일=0으로 바뀌어 "일요일 시작" 오프셋이 된다.
    val leading = yearMonth.atDay(1).dayOfWeek.value % 7
    val total = yearMonth.lengthOfMonth()
    val cells = ArrayList<Int?>(MonthGridCellCount)
    repeat(leading) { cells.add(null) }
    (1..total).forEach { cells.add(it) }
    while (cells.size < MonthGridCellCount) cells.add(null)
    return cells
}

@Composable
private fun CalendarDayGrid(
    yearMonth: YearMonth,
    selectedDay: Int?,
    onDayClick: (Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        CalendarWeekdayLabelsRow()
        val cells = remember(yearMonth) { buildMonthCells(yearMonth) }
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            cells.chunked(7).forEach { week ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                    week.forEachIndexed { columnIndex, day ->
                        // 빈 칸(day == null)도 DayCellSize만큼 높이를 잡아야 그 주 전체가 실제 요일이 있는
                        // 주와 같은 높이를 유지한다 — 안 그러면 weight(1f)만으로는 세로 크기가 0으로
                        // 찌그러져서, 6주로 패딩해도 시트 높이가 완전히 통일되지 않는다.
                        Box(modifier = Modifier.weight(1f).height(DayCellSize), contentAlignment = Alignment.Center) {
                            if (day != null) {
                                CalendarDayCell(
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
private fun CalendarDayCell(day: Int, columnIndex: Int, isSelected: Boolean, onClick: () -> Unit) {
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

/** 연도 휠의 고정 범위. 예전엔 휠이 열릴 때 표시 연도 기준 ±10년으로 잡았는데, 그러면 헤더로
 * 연도를 이동한 뒤 다시 휠을 열 때마다 범위 자체가 같이 밀렸다(예: 2016년으로 이동 후 재오픈하면
 * 2006~2026년으로 범위가 바뀜). 고정값으로 못박아 몇 번을 열어도 범위가 그대로 유지되게 한다. */
private const val CalendarPickerMinYear = 2026
private const val CalendarPickerMaxYear = 2046

@Composable
private fun CalendarYearMonthWheel(
    yearMonth: YearMonth,
    onYearMonthChange: (YearMonth) -> Unit,
) {
    val years = remember {
        (CalendarPickerMinYear..CalendarPickerMaxYear).map { "${it}년" }.toTypedArray()
    }
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
                    selectedIndex = years.indexOf("${yearMonth.year}년").coerceAtLeast(0),
                    onSelectedIndexChange = { index ->
                        onYearMonthChange(YearMonth.of(CalendarPickerMinYear + index, yearMonth.monthValue))
                    },
                    selectedTextColorArgb = selectedTextColorArgb,
                    modifier = Modifier.weight(1f),
                    height = YearMonthWheelRowsHeight,
                )
                WheelNumberPicker(
                    values = months,
                    selectedIndex = (yearMonth.monthValue - 1).coerceIn(0, 11),
                    onSelectedIndexChange = { index -> onYearMonthChange(YearMonth.of(yearMonth.year, index + 1)) },
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
private fun LiroutiCalendarBottomSheetPreview() {
    LiroutiFrontendTheme {
        LiroutiCalendarBottomSheet(
            initialDate = LocalDate.of(2026, 9, 2),
            onDismissRequest = {},
            onDateSelected = {},
        )
    }
}
