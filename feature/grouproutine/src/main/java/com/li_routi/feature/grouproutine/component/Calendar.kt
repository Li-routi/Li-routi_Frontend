package com.li_routi.feature.grouproutine.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.HorizontalSegment
import com.li_routi.core.designsystem.component.LiroutiYearMonth
import com.li_routi.core.designsystem.component.LiroutiYearMonthWheelPicker
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import java.time.LocalDate
import java.time.YearMonth

private val CalendarSheetHeight = 336.dp
private val CalendarSheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
private val CalendarSheetShadowColor = Color.Black.copy(alpha = 0.25f)
private val YearMonthLabelTopPadding = 20.dp
private val CalendarSelectorHorizontalMargin = 28.dp
private val CalendarActionButtonHorizontalMargin = 16.dp
private val CalendarActionButtonBottomPadding = 32.dp
private val CalendarGridTopPadding = 18.dp
private val CalendarGridHorizontalMargin = 22.dp
private val CalendarWeekdayLabels = listOf("일", "월", "화", "수", "목", "금", "토")
private val CalendarSelectedDaySize = 28.dp
private val CalendarSelectedDayColor = Color(0xFF338AFF)

/**
 * 채팅 상단 캘린더 아이콘을 누르면 아래에서 올라오는 날짜 선택 팝업.
 *
 * 두 화면을 토글한다 — 연/월을 고르는 중이면 연/월 휠([LiroutiYearMonthWheelPicker]) +
 * 취소/확인 버튼을, 아니면 확정된 [yearMonth]의 요일·날짜 그리드를 보여준다. "확인"을 누르면
 * [onConfirm]으로 새 연/월을 올려보내고 그리드 화면으로 전환되고, "취소"를 누르면 draft를 버리고
 * (원래 [yearMonth] 그대로) 그리드 화면으로 전환된다.
 *
 * [yearMonth]는 이 컴포저블이 직접 오늘 날짜를 구하지 않고 호출부(예: `YearMonth.now()`)에서
 * 받는다 — 확정된 값은 항상 호출부 상태다.
 *
 * 그리드 화면에서 날짜를 한 번 탭하면 그 날짜가 선택 표시(파란 박스)되고, 같은 날짜를 한 번 더
 * 탭하면 [onDateConfirmed]가 호출된다 — 호출부는 이걸로 그 날짜의 채팅으로 이동시키면 된다.
 *
 * 너비는 부모(ModalBottomSheet 등, 좌우 패딩 없이 화면 전체 너비로 뜨는 컨테이너)가 주는 만큼
 * [Modifier.fillMaxWidth]로 채운다.
 */
@Composable
fun CalendarSheet(
    yearMonth: YearMonth,
    modifier: Modifier = Modifier,
    onConfirm: (YearMonth) -> Unit = {},
    onCancel: () -> Unit = {},
    onDateConfirmed: (LocalDate) -> Unit = {},
) {
    var draftYearMonth by remember(yearMonth) {
        mutableStateOf(LiroutiYearMonth(year = yearMonth.year, month = yearMonth.monthValue))
    }
    var isSelectingYearMonth by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(CalendarSheetHeight)
            .shadow(
                elevation = 26.dp,
                shape = CalendarSheetShape,
                ambientColor = CalendarSheetShadowColor,
                spotColor = CalendarSheetShadowColor,
            )
            .clip(CalendarSheetShape)
            .background(LiroutiTheme.colors.backgroundDefault),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "<${yearMonth.year}년 ${yearMonth.monthValue}월>",
            style = LiroutiTheme.typography.heading2SemiBold,
            color = LiroutiTheme.colors.labelDefault,
            modifier = Modifier.padding(top = YearMonthLabelTopPadding),
        )

        // 라벨 아래 남는 공간을 전부 이 Box가 차지한다 — 휠+버튼 화면일 땐 그 안에서 정중앙/하단 정렬로
        // 쓰고, 그리드 화면으로 넘어가면 버튼이 사라지는 만큼 그리드가 이 공간을 그대로 넓게 채운다.
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            if (isSelectingYearMonth) {
                LiroutiYearMonthWheelPicker(
                    value = draftYearMonth,
                    onValueChange = { draftYearMonth = it },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .padding(horizontal = CalendarSelectorHorizontalMargin),
                )

                // HorizontalDoubleButton 내부의 기본 너비(containerWidth = 345.dp)는 Modifier.size()로
                // 적용되는데, size()는 required가 아니라서 바깥에서 들어오는 제약(fillMaxWidth -> padding)에
                // 맞춰 그대로 줄어든다 — 그래서 fillMaxWidth() + padding만으로 원하는 너비를 만들 수 있다.
                HorizontalSegment(
                    leftLabel = "취소",
                    rightLabel = "확인",
                    onLeftClick = {
                        draftYearMonth = LiroutiYearMonth(year = yearMonth.year, month = yearMonth.monthValue)
                        isSelectingYearMonth = false
                        onCancel()
                    },
                    onRightClick = {
                        onConfirm(YearMonth.of(draftYearMonth.year, draftYearMonth.month))
                        isSelectingYearMonth = false
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = CalendarActionButtonHorizontalMargin)
                        .padding(bottom = CalendarActionButtonBottomPadding),
                )
            } else {
                CalendarDayGrid(
                    yearMonth = yearMonth,
                    onDateConfirmed = onDateConfirmed,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = CalendarGridHorizontalMargin)
                        .padding(top = CalendarGridTopPadding, bottom = CalendarActionButtonBottomPadding),
                )
            }
        }
    }
}

