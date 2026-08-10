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
import com.li_routi.feature.mypage.component.EditProfileTopBar
import com.li_routi.feature.mypage.component.MonthlyDayUiModel
import com.li_routi.feature.mypage.component.MonthlyReportCard
import com.li_routi.feature.mypage.component.ReportPeriodTabs
import com.li_routi.feature.mypage.component.SampleMonthlyDays
import com.li_routi.feature.mypage.component.WeeklyBarUiModel
import com.li_routi.feature.mypage.component.WeeklyReportCard

private const val PeriodTabWeekly = 0
private const val PeriodTabMonthly = 1

/**
 * 리포트 화면. Figma node `205:18355`("리포트")/`205:18416`("리포트" 월간) 기준 —
 * 마이페이지 "리포트" 메뉴로 진입한다.
 *
 * 주간/월간 탭 + 기간별 달성률 그래프(주간: 막대그래프, 월간: 달력) + 활동 통계로 구성된다.
 */
@Composable
fun ReportScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    weekLabel: String = "2026.09 · 1주차",
    weeklyBars: List<WeeklyBarUiModel> = SampleWeeklyBars,
    monthLabel: String = "2026.09",
    monthlyDays: List<MonthlyDayUiModel> = SampleMonthlyDays,
    activityStats: List<ActivityStatUiModel> = SampleActivityStats,
    onPreviousWeekClick: () -> Unit = {},
    onNextWeekClick: () -> Unit = {},
    onPreviousMonthClick: () -> Unit = {},
    onNextMonthClick: () -> Unit = {},
) {
    var selectedTab by remember { mutableIntStateOf(PeriodTabWeekly) }

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
                .padding(top = 20.dp),
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

private val SampleWeeklyBars = listOf(
    WeeklyBarUiModel(0.3f),
    WeeklyBarUiModel(0.5f),
    WeeklyBarUiModel(1f),
    WeeklyBarUiModel(0.5f),
    WeeklyBarUiModel(0f),
    WeeklyBarUiModel(0f),
    WeeklyBarUiModel(0f),
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
