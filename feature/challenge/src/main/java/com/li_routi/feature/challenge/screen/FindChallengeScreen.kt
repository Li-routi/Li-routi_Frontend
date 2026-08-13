package com.li_routi.feature.challenge.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.LiroutiChevronLeftIcon
import com.li_routi.core.designsystem.component.LiroutiLabel
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.component.LiroutiSearchField
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.core.domain.challenge.ChallengeCategory
import com.li_routi.core.common.ui.nav.AppBottomNavBar
import com.li_routi.core.common.ui.nav.AppBottomTab
import com.li_routi.feature.challenge.component.ChallengeCard
import com.li_routi.feature.challenge.component.ChallengeCardUiModel
import com.li_routi.feature.challenge.navigation.FindChallengeScreenActions
import com.li_routi.feature.challenge.vm.FindChallengeUiState

// 필터 칩 표시 라벨 <-> 서버 category 쿼리 매핑 ("전체" 칩은 category 생략).
private val filterOptions: List<Pair<String, ChallengeCategory?>> = listOf(
    "전체" to null,
    "건강" to ChallengeCategory.HEALTH,
    "운동" to ChallengeCategory.EXERCISE,
    "공부" to ChallengeCategory.STUDY,
    "생활" to ChallengeCategory.LIFE,
    "취미" to ChallengeCategory.HOBBY,
)

// Figma node: 2380:40517 ("챌린지 찾아보기")
// "챌린지" 화면(디폴트/빈 상태)의 "새 챌린지 찾아보기" 버튼에서 넘어오는 화면.
// 카드를 누르면 상세 화면(ChallengeDetailScreen)으로 이동한다.
// 하단 GNB는 챌린지 상세 화면에는 노출되지 않고 이 화면까지만 보인다.
@Composable
fun FindChallengeScreen(
    uiState: FindChallengeUiState,
    actions: FindChallengeScreenActions,
    onBackClick: () -> Unit,
    onChallengeClick: (challengeId: Long) -> Unit,
    onTabSelected: (AppBottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    // 목록 끝에 가까워지면 다음 페이지를 불러오는 무한 스크롤 트리거. ChallengeDetailScreen과 동일한 패턴.
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleIndex >= layoutInfo.totalItemsCount - 3
        }
    }
    LaunchedEffect(shouldLoadMore, uiState.hasNext) {
        if (shouldLoadMore && uiState.hasNext) {
            actions.onLoadMore()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {

            // ---------- 상단 네비게이션 (뒤로가기 + "챌린지 찾아보기") ----------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LiroutiTheme.colors.backgroundDefault)
                    .statusBarsPadding()
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                LiroutiChevronLeftIcon(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp)
                        .size(24.dp)
                        .clickable(onClick = onBackClick),
                    color = LiroutiTheme.colors.labelDefault,
                )
                Text(
                    text = "챌린지 찾아보기",
                    style = LiroutiTheme.typography.heading2SemiBold,
                    color = LiroutiTheme.colors.labelDefault,
                )
            }

            // 검색바/필터 칩은 스크롤 영역 밖에 고정하고, 카드 리스트만 그 아래에서 스크롤되게 한다.
            // 필터 칩 밑 여백도 리스트의 contentPadding이 아니라 여기(고정 영역)에 둬야, 스크롤해도
            // 이 여백이 사라지지 않고 필터 칩과 함께 위에 그대로 남아 있는다.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LiroutiTheme.colors.backgroundSecondary)
                    .padding(horizontal = 16.dp)
                    .padding(top = 25.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // ---------- 검색바 (입력마다 디바운스되어 서버 keyword로 반영됨) ----------
                LiroutiSearchField(
                    value = uiState.searchQuery,
                    onValueChange = actions::onSearchQueryChanged,
                )

                // ---------- 필터 칩 ----------
                // (Figma: Dim > Filter, node 2380:40523)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    filterOptions.forEach { (label, category) ->
                        LiroutiLabel(
                            text = label,
                            selected = category == uiState.selectedCategory,
                            onClick = { actions.onCategorySelected(category) },
                        )
                    }
                }
            }

            // ---------- 챌린지 카드 리스트 (백엔드 GET /api/challenges 연동) ----------
            // (Figma node 2380:40530 / 2380:40558).
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LiroutiTheme.colors.backgroundSecondary),
            ) {
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(color = LiroutiTheme.colors.labelDefault)
                        }
                    }

                    uiState.errorMessage != null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = uiState.errorMessage,
                                style = LiroutiTheme.typography.captionRegular,
                                color = LiroutiTheme.colors.labelDefault,
                            )
                            Text(
                                text = "다시 시도",
                                style = LiroutiTheme.typography.captionRegular,
                                color = LiroutiTheme.colors.labelDefault,
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .clickable(onClick = actions::onRetryClick),
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 50.dp),
                        ) {
                            items(uiState.challenges, key = { it.id }) { challenge ->
                                ChallengeCard(
                                    challenge = challenge,
                                    onClick = { onChallengeClick(challenge.id) },
                                )
                            }
                            if (uiState.isLoadingMore) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        CircularProgressIndicator(color = LiroutiTheme.colors.labelDefault)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 홈/그룹 루틴/챌린지/마이 4탭 하단 GNB.
        AppBottomNavBar(selectedTab = AppBottomTab.Challenge, onTabSelected = onTabSelected)
    }
}

private object PreviewFindChallengeScreenActions : FindChallengeScreenActions {
    override fun onCategorySelected(category: ChallengeCategory?) = Unit
    override fun onSearchQueryChanged(query: String) = Unit
    override fun onRetryClick() = Unit
    override fun onLoadMore() = Unit
}

private val PreviewChallenges = listOf(
    ChallengeCardUiModel(
        id = 1L,
        title = "매일 우유 한 잔",
        tagLabel = "건강",
        description = "매일 우유를 마시며 건강 관리를 해요",
        badge = "매일 루틴",
        participantCount = 300,
        rewardCount = 0,
        postCount = 80,
    ),
    ChallengeCardUiModel(
        id = 2L,
        title = "매일 사과 한 개",
        tagLabel = "건강",
        description = "매일 사과를 먹으며 건강 관리를 해요",
        badge = "매일 루틴",
        participantCount = 3600,
        rewardCount = 0,
        postCount = 123,
    ),
)

@Preview(showBackground = true, heightDp = 900, name = "1. 목록")
@Composable
private fun FindChallengeScreenPreview() {
    LiroutiFrontendTheme {
        FindChallengeScreen(
            uiState = FindChallengeUiState(isLoading = false, challenges = PreviewChallenges),
            actions = PreviewFindChallengeScreenActions,
            onBackClick = {},
            onChallengeClick = {},
            onTabSelected = {},
        )
    }
}

@Preview(showBackground = true, heightDp = 900, name = "2. 로딩")
@Composable
private fun FindChallengeScreenLoadingPreview() {
    LiroutiFrontendTheme {
        FindChallengeScreen(
            uiState = FindChallengeUiState(isLoading = true),
            actions = PreviewFindChallengeScreenActions,
            onBackClick = {},
            onChallengeClick = {},
            onTabSelected = {},
        )
    }
}

@Preview(showBackground = true, heightDp = 900, name = "3. 에러")
@Composable
private fun FindChallengeScreenErrorPreview() {
    LiroutiFrontendTheme {
        FindChallengeScreen(
            uiState = FindChallengeUiState(isLoading = false, errorMessage = "목록을 불러오지 못했어요"),
            actions = PreviewFindChallengeScreenActions,
            onBackClick = {},
            onChallengeClick = {},
            onTabSelected = {},
        )
    }
}