/**
 * [yearMonth]의 요일 헤더 + 날짜 그리드(일요일 시작, 7열). 앞뒤 빈 칸은 공백으로 채운다.
 *
 * 날짜를 탭하면 첫 탭은 선택 표시(파란 박스 + 흰 숫자)만 하고, 이미 선택된 날짜를 다시 탭하면
 * [onDateConfirmed]를 호출한다 — 두 번째 탭이 곧 "이 날짜로 이동" 트리거다.
 */
@Composable
private fun CalendarDayGrid(
    yearMonth: YearMonth,
    onDateConfirmed: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val weeks = remember(yearMonth) { buildCalendarDayNumbers(yearMonth).chunked(7) }
    var selectedDay by remember(yearMonth) { mutableStateOf<Int?>(null) }

    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth()) {
            CalendarWeekdayLabels.forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = LiroutiTheme.typography.captionMedium,
                    color = LiroutiTheme.colors.labelInfo,
                )
            }
        }
        // 남은 세로 공간을 주 단위 행마다 균등하게 나눠 가져서(weight), 버튼이 사라져 넓어진 영역을
        // 그리드가 꽉 채운다 — 달이 5주짜리든 6주짜리든 항상 남는 공간 전체를 나눠 쓴다.
        weeks.forEach { week ->
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                week.forEach { day ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (day != null) {
                            val isSelected = day == selectedDay
                            Box(
                                modifier = Modifier
                                    .size(CalendarSelectedDaySize)
                                    .background(if (isSelected) CalendarSelectedDayColor else Color.Transparent)
                                    .clickable {
                                        if (isSelected) {
                                            onDateConfirmed(yearMonth.atDay(day))
                                        } else {
                                            selectedDay = day
                                        }
                                    },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = day.toString(),
                                    style = LiroutiTheme.typography.body3,
                                    color = if (isSelected) Color.White else LiroutiTheme.colors.labelDefault,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** 1일 앞뒤로 빈 칸(null)을 채워 7의 배수 길이로 만든, 일요일 시작 날짜 목록. */
private fun buildCalendarDayNumbers(yearMonth: YearMonth): List<Int?> {
    // DayOfWeek.value: 월=1 ... 일=7 이라 %7 하면 일=0으로 바뀌어 "일요일 시작" 오프셋이 된다.
    val leadingBlankCount = yearMonth.atDay(1).dayOfWeek.value % 7
    val days = List(leadingBlankCount) { null } + (1..yearMonth.lengthOfMonth()).toList()
    val trailingBlankCount = (7 - days.size % 7) % 7
    return days + List(trailingBlankCount) { null }
}

@Preview(showBackground = true)
@Composable
private fun CalendarSheetPreview() {
    LiroutiFrontendTheme {
        CalendarSheet(yearMonth = YearMonth.now())
    }
}