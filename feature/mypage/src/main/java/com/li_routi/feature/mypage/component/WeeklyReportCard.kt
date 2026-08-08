package com.li_routi.feature.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

data class WeeklyBarUiModel(
    val ratio: Float,
)

private val ChartHeight = 100.dp
private val MinBarHeight = 4.dp

/**
 * 리포트 "주간" 카드. Figma node `205:18362`("Component 2") 기준 —
 * 주차 선택(◀ 2026.09 · 1주차 ▶) + 요일 라벨 + 요일별 달성률 막대그래프.
 */
@Composable
fun WeeklyReportCard(
    weekLabel: String,
    bars: List<WeeklyBarUiModel>,
    onPreviousWeekClick: () -> Unit,
    onNextWeekClick: () -> Unit,
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
        ReportPeriodHeader(label = weekLabel, onPreviousClick = onPreviousWeekClick, onNextClick = onNextWeekClick)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ReportWeekdayLabelsRow()
            Row(
                modifier = Modifier.fillMaxWidth().height(ChartHeight),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                bars.forEach { bar ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .height(ChartHeight)
                            .background(LiroutiTheme.colors.borderSub, RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((ChartHeight * bar.ratio.coerceIn(0f, 1f)).coerceAtLeast(MinBarHeight))
                                .background(LiroutiTheme.colors.primaryNormal, RoundedCornerShape(6.dp)),
                        )
                    }
                }
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

@Preview(showBackground = true)
@Composable
private fun WeeklyReportCardPreview() {
    LiroutiFrontendTheme {
        WeeklyReportCard(
            weekLabel = "2026.09 · 1주차",
            bars = SampleWeeklyBars,
            onPreviousWeekClick = {},
            onNextWeekClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
