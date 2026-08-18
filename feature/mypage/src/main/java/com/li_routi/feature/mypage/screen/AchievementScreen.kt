package com.li_routi.feature.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.LiroutiLabel
import com.li_routi.core.designsystem.component.LiroutiLineTab
import com.li_routi.core.designsystem.component.LiroutiToast
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.core.domain.achievement.WaveRoutineStatus
import com.li_routi.core.domain.routine.CreatedRoutine
import com.li_routi.feature.mypage.component.AchievementBadgeGrid
import com.li_routi.feature.mypage.component.AchievementBadgeUiModel
import com.li_routi.feature.mypage.component.AchievementListItem
import com.li_routi.feature.mypage.component.AchievementRarity
import com.li_routi.feature.mypage.component.AchievementUiModel
import com.li_routi.feature.mypage.component.EditProfileTopBar

/** 업적 상태 탭("전체"/"진행중"/"달성"). Figma node `4714:40612`("Tab") 기준. */
enum class AchievementStatusTab(val label: String) {
    All("전체"),
    InProgress("진행중"),
    Achieved("달성"),
}

private val CountTextStyle = TextStyle(fontSize = 11.sp, lineHeight = 14.sp)

/**
 * 업적 화면. Figma node `4201:34400`(전체/진행중)·`4869:37738`(달성) 기준 — 마이페이지 "업적" 메뉴로
 * 진입한다.
 *
 * 상태 탭(전체/진행중/달성) + 등급 필터 칩(전체/레어/에픽/유니크)으로 구성되고, "달성" 탭만 진행률 카드
 * 목록 대신 3열 배지 그리드를 보여준다. 등급 필터 칩은 "전체"/"진행중" 탭에서만 보이고, "달성" 탭은
 * 필터 없이 획득한 배지를 전부 보여준다(Figma node `6008:31728` 기준).
 */
@Composable
fun AchievementScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    achievements: List<AchievementUiModel> = SampleAchievements,
    achievedBadges: List<AchievementBadgeUiModel> = SampleAchievedBadges,
    isLoading: Boolean = false,
    isError: Boolean = false,
    onClaimClick: (Long) -> Unit = {},
    claimMessage: String? = null,
    onClaimMessageDismissed: () -> Unit = {},
    /** "파도타기"(연속 기록) 업적이 추적 중인 루틴. null이면 아직 못 불러온 상태(카드 자체를 숨김) */
    waveRoutineStatus: WaveRoutineStatus? = null,
    isWaveRoutinePickerVisible: Boolean = false,
    myRoutines: List<CreatedRoutine> = emptyList(),
    onWaveRoutinePickerOpen: () -> Unit = {},
    onWaveRoutinePickerDismiss: () -> Unit = {},
    onWaveRoutineChosen: (Long) -> Unit = {},
) {
    var selectedTab by remember { mutableStateOf(AchievementStatusTab.All) }
    var selectedRarity by remember { mutableStateOf<AchievementRarity?>(null) }
    var equippedBadgeId by remember { mutableStateOf<Long?>(null) }

    val filteredAchievements = achievements.filter { item ->
        (selectedRarity == null || item.rarity == selectedRarity) &&
            (selectedTab != AchievementStatusTab.InProgress || item.isInProgress)
    }
    val filteredBadges = achievedBadges

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LiroutiTheme.colors.backgroundDefault),
        ) {
            EditProfileTopBar(title = "업적", onBackClick = onBackClick)
            LiroutiLineTab(
                tabs = AchievementStatusTab.entries.map { it.label },
                selectedIndex = selectedTab.ordinal,
                onTabSelected = { index -> selectedTab = AchievementStatusTab.entries[index] },
                equalWidth = true,
            )
            if (waveRoutineStatus != null) {
                WaveRoutineStatusRow(
                    status = waveRoutineStatus,
                    onClick = onWaveRoutinePickerOpen,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                )
            }
            if (isLoading) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = LiroutiTheme.colors.primaryNormal)
                }
            } else if (isError) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    AchievementEmptyState(message = "업적 정보를 불러오지 못했어요")
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    if (selectedTab != AchievementStatusTab.Achieved) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            LiroutiLabel(text = "전체", selected = selectedRarity == null, onClick = { selectedRarity = null })
                            AchievementRarity.entries.forEach { rarity ->
                                LiroutiLabel(
                                    text = rarity.label,
                                    selected = selectedRarity == rarity,
                                    onClick = { selectedRarity = rarity },
                                )
                            }
                        }
                    }
                    if (selectedTab == AchievementStatusTab.Achieved) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "총 ${filteredBadges.size}개 달성",
                                style = CountTextStyle,
                                color = LiroutiTheme.colors.labelSub,
                            )
                            AchievementBadgeGrid(
                                badges = filteredBadges,
                                equippedBadgeId = equippedBadgeId,
                                onBadgeClick = { badge -> equippedBadgeId = badge.id },
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "총 ${filteredAchievements.size}개의 업적이 있어요",
                                style = CountTextStyle,
                                color = LiroutiTheme.colors.labelSub,
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                filteredAchievements.forEach { item ->
                                    AchievementListItem(item = item, onClaimClick = onClaimClick)
                                }
                            }
                        }
                    }
                }
            }
        }
        if (claimMessage != null) {
            LiroutiToast(
                message = claimMessage,
                onCloseClick = onClaimMessageDismissed,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp),
            )
        }
    }

    if (isWaveRoutinePickerVisible) {
        WaveRoutinePickerSheet(
            routines = myRoutines,
            selectedRoutineId = waveRoutineStatus?.memberRoutineId,
            onRoutineClick = onWaveRoutineChosen,
            onDismissRequest = onWaveRoutinePickerDismiss,
        )
    }
}

