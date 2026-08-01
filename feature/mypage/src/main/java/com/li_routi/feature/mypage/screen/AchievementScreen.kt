package com.li_routi.feature.mypage.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.LiroutiDivider
import com.li_routi.core.designsystem.component.LiroutiDividerThickness
import com.li_routi.core.designsystem.component.LiroutiLabel
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.mypage.component.AchievementBarcodeIcon
import com.li_routi.feature.mypage.component.AchievementCouponIcon
import com.li_routi.feature.mypage.component.AchievementListItem
import com.li_routi.feature.mypage.component.AchievementStatUiModel
import com.li_routi.feature.mypage.component.AchievementSummaryCard
import com.li_routi.feature.mypage.component.AchievementUiModel
import com.li_routi.feature.mypage.component.AchievementWalletIcon
import com.li_routi.feature.mypage.component.EditProfileTopBar

/** 업적 필터("전체"/"시작"/"달성"/"스페셜"). Figma node `205:18265`("Filter") 기준. */
enum class AchievementFilter(val label: String) {
    All("전체"),
    InProgress("시작"),
    Achieved("달성"),
    Special("스페셜"),
}

/**
 * 업적 화면. Figma node `205:18214`("logged in") 기준 — 마이페이지 "업적" 메뉴로 진입한다.
 *
 * 요약 카드(대표 배지/캐릭터/통계) + 필터 칩 + 진행 중 업적 목록으로 구성된다.
 */
@Composable
fun AchievementScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    achievements: List<AchievementUiModel> = SampleAchievements,
    acquiredCount: String = "8/36",
    inProgressCount: String = "12",
    specialCount: String = "1/4",
) {
    var selectedFilter by remember { mutableStateOf(AchievementFilter.InProgress) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundSecondary),
    ) {
        EditProfileTopBar(
            title = "업적",
            onBackClick = onBackClick,
            modifier = Modifier.background(LiroutiTheme.colors.backgroundDefault),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            Column(modifier = Modifier.padding(top = 25.dp, start = 16.dp, end = 16.dp)) {
                AchievementSummaryCard(
                    representativeBadges = listOf("대표 배지", "대표 배지"),
                    highlightText = "첫 인증!",
                    stats = listOf(
                        AchievementStatUiModel("획득", acquiredCount) { AchievementWalletIcon() },
                        AchievementStatUiModel("진행 중", inProgressCount) { AchievementCouponIcon() },
                        AchievementStatUiModel("스페셜", specialCount) { AchievementBarcodeIcon() },
                    ),
                )
            }
            LiroutiDivider(thickness = LiroutiDividerThickness.ExtraBold, color = LiroutiTheme.colors.borderSub)
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AchievementFilter.entries.forEach { filter ->
                        LiroutiLabel(
                            text = filter.label,
                            selected = filter == selectedFilter,
                            onClick = { selectedFilter = filter },
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(6.dp))
                        .background(LiroutiTheme.colors.backgroundDefault, RoundedCornerShape(6.dp))
                        .padding(horizontal = 20.dp),
                ) {
                    achievements.forEachIndexed { index, item ->
                        if (index != 0) {
                            LiroutiDivider(color = LiroutiTheme.colors.borderSub)
                        }
                        AchievementListItem(item = item)
                    }
                }
            }
        }
    }
}

private val SampleAchievements = listOf(
    AchievementUiModel(title = "친구 부자", rewardBadges = listOf("50코인", "배지"), progressLabel = "5/7", progress = 5f / 7f),
    AchievementUiModel(title = "꾸준한 루티너", rewardBadges = emptyList(), progressLabel = "50코인", progress = 0.6f),
    AchievementUiModel(title = "소셜 스타", rewardBadges = emptyList(), progressLabel = "50코인", progress = 0.6f),
)

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun AchievementScreenPreview() {
    LiroutiFrontendTheme {
        AchievementScreen(achievements = SampleAchievements, onBackClick = {})
    }
}
