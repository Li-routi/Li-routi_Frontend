package com.li_routi.feature.mypage.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.di.ReportContainer
import com.li_routi.core.domain.report.GetMonthlyReportUseCase
import com.li_routi.core.domain.report.GetWeeklyReportUseCase
import com.li_routi.core.domain.report.ReportDay
import com.li_routi.core.domain.report.ReportStats
import com.li_routi.feature.mypage.component.ActivityStatUiModel
import com.li_routi.feature.mypage.component.BlackDay
import com.li_routi.feature.mypage.component.BlueDay
import com.li_routi.feature.mypage.component.MonthlyDayUiModel
import com.li_routi.feature.mypage.component.RedDay
import com.li_routi.feature.mypage.component.SimpleDate
import com.li_routi.feature.mypage.component.WeeklyBarUiModel
import com.li_routi.feature.mypage.component.plusDays
import com.li_routi.feature.mypage.component.toApiDateString
import com.li_routi.feature.mypage.component.todaySimpleDate
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val PeriodTabWeekly = 0
private const val PeriodTabMonthly = 1

/**
 * 리포트 화면 ViewModel. "주간" 탭은 `GET /api/members/me/reports/weekly`, "월간" 탭은
 * `GET /api/members/me/reports/monthly`를 호출한다. 두 API 모두 "활동 통계"는 그 주/달이 표시되는
 * 달 기준으로 내려주므로([ReportStats] 문서 참고), 탭과 무관하게 같은 [ActivityStatUiModel] 목록으로
 * 매핑해 화면 하단에 그대로 보여준다.
 */
class ReportViewModel(
    private val getWeeklyReportUseCase: GetWeeklyReportUseCase = ReportContainer.getWeeklyReportUseCase,
    private val getMonthlyReportUseCase: GetMonthlyReportUseCase = ReportContainer.getMonthlyReportUseCase,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ReportUiState(isLoading = true))
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    init {
        loadWeekly()
    }

    fun onTabSelected(tab: Int) {
        if (tab == _uiState.value.selectedTab) return
        _uiState.update { it.copy(selectedTab = tab) }
        if (tab == PeriodTabWeekly) loadWeekly() else loadMonthly()
    }

    fun onPreviousWeekClick() {
        _uiState.update { it.copy(weekAnchor = it.weekAnchor.plusDays(-7)) }
        loadWeekly()
    }

    fun onNextWeekClick() {
        _uiState.update { it.copy(weekAnchor = it.weekAnchor.plusDays(7)) }
        loadWeekly()
    }

    fun onPreviousMonthClick() {
        _uiState.update { it.copy(monthAnchor = stepMonth(it.monthAnchor, -1)) }
        loadMonthly()
    }

    fun onNextMonthClick() {
        _uiState.update { it.copy(monthAnchor = stepMonth(it.monthAnchor, 1)) }
        loadMonthly()
    }

    private fun loadWeekly() {
        val requestedWeek = _uiState.value.weekAnchor
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isError = false) }
            when (val result = getWeeklyReportUseCase(date = requestedWeek.toApiDateString())) {
                is ResultState.Success -> _uiState.update { current ->
                    if (current.weekAnchor != requestedWeek) return@update current
                    val report = result.data
                    current.copy(
                        weekLabel = "${report.displayMonth.toDisplayMonthLabel()} · ${report.weekOfMonth}주차",
                        weeklyBars = report.days.map {
                            WeeklyBarUiModel(registeredCount = it.scheduledCount, completedCount = it.completedCount)
                        },
                        activityStats = report.stats.toUiModel(),
                        isLoading = false,
                    )
                }
                is ResultState.Error -> _uiState.update { current ->
                    if (current.weekAnchor != requestedWeek) current else current.copy(isLoading = false, isError = true)
                }
                ResultState.Loading -> Unit
            }
        }
    }

    private fun loadMonthly() {
        val requestedMonth = _uiState.value.monthAnchor
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isError = false) }
            when (val result = getMonthlyReportUseCase(yearMonth = requestedMonth.toApiYearMonthString())) {
                is ResultState.Success -> _uiState.update { current ->
                    if (current.monthAnchor != requestedMonth) return@update current
                    val report = result.data
                    current.copy(
                        monthLabel = monthLabel(requestedMonth),
                        monthlyDays = buildMonthlyDays(requestedMonth, report.days),
                        activityStats = report.stats.toUiModel(),
                        isLoading = false,
                    )
                }
                is ResultState.Error -> _uiState.update { current ->
                    if (current.monthAnchor != requestedMonth) current else current.copy(isLoading = false, isError = true)
                }
                ResultState.Loading -> Unit
            }
        }
    }
}

