package com.li_routi.feature.mypage.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.theme.LiroutiTheme

private val PeriodLabelTextStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = (-0.08).sp)
private val WeekdayLabelTextStyle = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 14.sp)

/** 리포트 주간/월간 카드 상단의 "◀ 기간 라벨 ▶" 이전/다음 이동 행. */
@Composable
fun ReportPeriodHeader(
    label: String,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable.chevron__left),
            contentDescription = "이전",
            modifier = Modifier.size(16.dp).clickable(onClick = onPreviousClick),
        )
        Text(text = label, style = PeriodLabelTextStyle, color = LiroutiTheme.colors.labelStrong)
        Image(
            painter = painterResource(id = R.drawable.chevron__right),
            contentDescription = "다음",
            modifier = Modifier.size(16.dp).clickable(onClick = onNextClick),
        )
    }
}

/** 일~토 요일 라벨 행(일=danger/text, 토=primary/normal). Figma node `205:18369`/`3344:39748` 기준. */
@Composable
fun ReportWeekdayLabelsRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 9.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        ReportWeekdayLabels.forEach { (label, color) ->
            Text(text = label, style = WeekdayLabelTextStyle, color = color)
        }
    }
}

val ReportWeekdayLabels: List<Pair<String, Color>> = listOf(
    "일" to Color(0xFFFF4242),
    "월" to Color(0xFF171719),
    "화" to Color(0xFF171719),
    "수" to Color(0xFF171719),
    "목" to Color(0xFF171719),
    "금" to Color(0xFF171719),
    "토" to Color(0xFF338AFF),
)