/**
 * "파도타기"(연속 기록) 업적 상태 카드. Figma 시안이 따로 없어 기존 라벨 칩과 톤을 맞춘 최소
 * 구현이다 — 지정한 루틴이 있으면 이름+현재/목표 연속 기록을, 없으면 선택을 유도하는 문구를 보여준다.
 */
@Composable
private fun WaveRoutineStatusRow(
    status: WaveRoutineStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(LiroutiTheme.colors.backgroundFill)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (status.isSelected) {
                "🌊 ${status.routineName} · ${status.currentStreak}/${status.targetStreak}일 연속"
            } else {
                "🌊 연속 기록을 추적할 루틴을 선택해보세요"
            },
            style = LiroutiTheme.typography.body2LongMedium,
            color = LiroutiTheme.colors.labelStrong,
        )
        Text(
            text = if (status.isSelected) "변경" else "선택",
            style = LiroutiTheme.typography.body3Bold,
            color = LiroutiTheme.colors.primaryNormal,
        )
    }
}

/** 연속 기록을 추적할 루틴을 고르는 시트. 별도 Figma 없이 단순 목록으로 구현했다. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WaveRoutinePickerSheet(
    routines: List<CreatedRoutine>,
    selectedRoutineId: Long?,
    onRoutineClick: (Long) -> Unit,
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = LiroutiTheme.colors.backgroundDefault,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "연속 기록 추적할 루틴 선택",
                style = LiroutiTheme.typography.heading2,
                color = LiroutiTheme.colors.labelStrong,
                modifier = Modifier.padding(bottom = 12.dp),
            )
            if (routines.isEmpty()) {
                Text(
                    text = "선택할 수 있는 루틴이 없어요",
                    style = LiroutiTheme.typography.body2LongRegular,
                    color = LiroutiTheme.colors.labelInfo,
                    modifier = Modifier.padding(vertical = 20.dp),
                )
            }
            routines.forEach { routine ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onRoutineClick(routine.routineId) }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = routine.name,
                        style = LiroutiTheme.typography.body2LongMedium,
                        color = LiroutiTheme.colors.labelStrong,
                    )
                    if (routine.routineId == selectedRoutineId) {
                        Text(
                            text = "선택됨",
                            style = LiroutiTheme.typography.body3Bold,
                            color = LiroutiTheme.colors.primaryNormal,
                        )
                    }
                }
            }
        }
    }
}


/** 업적 조회에 실패했을 때 화면 가운데에 보여주는 상태. */
@Composable
private fun AchievementEmptyState(modifier: Modifier = Modifier, message: String) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = R.drawable.warning),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
        )
        Text(
            text = message,
            style = LiroutiTheme.typography.body2LongMedium,
            color = LiroutiTheme.colors.labelInfo,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

private val SampleAchievements = listOf(
    AchievementUiModel(
        title = "불꽃연속",
        rarity = AchievementRarity.Rare,
        description = "30일 연속 루틴을 달성하세요",
        progressLabel = "27/30",
        progress = 27f / 30f,
        rewardText = "+50토파즈",
        isInProgress = true,
    ),
    AchievementUiModel(
        title = "불꽃연속",
        rarity = AchievementRarity.Epic,
        description = "30일 연속 루틴을 달성하세요",
        progressLabel = "27/30",
        progress = 27f / 30f,
        isInProgress = true,
    ),
    AchievementUiModel(
        title = "친구 부자",
        rarity = AchievementRarity.Unique,
        description = "친구 7명을 초대하세요",
        progressLabel = "5/7",
        progress = 5f / 7f,
        isInProgress = true,
    ),
)

private val SampleAchievedBadges = listOf(
    AchievementBadgeUiModel(1, "불꽃 연속", AchievementRarity.Rare),
    AchievementBadgeUiModel(2, "친구 부자", AchievementRarity.Unique),
    AchievementBadgeUiModel(3, "소셜 스타", AchievementRarity.Epic),
    AchievementBadgeUiModel(4, "꾸준한 루티너", AchievementRarity.Rare),
)

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun AchievementScreenPreview() {
    LiroutiFrontendTheme {
        AchievementScreen(achievements = SampleAchievements, onBackClick = {})
    }
}
