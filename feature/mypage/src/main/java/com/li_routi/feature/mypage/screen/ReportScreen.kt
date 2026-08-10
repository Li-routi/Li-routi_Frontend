package com.li_routi.feature.mypage.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.LiroutiDivider
import com.li_routi.core.designsystem.component.LiroutiDividerThickness
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.mypage.component.ActivityStatUiModel
import com.li_routi.feature.mypage.component.ActivityStatsGrid
import com.li_routi.feature.mypage.component.BlackDay
import com.li_routi.feature.mypage.component.BlueDay
import com.li_routi.feature.mypage.component.EditProfileTopBar
import com.li_routi.feature.mypage.component.MonthlyDayUiModel
import com.li_routi.feature.mypage.component.MonthlyReportCard
import com.li_routi.feature.mypage.component.RedDay
import com.li_routi.feature.mypage.component.ReportPeriodTabs
import com.li_routi.feature.mypage.component.SimpleDate
import com.li_routi.feature.mypage.component.WeeklyBarUiModel
import com.li_routi.feature.mypage.component.WeeklyReportCard
import com.li_routi.feature.mypage.component.plusDays
import com.li_routi.feature.mypage.component.todaySimpleDate
import java.util.Calendar

private const val PeriodTabWeekly = 0
private const val PeriodTabMonthly = 1

/**
 * 리포트 화면. Figma node `3610:30619`("리포트" 주간)/`3610:30680`("리포트" 월간) 기준 —
 * 마이페이지 "리포트" 메뉴로 진입한다.
 *
 * 주간/월간 탭 + 기간별 달성률 그래프(주간: 막대그래프, 월간: 달력) + 활동 통계로 구성된다.
 * ◀▶로 주차/월을 이동하면 라벨과(월간은) 달력 모양이 그 기간에 맞게 바뀐다 — 다만 막대그래프/달성률
 * 자체는 아직 실제 기간별 데이터 연동 전이라 항상 같은 샘플 값을 보여준다.
 */
@Composable
fun ReportScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    weeklyBars: List<WeeklyBarUiModel> = SampleWeeklyBars,
    activityStats: List<ActivityStatUiModel> = SampleActivityStats,
) {
    var selectedTab by remember { mutableIntStateOf(PeriodTabWeekly) }
    var weekAnchor by remember { mutableStateOf(todaySimpleDate()) }
    var monthAnchor by remember { mutableStateOf(todaySimpleDate().let { it.year to it.month }) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundDefault),
    ) {
        EditProfileTopBar(title = "리포트", onBackClick = onBackClick)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = 20.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(25.dp),
        ) {
            ReportPeriodTabs(
                tabs = listOf("주간", "월간"),
                selectedIndex = selectedTab,
                onTabSelected = { selectedTab = it },
            )
            if (selectedTab == PeriodTabWeekly) {
                WeeklyReportCard(
                    weekLabel = weekLabel(weekAnchor),
                    bars = weeklyBars,
                    onPreviousWeekClick = { weekAnchor = weekAnchor.plusDays(-7) },
                    onNextWeekClick = { weekAnchor = weekAnchor.plusDays(7) },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            } else if (selectedTab == PeriodTabMonthly) {
                val (year, month) = monthAnchor
                MonthlyReportCard(
                    monthLabel = monthLabel(year, month),
                    days = buildMonthlyDays(year, month),
                    onPreviousMonthClick = { monthAnchor = stepMonth(year, month, -1) },
                    onNextMonthClick = { monthAnchor = stepMonth(year, month, 1) },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            LiroutiDivider(thickness = LiroutiDividerThickness.ExtraBold, color = LiroutiTheme.colors.borderSub)
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(text = "활동 통계", style = LiroutiTheme.typography.body1Bold, color = LiroutiTheme.colors.labelStrong)
                ActivityStatsGrid(stats = activityStats)
            }
        }
    }
}

/** 일요일 시작 기준 그 달의 몇 번째 주인지("N주차") — 1일이 속한 주가 1주차. */
private fun weekOfMonth(date: SimpleDate): Int {
    val firstWeekdayOfMonth = Calendar.getInstance().apply { set(date.year, date.month - 1, 1) }.get(Calendar.DAY_OF_WEEK) - 1
    return (date.day - 1 + firstWeekdayOfMonth) / 7 + 1
}

private fun weekLabel(date: SimpleDate): String = "%d년 %02d월 · %d주차".format(date.year, date.month, weekOfMonth(date))

private fun monthLabel(year: Int, month: Int): String = "%d년 %02d월".format(year, month)

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

/**
 * [year]년 [month]월 달력 그리드를 만든다. 달성률(ratio)은 실제 데이터가 없어 Figma 목업(2026년 9월
 * 1/5/8일)의 값을 요일 상관없이 날짜 번호에 그대로 고정해 재사용한다 — 다른 달로 이동해도 그 달의
 * 1/5/8일에 같은 자리표시자 값이 보인다.
 */
private fun buildMonthlyDays(year: Int, month: Int): List<MonthlyDayUiModel> {
    val firstWeekday = Calendar.getInstance().apply { set(year, month - 1, 1) }.get(Calendar.DAY_OF_WEEK) - 1
    val total = Calendar.getInstance().apply { set(year, month - 1, 1) }.getActualMaximum(Calendar.DAY_OF_MONTH)

    val cells = ArrayList<Pair<Int?, Float>>(42)
    repeat(firstWeekday) { cells.add(null to 0f) }
    (1..total).forEach { day ->
        val ratio = when (day) {
            1 -> 1f
            5 -> 12f / 28f
            8 -> 19f / 28f
            else -> 0f
        }
        cells.add(day to ratio)
    }
    while (cells.size % 7 != 0) cells.add(null to 0f)

    return cells.mapIndexed { index, (day, ratio) ->
        val dayColor = when (index % 7) {
            0 -> RedDay
            6 -> BlueDay
            else -> BlackDay
        }
        MonthlyDayUiModel(day = day, dayColor = dayColor, ratio = ratio)
    }
}

// Figma 샘플의 막대 높이 비율(30%/50%/100%·100%/100%·50%/0/0/0)을 그대로 재현한 값.
private val SampleWeeklyBars = listOf(
    WeeklyBarUiModel(registeredCount = 3, completedCount = 0),
    WeeklyBarUiModel(registeredCount = 5, completedCount = 0),
    WeeklyBarUiModel(registeredCount = 10, completedCount = 10),
    WeeklyBarUiModel(registeredCount = 10, completedCount = 5),
    WeeklyBarUiModel(registeredCount = 0, completedCount = 0),
    WeeklyBarUiModel(registeredCount = 0, completedCount = 0),
    WeeklyBarUiModel(registeredCount = 0, completedCount = 0),
)

private val SampleActivityStats = listOf(
    ActivityStatUiModel("이번 달 완료 루틴", "100"),
    ActivityStatUiModel("이번 달 평균 달성률", "98%"),
    ActivityStatUiModel("완료한 챌린지", "12"),
    ActivityStatUiModel("이번 달 획득 코인", "2300"),
)

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun ReportScreenPreview() {
    LiroutiFrontendTheme {
        ReportScreen(onBackClick = {})
    }
}
