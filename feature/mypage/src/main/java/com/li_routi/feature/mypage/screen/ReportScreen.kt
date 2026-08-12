package com.li_routi.feature.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.R
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
import com.li_routi.feature.mypage.component.WeeklyBarUiModel
import com.li_routi.feature.mypage.component.WeeklyReportCard

private const val PeriodTabWeekly = 0
private const val PeriodTabMonthly = 1

/**
 * 리포트 화면. Figma node `3610:30619`("리포트" 주간)/`3610:30680`("리포트" 월간) 기준 —
 * 마이페이지 "리포트" 메뉴로 진입한다.
 *
 * 주간/월간 탭 + 기간별 달성률 그래프(주간: 막대그래프, 월간: 달력) + 활동 통계로 구성된다.
 * `GET /api/members/me/reports/weekly`·`/monthly`를 다시 호출해야 해서(
 * [com.li_routi.feature.mypage.vm.ReportViewModel]) 탭/주차/월 선택 상태를 화면이 직접 들고 있지
 * 않고 끌어올렸다 — ◀▶나 탭을 누르면 각 콜백을 통해 상위(ViewModel)가 새로 조회한다.
 */
@Composable
fun ReportScreen(
    onBackClick: () -> Unit,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    weekLabel: String,
    weeklyBars: List<WeeklyBarUiModel>,
    onPreviousWeekClick: () -> Unit,
    onNextWeekClick: () -> Unit,
    monthLabel: String,
    monthlyDays: List<MonthlyDayUiModel>,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    activityStats: List<ActivityStatUiModel>,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isError: Boolean = false,
) {
    // 이미 보여줄 데이터가 있으면(탭을 최소 한 번은 불러왔으면) ◀▶나 탭 전환으로 다시 불러오는 동안에도
    // 기존 내용을 그대로 두고, 완전히 처음 불러올 때만 전체 화면 스피너/에러로 바꾼다 — 안 그러면
    // 매번 화면이 통째로 깜빡이며 새로고침되는 것처럼 보인다.
    val hasContent = if (selectedTab == PeriodTabWeekly) weeklyBars.isNotEmpty() else monthlyDays.isNotEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundDefault),
    ) {
        EditProfileTopBar(title = "리포트", onBackClick = onBackClick)
        if (isLoading && !hasContent) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LiroutiTheme.colors.primaryNormal)
            }
        } else if (isError && !hasContent) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                ReportEmptyState(message = "리포트를 불러오지 못했어요")
            }
        } else {
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
                    onTabSelected = onTabSelected,
                )
                if (selectedTab == PeriodTabWeekly) {
                    WeeklyReportCard(
                        weekLabel = weekLabel,
                        bars = weeklyBars,
                        onPreviousWeekClick = onPreviousWeekClick,
                        onNextWeekClick = onNextWeekClick,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                } else if (selectedTab == PeriodTabMonthly) {
                    MonthlyReportCard(
                        monthLabel = monthLabel,
                        days = monthlyDays,
                        onPreviousMonthClick = onPreviousMonthClick,
                        onNextMonthClick = onNextMonthClick,
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
}

/** 리포트 조회에 실패했을 때 화면 가운데에 보여주는 상태. */
@Composable
private fun ReportEmptyState(modifier: Modifier = Modifier, message: String) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = R.drawable.warning),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
        )
        Text(
            text = message,
            style = LiroutiTheme.typography.body2LongMedium,
            color = LiroutiTheme.colors.labelInfo,
            modifier = Modifier.padding(top = 8.dp),
        )
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
        ReportScreen(
            onBackClick = {},
            selectedTab = PeriodTabWeekly,
            onTabSelected = {},
            weekLabel = "2026년 09월 · 1주차",
            weeklyBars = SampleWeeklyBars,
            onPreviousWeekClick = {},
            onNextWeekClick = {},
            monthLabel = "2026년 09월",
            monthlyDays = SampleMonthlyDays,
            onPreviousMonthClick = {},
            onNextMonthClick = {},
            activityStats = SampleActivityStats,
        )
    }
}

@Preview(showBackground = true, heightDp = 900, name = "로딩 중")
@Composable
private fun ReportScreenLoadingPreview() {
    LiroutiFrontendTheme {
        ReportScreen(
            onBackClick = {},
            selectedTab = PeriodTabWeekly,
            onTabSelected = {},
            weekLabel = "",
            weeklyBars = emptyList(),
            onPreviousWeekClick = {},
            onNextWeekClick = {},
            monthLabel = "",
            monthlyDays = emptyList(),
            onPreviousMonthClick = {},
            onNextMonthClick = {},
            activityStats = emptyList(),
            isLoading = true,
        )
    }
}

@Preview(showBackground = true, heightDp = 900, name = "조회 실패")
@Composable
private fun ReportScreenErrorPreview() {
    LiroutiFrontendTheme {
        ReportScreen(
            onBackClick = {},
            selectedTab = PeriodTabWeekly,
            onTabSelected = {},
            weekLabel = "",
            weeklyBars = emptyList(),
            onPreviousWeekClick = {},
            onNextWeekClick = {},
            monthLabel = "",
            monthlyDays = emptyList(),
            onPreviousMonthClick = {},
            onNextMonthClick = {},
            activityStats = emptyList(),
            isError = true,
        )
    }
}
