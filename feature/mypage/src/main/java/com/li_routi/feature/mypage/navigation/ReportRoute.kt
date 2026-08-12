package com.li_routi.feature.mypage.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.li_routi.feature.mypage.screen.ReportScreen
import com.li_routi.feature.mypage.vm.ReportViewModel

/** "리포트" 화면 진입점. [ReportViewModel]과 [ReportScreen]을 연결한다. */
@Composable
fun ReportRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReportViewModel = viewModel { ReportViewModel() },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ReportScreen(
        onBackClick = onBackClick,
        selectedTab = uiState.selectedTab,
        onTabSelected = viewModel::onTabSelected,
        weekLabel = uiState.weekLabel,
        weeklyBars = uiState.weeklyBars,
        onPreviousWeekClick = viewModel::onPreviousWeekClick,
        onNextWeekClick = viewModel::onNextWeekClick,
        monthLabel = uiState.monthLabel,
        monthlyDays = uiState.monthlyDays,
        onPreviousMonthClick = viewModel::onPreviousMonthClick,
        onNextMonthClick = viewModel::onNextMonthClick,
        activityStats = uiState.activityStats,
        isLoading = uiState.isLoading,
        isError = uiState.isError,
        modifier = modifier,
    )
}
