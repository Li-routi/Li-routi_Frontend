package com.li_routi.feature.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.component.LiroutiBadge
import com.li_routi.core.designsystem.component.LiroutiBadgeColor
import com.li_routi.core.designsystem.component.LiroutiBadgeSize
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

data class AchievementUiModel(
    val title: String,
    val rewardBadges: List<String>,
    val progressLabel: String,
    val progress: Float,
)

private val TitleTextStyle = TextStyle(fontSize = 14.sp, lineHeight = 22.sp, letterSpacing = (-0.35).sp)
private val ProgressLabelTextStyle = TextStyle(fontSize = 12.sp, lineHeight = 18.sp)

/**
 * 업적 목록 한 줄. Figma node `205:18271`("Check_container") 기준 —
 * 타이틀 + 보상 뱃지(선택) + 진행률 텍스트 + 선형 진행바.
 */
@Composable
fun AchievementListItem(
    item: AchievementUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = item.title,
                style = TitleTextStyle,
                color = LiroutiTheme.colors.labelStrong,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            item.rewardBadges.forEach { badge ->
                LiroutiBadge(text = badge, color = LiroutiBadgeColor.Blue, size = LiroutiBadgeSize.Small)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = item.progressLabel, style = ProgressLabelTextStyle, color = LiroutiTheme.colors.labelSub)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .background(LiroutiTheme.colors.borderDefault, RoundedCornerShape(10.dp)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(item.progress.coerceIn(0f, 1f))
                        .background(LiroutiTheme.colors.primaryNormal, RoundedCornerShape(10.dp)),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AchievementListItemPreview() {
    LiroutiFrontendTheme {
        Column {
            AchievementListItem(
                item = AchievementUiModel(
                    title = "친구 부자",
                    rewardBadges = listOf("50코인", "배지"),
                    progressLabel = "5/7",
                    progress = 0.6f,
                ),
            )
            AchievementListItem(
                item = AchievementUiModel(
                    title = "꾸준한 루티너",
                    rewardBadges = emptyList(),
                    progressLabel = "50코인",
                    progress = 0.6f,
                ),
            )
        }
    }
}
