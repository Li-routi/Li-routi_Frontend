package com.li_routi.feature.mypage.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

data class AchievementBadgeUiModel(
    val id: Long,
    val title: String,
    val rarity: AchievementRarity,
    @param:DrawableRes val iconRes: Int? = null,
    /** 서버 제공 뱃지 이미지. 있으면 [iconRes]보다 우선한다 — 같은 업적의 목록 아이콘과 항상 같은 이미지를 쓴다. */
    val imageUrl: String? = null,
)

private val CellHeight = 120.dp
private val IconFrameWidth = 90.dp
private val IconFrameHeight = 78.dp
private val MedalImageWidth = 72.dp
private val MedalImageHeight = 69.dp
private val BadgeIconSize = 48.dp
private val EquippedLabelTextStyle = TextStyle(fontSize = 13.sp, lineHeight = 16.sp, letterSpacing = (-0.325).sp)
private val EquippedBackgroundColor = Color(0xFFF2F8FF)
private val EquippedBorderWidth = 2.dp

private const val ColumnCount = 3

/**
 * "달성" 탭의 획득 배지 그리드. Figma node `6008:31728`("달성")/`6008:36133`("달성 업적 장착 시") 기준 —
 * 3열 그리드, 셀 간 여백 6dp, 각 셀 120dp 높이. 배지 획득 개수만큼 채우고 나머지는 빈 셀로 둔다.
 *
 * 마지막 줄이 3개를 못 채우면 `null`로 패딩해서 빈 셀(배경만 있고 아이콘 없음, Figma 원본도 동일)을
 * 채운다 — 안 그러면 `weight(1f)`가 남은 셀에 다 몰려서 폭이 어긋난다.
 *
 * 배지를 탭하면 [onBadgeClick]으로 알리고, [equippedBadgeId]와 일치하는 배지에 파란 테두리 +
 * "장착 중" 표시를 보여준다 — 장착 상태를 서버에 저장하는 API가 아직 없어 화면 로컬 상태로만 관리한다.
 */
@Composable
fun AchievementBadgeGrid(
    badges: List<AchievementBadgeUiModel>,
    modifier: Modifier = Modifier,
    equippedBadgeId: Long? = null,
    onBadgeClick: (AchievementBadgeUiModel) -> Unit = {},
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        badges.chunked(ColumnCount).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                val paddedRow = row + List(ColumnCount - row.size) { null }
                paddedRow.forEach { badge ->
                    AchievementBadgeCell(
                        badge = badge,
                        isEquipped = badge != null && badge.id == equippedBadgeId,
                        onClick = { badge?.let(onBadgeClick) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementBadgeCell(
    badge: AchievementBadgeUiModel?,
    isEquipped: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .height(CellHeight)
            .background(
                if (isEquipped) EquippedBackgroundColor else LiroutiTheme.colors.backgroundFill,
                RoundedCornerShape(6.dp),
            )
            .then(
                if (isEquipped) {
                    Modifier.border(EquippedBorderWidth, LiroutiTheme.colors.primaryNormal, RoundedCornerShape(6.dp))
                } else {
                    Modifier
                },
            )
            .then(if (badge != null) Modifier.clickable(onClick = onClick) else Modifier)
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
                // 목록 카드(AchievementListItem)와 소스 우선순위(서버 이미지 > 로컬 매핑 > 기본
                // 캐릭터)를 똑같이 맞춰서, 같은 업적이면 항상 같은 이미지가 보이게 한다.
                AchievementFallbackImage(
                    imageUrl = badge.imageUrl,
                    iconRes = badge.iconRes,
                    contentDescription = badge.title,
                    modifier = Modifier.size(BadgeIconSize),
                    fallback = {
                        AchievementCharacterIcon(modifier = Modifier.size(width = MedalImageWidth, height = MedalImageHeight))
                    },
                )
            }
            if (isEquipped) {
                Text(text = "장착 중", style = EquippedLabelTextStyle, color = LiroutiTheme.colors.primaryNormal)
            }
        }
    }
}

private val SampleBadges = listOf(
    AchievementBadgeUiModel(1, "불꽃 연속", AchievementRarity.Rare),
    AchievementBadgeUiModel(2, "친구 부자", AchievementRarity.Epic),
    AchievementBadgeUiModel(3, "소셜 스타", AchievementRarity.Unique),
    AchievementBadgeUiModel(4, "꾸준한 루티너", AchievementRarity.Rare),
)

@Preview(showBackground = true)
@Composable
private fun AchievementBadgeGridPreview() {
    LiroutiFrontendTheme {
        AchievementBadgeGrid(badges = SampleBadges, equippedBadgeId = 1L, modifier = Modifier.padding(16.dp))
    }
}
