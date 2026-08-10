package com.li_routi.feature.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * 업적 등급. Figma node `4201:34400`에는 레어/에픽 배지 색상만 예시가 있고 유니크는 예시가 없어서,
 * 노란색 계열로 임시 지정했다 — 실제 스펙이 나오면 교체해야 한다.
 */
enum class AchievementRarity(val label: String, val backgroundColor: Color, val textColor: Color) {
    Rare(label = "레어", backgroundColor = Color(0xFFF4F7FB), textColor = Color(0xFF00AAD2)),
    Epic(label = "에픽", backgroundColor = Color(0xFFEFE0F8), textColor = Color(0xFF6903D6)),
    Unique(label = "유니크", backgroundColor = Color(0xFFFFF6D8), textColor = Color(0xFFA67C00)),
}

data class AchievementUiModel(
    val title: String,
    val rarity: AchievementRarity,
    val description: String,
    val progressLabel: String,
    val progress: Float,
    val rewardText: String? = null,
)

private val TitleTextStyle = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 22.sp, letterSpacing = (-0.35).sp)
private val RarityBadgeTextStyle = TextStyle(fontSize = 11.sp, lineHeight = 14.sp)
private val RewardTextStyle = TextStyle(fontSize = 11.sp, lineHeight = 14.sp)
private val DescriptionTextStyle = TextStyle(fontSize = 11.sp, lineHeight = 14.sp)
private val ProgressCountTextStyle = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 18.sp)

private val CardBorderColor = Color(0xFFF4F4F5)
private val ProgressCountColor = Color(0xFF81898E)
private val CharacterIconSize = 60.dp

/**
 * 업적 목록 카드 한 장. Figma node `4714:40629`("List") 기준 —
 * 캐릭터 아이콘 + (타이틀·등급 배지·보상) + 설명 + 진행률 바(오른쪽에 진행 카운트).
 */
@Composable
fun AchievementListItem(
    item: AchievementUiModel,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(LiroutiTheme.colors.backgroundDefault, RoundedCornerShape(6.dp))
            .border(1.dp, CardBorderColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(CharacterIconSize)
                .background(LiroutiTheme.colors.backgroundDefault, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            AchievementCharacterIcon(modifier = Modifier.size(width = 69.dp, height = 56.dp))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        modifier = Modifier.weight(1f, fill = false),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = item.title,
                            style = TitleTextStyle,
                            color = LiroutiTheme.colors.labelDefault,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        AchievementRarityBadge(rarity = item.rarity)
                    }
                    if (item.rewardText != null) {
                        Text(text = item.rewardText, style = RewardTextStyle, color = LiroutiTheme.colors.primaryNormal)
                    }
                }
                Text(text = item.description, style = DescriptionTextStyle, color = LiroutiTheme.colors.labelSub)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
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
                Text(text = item.progressLabel, style = ProgressCountTextStyle, color = ProgressCountColor)
            }
        }
    }
}

@Composable
private fun AchievementRarityBadge(rarity: AchievementRarity, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(rarity.backgroundColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 2.dp),
    ) {
        Text(text = rarity.label, style = RarityBadgeTextStyle, color = rarity.textColor)
    }
}

@Preview(showBackground = true)
@Composable
private fun AchievementListItemPreview() {
    LiroutiFrontendTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AchievementListItem(
                item = AchievementUiModel(
                    title = "불꽃연속",
                    rarity = AchievementRarity.Rare,
                    description = "30일 연속 루틴을 달성하세요",
                    progressLabel = "27/30",
                    progress = 27f / 30f,
                    rewardText = "+50코인",
                ),
            )
            AchievementListItem(
                item = AchievementUiModel(
                    title = "불꽃연속",
                    rarity = AchievementRarity.Epic,
                    description = "30일 연속 루틴을 달성하세요",
                    progressLabel = "27/30",
                    progress = 27f / 30f,
                ),
            )
            AchievementListItem(
                item = AchievementUiModel(
                    title = "불꽃연속",
                    rarity = AchievementRarity.Unique,
                    description = "30일 연속 루틴을 달성하세요",
                    progressLabel = "27/30",
                    progress = 27f / 30f,
                ),
            )
        }
    }
}
