package com.li_routi.feature.mypage.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

data class AchievementBadgeUiModel(val title: String)

private val CellHeight = 120.dp
private val IconFrameWidth = 90.dp
private val IconFrameHeight = 78.dp
private val MedalImageWidth = 72.dp
private val MedalImageHeight = 69.dp
private val TitleTextStyle = TextStyle(fontSize = 13.sp, lineHeight = 16.sp, letterSpacing = (-0.325).sp)

private const val ColumnCount = 3

/**
 * "달성" 탭의 획득 배지 그리드. Figma node `4869:37738`("달성") 기준 —
 * 3열 그리드, 셀 간 여백 5dp, 각 셀 120dp 높이. 배지 획득 개수만큼 채우고 나머지는 빈 셀로 둔다.
 *
 * 마지막 줄이 3개를 못 채우면 `null`로 패딩해서 빈 셀(배경만 있고 아이콘/타이틀 없음, Figma 원본도
 * 동일)을 채운다 — 안 그러면 `weight(1f)`가 남은 셀에 다 몰려서 폭이 어긋난다.
 */
@Composable
fun AchievementBadgeGrid(
    badges: List<AchievementBadgeUiModel>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        badges.chunked(ColumnCount).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                val paddedRow = row + List(ColumnCount - row.size) { null }
                paddedRow.forEach { badge -> AchievementBadgeCell(badge = badge, modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun AchievementBadgeCell(badge: AchievementBadgeUiModel?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .height(CellHeight)
            .background(LiroutiTheme.colors.backgroundFill)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        if (badge != null) {
            Box(
                modifier = Modifier
                    .size(IconFrameWidth, IconFrameHeight)
                    .clip(RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_achievement_medal),
                    contentDescription = null,
                    modifier = Modifier.size(MedalImageWidth, MedalImageHeight),
                )
            }
            Text(
                text = badge.title,
                style = TitleTextStyle,
                color = LiroutiTheme.colors.labelDefault,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private val SampleBadges = listOf(
    AchievementBadgeUiModel("불꽃 연속"),
    AchievementBadgeUiModel("친구 부자"),
    AchievementBadgeUiModel("소셜 스타"),
    AchievementBadgeUiModel("꾸준한 루티너"),
)

@Preview(showBackground = true)
@Composable
private fun AchievementBadgeGridPreview() {
    LiroutiFrontendTheme {
        AchievementBadgeGrid(badges = SampleBadges, modifier = Modifier.padding(16.dp))
    }
}
