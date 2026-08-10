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

/** 하루치 막대. [registeredCount]는 그날 등록된 루틴 수, [completedCount]는 그중 완료한 수. */
data class WeeklyBarUiModel(
    val registeredCount: Int,
    val completedCount: Int,
)

private val ChartHeight = 100.dp
private val MinBarHeight = 4.dp

/**
 * 리포트 "주간" 카드. Figma node `3610:30619`("Component 2") 기준 —
 * 주차 선택(◀ 2026년 09월 · 1주차 ▶) + 요일 라벨 + 요일별 막대그래프.
 *
 * 막대 높이는 그 주 등록 수 최댓값을 기준으로 스케일링한다 — 회색(등록)이 막대 전체 높이, 파랑(완료)이
 * 그 안에서 아래쪽부터 채워지는 부분이다(완료 ≤ 등록이라 파랑이 회색을 넘지 않는다).
 */
@Composable
fun WeeklyReportCard(
    weekLabel: String,
    bars: List<WeeklyBarUiModel>,
    onPreviousWeekClick: () -> Unit,
    onNextWeekClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val maxRegistered = bars.maxOfOrNull { it.registeredCount }?.coerceAtLeast(1) ?: 1

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
                    val registeredRatio = bar.registeredCount / maxRegistered.toFloat()
                    val completedRatio = bar.completedCount / maxRegistered.toFloat()
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .height(ChartHeight),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        // 등록이 0이어도 Figma는 바닥에 얇은 회색 선(4dp)을 남겨 그 요일 칸이 있다는 걸
                        // 표시한다 — 그래서 registeredRatio가 0이어도 이 회색 바는 항상 그린다.
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((ChartHeight * registeredRatio).coerceAtLeast(MinBarHeight))
                                .background(LiroutiTheme.colors.borderSub, RoundedCornerShape(6.dp)),
                        )
                        if (completedRatio > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height((ChartHeight * completedRatio).coerceAtLeast(MinBarHeight))
                                    .background(LiroutiTheme.colors.primaryNormal, RoundedCornerShape(6.dp)),
                            )
                        }
                    }
                }
            }
        }
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

@Preview(showBackground = true)
@Composable
private fun WeeklyReportCardPreview() {
    LiroutiFrontendTheme {
        WeeklyReportCard(
            weekLabel = "2026년 09월 · 1주차",
            bars = SampleWeeklyBars,
            onPreviousWeekClick = {},
            onNextWeekClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
