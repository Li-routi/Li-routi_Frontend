package com.li_routi.feature.challenge.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ri_routi.LabelButton
import com.example.ri_routi.LiroutiButtonStyle
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.LiroutiLabel
import com.li_routi.core.designsystem.component.LiroutiRoutineSimpleCard
import com.li_routi.core.designsystem.component.LiroutiSearchField
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.core.common.ui.nav.AppBottomNavBar
import com.li_routi.core.common.ui.nav.AppBottomTab
import com.li_routi.feature.challenge.vm.ChallengeUiState
import com.li_routi.feature.challenge.vm.RoutineUiModel

private val filterOptions = listOf("전체", "건강", "운동", "공부", "생활", "취미")

// Figma node: 2380:37243 (참여 중인 챌린지 있음) / 2187:37494 (참여 중인 챌린지 없음, 디폴트 화면)
// routines가 비어있으면 2187:37494(빈 상태)를, 있으면 2380:37243(리스트)를 보여줍니다.
@Composable
fun ChallengeScreen(
    uiState: ChallengeUiState,
    onFindNewChallengeClick: () -> Unit,
    onChallengeClick: (challengeId: Long) -> Unit,
    onTabSelected: (AppBottomTab) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {

            // ---------- 상단 네비게이션 (챌린지 + 추가 버튼) ----------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LiroutiTheme.colors.backgroundDefault)
                    .statusBarsPadding()
                    .height(48.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "챌린지",
                    style = LiroutiTheme.typography.heading2SemiBold,
                    color = LiroutiTheme.colors.labelDefault,
                )
                Image(
                    painter = painterResource(id = R.drawable.add__alt),
                    contentDescription = "새 챌린지 추가",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                        .size(20.dp)
                        .clickable(onClick = onFindNewChallengeClick),
                )
            }

            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = LiroutiTheme.colors.labelDefault)
                    }
                }
                uiState.routines.isEmpty() -> {
                    EmptyChallengeContent(onFindNewChallengeClick = onFindNewChallengeClick)
                }
                else -> {
                    ChallengeListContent(routines = uiState.routines, onChallengeClick = onChallengeClick)
                }
            }
        }

        // 홈/그룹 루틴/챌린지/마이 4탭 하단 GNB. 챌린지 상세 화면에는 노출되지 않는다.
        AppBottomNavBar(selectedTab = AppBottomTab.Challenge, onTabSelected = onTabSelected)
    }
}

// ============================================================
// 2번 디자인 (Figma node: 2187:37494) - 참여 중인 챌린지가 없을 때 디폴트로 보이는 화면
// ============================================================
@Composable
private fun EmptyChallengeContent(onFindNewChallengeClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundSecondary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.warning),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                )
                Text(
                    text = "참여 중인 챌린지가 없어요",
                    style = LiroutiTheme.typography.body2Regular,
                    color = LiroutiTheme.colors.labelInfo,
                    modifier = Modifier.width(280.dp),
                    textAlign = TextAlign.Center,
                )
            }

            LabelButton(
                text = "새 챌린지 찾아보기",
                onClick = onFindNewChallengeClick,
                style = LiroutiButtonStyle.Quaternary,
            )
        }
    }
}

// ============================================================
// 1번 디자인 (Figma node: 2380:37243) - 참여 중인 챌린지가 있을 때 (검색 + 필터 + 리스트)
// ============================================================
@Composable
private fun ChallengeListContent(routines: List<RoutineUiModel>, onChallengeClick: (challengeId: Long) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf("전체") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundSecondary)
            .padding(horizontal = 16.dp)
            .padding(top = 25.dp, bottom = 50.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {

        // ---------- 검색바 ----------
        LiroutiSearchField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
        )

        // ---------- 필터 칩 ----------
        // (Figma: Dim > Filter, node 2187:22130)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            filterOptions.forEach { label ->
                LiroutiLabel(
                    text = label,
                    selected = label == activeFilter,
                    onClick = { activeFilter = label },
                )
            }
        }

        // ---------- 루틴 리스트 ----------
        // (Figma: List > Property1=routine_simple, node 2398:11076)
        // 카테고리가 여러 개인 경우 콤마로 이어붙임 (예: "건강, 취미")
        val filteredRoutines = remember(routines, activeFilter) {
            if (activeFilter == "전체") routines else routines.filter { activeFilter in it.categories }
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            filteredRoutines.forEach { routine ->
                LiroutiRoutineSimpleCard(
                    title = routine.title,
                    subtitle = "${routine.categories.joinToString(", ")} | ${routine.description}",
                    badgeText = routine.badge,
                    onClick = { onChallengeClick(routine.id) },
                )
            }
        }
    }
}