data class ReportUiState(
    val selectedTab: Int = PeriodTabWeekly,
    val weekAnchor: SimpleDate = todaySimpleDate(),
    val weekLabel: String = "",
    val weeklyBars: List<WeeklyBarUiModel> = emptyList(),
    val monthAnchor: Pair<Int, Int> = todaySimpleDate().let { it.year to it.month },
    val monthLabel: String = "",
    val monthlyDays: List<MonthlyDayUiModel> = emptyList(),
    val activityStats: List<ActivityStatUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
)

private fun ReportStats.toUiModel(): List<ActivityStatUiModel> = listOf(
    ActivityStatUiModel("이번 달 완료 루틴", completedRoutineCount.toString()),
    ActivityStatUiModel("이번 달 평균 달성률", "$averageCompletionRate%"),
    ActivityStatUiModel("완료한 챌린지", completedChallengeCount.toString()),
    ActivityStatUiModel("이번 달 획득 코인", earnedCoinCount.toString()),
)

private fun monthLabel(month: Pair<Int, Int>): String = "%d년 %02d월".format(month.first, month.second)

private fun stepMonth(month: Pair<Int, Int>, delta: Int): Pair<Int, Int> {
    var y = month.first
    var m = month.second + delta
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

private fun Pair<Int, Int>.toApiYearMonthString(): String = String.format(Locale.US, "%04d-%02d", first, second)

/** [WeeklyReport.displayMonth]은 이름과 달리 "yyyy-MM" 원시 포맷으로 온다 — "년/월" 표시용으로 바꾼다. */
private fun String.toDisplayMonthLabel(): String {
    val (year, month) = split("-").map { it.toInt() }
    return "%d년 %02d월".format(year, month)
}

/**
 * [month] 달력 그리드를 만든다. [apiDays]는 해당 월에 속한 날짜만(선행/후행 패딩 없이) 오므로, 1일의
 * 요일만큼 앞을 비우고 뒤는 7의 배수가 되게 채운다([com.li_routi.feature.mypage.screen.ReportScreen]이
 * 예전에 Figma 목업으로 하던 것과 같은 패딩 규칙, 이제는 실제 날짜별 완료/예정 건수로 ratio를 계산한다).
 */
private fun buildMonthlyDays(month: Pair<Int, Int>, apiDays: List<ReportDay>): List<MonthlyDayUiModel> {
    val (year, monthOfYear) = month
    val countsByDay = apiDays.associateBy { it.date.takeLast(2).toInt() }
    val firstWeekday = Calendar.getInstance().apply { set(year, monthOfYear - 1, 1) }.get(Calendar.DAY_OF_WEEK) - 1
    val total = Calendar.getInstance().apply { set(year, monthOfYear - 1, 1) }.getActualMaximum(Calendar.DAY_OF_MONTH)

    val cells = ArrayList<Pair<Int?, Float>>(42)
    repeat(firstWeekday) { cells.add(null to 0f) }
    (1..total).forEach { day ->
        val reportDay = countsByDay[day]
        val ratio = if (reportDay != null && reportDay.scheduledCount > 0) {
            reportDay.completedCount.toFloat() / reportDay.scheduledCount
        } else {
            0f
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
