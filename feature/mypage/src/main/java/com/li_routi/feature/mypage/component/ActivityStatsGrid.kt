package com.li_routi.feature.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

data class ActivityStatUiModel(
    val label: String,
    val value: String,
)

private val StatLabelTextStyle = TextStyle(fontSize = 12.sp, lineHeight = 14.sp)
private val StatValueTextStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 28.sp, letterSpacing = (-0.45).sp)

/** 리포트 "활동 통계" 2x2 카드 그리드. Figma node `205:18399` 기준. */
@Composable
fun ActivityStatsGrid(
    stats: List<ActivityStatUiModel>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        stats.chunked(2).forEach { rowStats ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowStats.forEach { stat ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(LiroutiTheme.colors.backgroundAlternative, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(text = stat.label, style = StatLabelTextStyle, color = LiroutiTheme.colors.labelSub)
                        Text(text = stat.value, style = StatValueTextStyle, color = LiroutiTheme.colors.labelDefault)
                    }
                }
            }
        }
    }
}

private val SampleActivityStats = listOf(
    ActivityStatUiModel("이번 달 완료 루틴", "100"),
    ActivityStatUiModel("이번 달 평균 달성률", "98%"),
    ActivityStatUiModel("완료한 챌린지", "12"),
    ActivityStatUiModel("이번 달 획득 코인", "2300"),
)

@Preview(showBackground = true)
@Composable
private fun ActivityStatsGridPreview() {
    LiroutiFrontendTheme {
        ActivityStatsGrid(stats = SampleActivityStats, modifier = Modifier.padding(16.dp))
    }
}
