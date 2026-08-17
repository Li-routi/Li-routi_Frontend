package com.li_routi.feature.mypage.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.li_routi.core.designsystem.foundation.color.AchievementProgressCountColor
import com.li_routi.core.designsystem.foundation.color.BackgroundSecondary
import com.li_routi.core.designsystem.foundation.color.Cyan500
import com.li_routi.core.designsystem.foundation.color.GradeCharacterBackground
import com.li_routi.core.designsystem.foundation.color.GradeCharacterText
import com.li_routi.core.designsystem.foundation.color.GradeEpicBackground
import com.li_routi.core.designsystem.foundation.color.GradeEpicText
import com.li_routi.core.designsystem.foundation.color.GradeUniqueBackground
import com.li_routi.core.designsystem.foundation.color.GradeUniqueText
import com.li_routi.core.designsystem.foundation.color.Neutral98
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * 업적 등급. Figma node `6008:31220`("전체")/`6008:31578`("레어")/`6008:31510`("에픽")/`6008:31459`("유니크") 기준.
 * [Character]는 서버 카테고리 `EGG`(캐릭터 해금용 알 업적)를 화면에 보여주는 이름이다.
 */
enum class AchievementRarity(val label: String, val backgroundColor: Color, val textColor: Color) {
    Rare(label = "레어", backgroundColor = BackgroundSecondary, textColor = Cyan500),
    Epic(label = "에픽", backgroundColor = GradeEpicBackground, textColor = GradeEpicText),
    Unique(label = "유니크", backgroundColor = GradeUniqueBackground, textColor = GradeUniqueText),
    Character(label = "캐릭터", backgroundColor = GradeCharacterBackground, textColor = GradeCharacterText),
}

data class AchievementUiModel(
    val title: String,
    val rarity: AchievementRarity,
    val description: String,
    val progressLabel: String,
    val progress: Float,
    val rewardText: String? = null,
    val isInProgress: Boolean = false,
    val isAchieved: Boolean = false,
    @param:DrawableRes val iconRes: Int? = null,
    /** 서버 제공 뱃지 이미지. 있으면 [iconRes]보다 우선한다. */
    val imageUrl: String? = null,
    /** [onClaimClick]에 넘길 식별자. 실제 데이터는 항상 0 이상이라, 프리뷰용 기본값(-1)과 구분된다. */
    val achievementId: Long = -1L,
    /** 달성했지만 보상을 아직 수령하지 않은 상태 — true면 "받기" 버튼을 보여준다. */
    val isClaimable: Boolean = false,
)

private val TitleTextStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 22.sp, letterSpacing = (-0.35).sp)
private val RarityBadgeTextStyle = TextStyle(fontSize = 11.sp, lineHeight = 14.sp)
private val RewardTextStyle = TextStyle(fontSize = 11.sp, lineHeight = 14.sp)
private val DescriptionTextStyle = TextStyle(fontSize = 11.sp, lineHeight = 14.sp)
private val ProgressCountTextStyle = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 18.sp)

private val CardBorderColor = Neutral98
private val AchievedBadgeBackgroundColor = Color(0xFFEAEBEC)
private val ProgressCountColor = AchievementProgressCountColor
private val CharacterIconSize = 60.dp
private val IconImageSize = 44.dp

/**
 * 업적 목록 카드 한 장. Figma node `4714:40629`("List") 기준 —
 * 캐릭터 아이콘 + (타이틀·등급 배지·보상) + 설명 + 진행률 바(오른쪽에 진행 카운트).
 */
@Composable
fun AchievementListItem(
    item: AchievementUiModel,
    modifier: Modifier = Modifier,
    onClaimClick: (Long) -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (item.isAchieved) LiroutiTheme.colors.backgroundFill else LiroutiTheme.colors.backgroundDefault,
                RoundedCornerShape(6.dp),
            )
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
            when {
                !item.imageUrl.isNullOrBlank() -> AsyncImage(
                    model = item.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.size(IconImageSize),
                )
                item.iconRes != null -> Image(
                    painter = painterResource(id = item.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(IconImageSize),
                )
                else -> AchievementCharacterIcon(modifier = Modifier.size(width = 69.dp, height = 56.dp))
            }
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
                            color = LiroutiTheme.colors.labelSub,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        AchievementRarityBadge(rarity = item.rarity)
                        if (item.isAchieved) {
                            AchievedBadge()
                        }
                    }
                    if (item.isClaimable) {
                        ClaimRewardButton(onClick = { onClaimClick(item.achievementId) })
                    } else if (item.rewardText != null) {
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

/** 달성 완료 배지. Figma node `6008:31319`("Badge") 기준 — 등급 배지 옆에 붙는다. */
@Composable
private fun AchievedBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(AchievedBadgeBackgroundColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 2.dp),
    ) {
        Text(text = "달성", style = RarityBadgeTextStyle, color = LiroutiTheme.colors.labelInfo)
    }
}

/**
 * 달성했지만 보상을 아직 안 받은 업적에 보이는 "받기" 버튼. Figma 시안이 따로 없어 [AchievedBadge]와
 * 같은 크기·모양에 파란 배경만 입혀 "탭 가능한 액션"임을 구분했다.
 */
@Composable
private fun ClaimRewardButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(LiroutiTheme.colors.primaryNormal, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 7.dp, vertical = 2.dp),
    ) {
        Text(text = "받기", style = RarityBadgeTextStyle, color = LiroutiTheme.colors.labelReverse)
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
            AchievementListItem(
                item = AchievementUiModel(
                    title = "첫 방만들기",
                    rarity = AchievementRarity.Rare,
                    description = "모임방을 처음 만들어보세요",
                    progressLabel = "1/1",
                    progress = 1f,
                    isAchieved = true,
                ),
            )
        }
    }
}
