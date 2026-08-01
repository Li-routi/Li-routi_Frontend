package com.li_routi.feature.challenge.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.LiroutiBadge
import com.li_routi.core.designsystem.component.LiroutiBadgeColor
import com.li_routi.core.designsystem.component.LiroutiBadgeSize
import com.li_routi.core.designsystem.component.LiroutiBottomSheet
import com.li_routi.core.designsystem.component.LiroutiChevronLeftIcon
import com.li_routi.core.designsystem.component.LiroutiChevronRightIcon
import com.li_routi.core.designsystem.component.LiroutiLineTab
import com.li_routi.core.designsystem.component.LiroutiPrimaryButton
import com.li_routi.core.designsystem.component.LiroutiRoutineStatsRow
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.challenge.component.CertificationCard
import com.li_routi.feature.challenge.navigation.ChallengeDetailScreenActions
import com.li_routi.feature.challenge.vm.CertificationTab
import com.li_routi.feature.challenge.vm.CertificationUiModel
import com.li_routi.feature.challenge.vm.ChallengeDetailUiState

// 챌린지 대표 이미지 자리의 배경. Figma 목업 기준 옅은 블루 톤(디자인 시스템에 대응하는 시맨틱 컬러 없음).
private val HeroBg = Color(0xFFF3F6FF)

// Figma node: 2380:40108(참여 전) / 2372:49856(참여 후, 버튼 문구만 다름) / 2222:22836(더보기 바텀시트)
// "챌린지 찾아보기" 카드를 눌렀을 때 넘어오는 챌린지 상세 화면. 화면 전체가 스크롤된다(LazyColumn).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeDetailScreen(
    uiState: ChallengeDetailUiState,
    actions: ChallengeDetailScreenActions,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiState.isLoading) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(LiroutiTheme.colors.backgroundDefault),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = LiroutiTheme.colors.labelDefault)
        }
        return
    }

    // 더보기 바텀시트 노출 여부는 화면 로컬 UI 상태(서버/재사용 데이터가 아님).
    var showMoreSheet by remember { mutableStateOf(false) }
    // 인증 게시글별 더보기 바텀시트 대상. null이면 닫힘, 값이 있으면 그 게시글 기준으로
    // "내 인증 보기" 탭이면 "수정하기", "인증" 탭이면 "신고하기"를 보여준다.
    var moreSheetCertification by remember { mutableStateOf<CertificationUiModel?>(null) }
    // "수정하기"를 누르면 이 값이 채워지고, 화면 전체가 인증 수정 화면으로 전환된다.
    var editingCertification by remember { mutableStateOf<CertificationUiModel?>(null) }
    val listState = rememberLazyListState()

    editingCertification?.let { certification ->
        CertificationEditScreen(
            certification = certification,
            onClose = { editingCertification = null },
            onSubmit = { content ->
                actions.onEditCertificationSubmit(certification.id, content)
                editingCertification = null
            },
            modifier = modifier,
        )
        return
    }

    // 리스트 끝에 가까워지면 다음 페이지를 불러오는 간단한 무한 스크롤 트리거.
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleIndex >= layoutInfo.totalItemsCount - 3
        }
    }
    LaunchedEffect(shouldLoadMore, uiState.selectedTab, uiState.hasMoreCertifications) {
        if (shouldLoadMore && uiState.hasMoreCertifications) {
            actions.onLoadMore()
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundDefault),
    ) {
        item {
            ChallengeHeroSection(
                onBackClick = onBackClick,
                onMoreClick = { showMoreSheet = true },
            )
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                ChallengeInfoSection(uiState = uiState, onJoinClick = actions::onJoinClick)
                LiroutiRoutineStatsRow(
                    participants = uiState.participantCount.toString(),
                    activity = uiState.rewardCount.toString(),
                    posts = uiState.postCount.toString(),
                    activityLabel = "리워드",
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
            LiroutiLineTab(
                tabs = listOf("인증", "내 인증 보기"),
                selectedIndex = if (uiState.selectedTab == CertificationTab.All) 0 else 1,
                onTabSelected = { index ->
                    actions.onTabSelected(if (index == 0) CertificationTab.All else CertificationTab.Mine)
                },
                equalWidth = true,
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
        items(uiState.visibleCertifications, key = { it.id }) { certification ->
            CertificationCard(
                certification = certification,
                onMoreClick = { moreSheetCertification = certification },
                // "내 인증 보기" 응답엔 liked 상태가 없어 그 탭에서는 좋아요를 누를 수 없게 한다.
                onLikeClick = if (certification.isMine) null else { { actions.onLikeToggleClick(certification.id) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 20.dp),
            )
        }
        item { Spacer(modifier = Modifier.height(30.dp)) }
    }

    if (showMoreSheet) {
        // Figma node 3610:30072 ("더보기" 바텀시트, 챌린지 상세). LiroutiBottomSheet가 dim 배경 + 시트
        // 형태를 이미 처리해준다. title을 빈 문자열로 둬서 닫기(X) 버튼만 우측에 뜨도록 함(타이틀 텍스트 없음).
        LiroutiBottomSheet(
            onDismissRequest = { showMoreSheet = false },
            title = "",
        ) {
            MoreSheetActionRow(
                text = "챌린지 나가기",
                showChevron = true,
                onClick = {
                    actions.onLeaveChallengeClick()
                    showMoreSheet = false
                },
            )
        }
    }

    // 인증 게시글 더보기 바텀시트. 내 글이면 수정하기(Figma 3610:30078),
    // 타인 글이면 신고하기(Figma 3610:30075)를 보여준다. 둘 다 챌린지 나가기와 달리 화살표가 없다.
    moreSheetCertification?.let { certification ->
        LiroutiBottomSheet(
            onDismissRequest = { moreSheetCertification = null },
            title = "",
        ) {
            if (certification.isMine) {
                MoreSheetActionRow(
                    text = "수정하기",
                    onClick = {
                        editingCertification = certification
                        moreSheetCertification = null
                    },
                )
            } else {
                MoreSheetActionRow(
                    text = "신고하기",
                    onClick = {
                        actions.onReportCertificationClick(certification.id)
                        moreSheetCertification = null
                    },
                )
            }
        }
    }
}

@Composable
private fun MoreSheetActionRow(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showChevron: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = LiroutiTheme.typography.body1Medium,
            color = LiroutiTheme.colors.labelDefault,
        )
        if (showChevron) {
            LiroutiChevronRightIcon(
                modifier = Modifier.size(24.dp),
                color = LiroutiTheme.colors.labelDefault,
            )
        }
    }
}

@Composable
private fun ChallengeHeroSection(
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(HeroBg),
    ) {
        // 챌린지 대표 이미지(비-DS 이미지 자산) — 실제 에셋은 백엔드에서 제공, 지금은 자리만
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(84.dp)
                .background(LiroutiTheme.colors.backgroundSecondary, RoundedCornerShape(8.dp)),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            LiroutiChevronLeftIcon(
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = onBackClick),
                color = LiroutiTheme.colors.labelDefault,
            )
            Image(
                painter = painterResource(id = R.drawable.overflow_menu__vertical),
                contentDescription = "더보기",
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = onMoreClick),
                colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelDefault),
            )
        }
    }
}

@Composable
private fun ChallengeInfoSection(
    uiState: ChallengeDetailUiState,
    onJoinClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            LiroutiBadge(text = uiState.badge, color = LiroutiBadgeColor.Blue, size = LiroutiBadgeSize.XSmall)
            Text(
                text = uiState.title,
                style = LiroutiTheme.typography.body1SemiBold,
                color = LiroutiTheme.colors.labelDefault,
            )
            Text(
                text = uiState.description,
                style = LiroutiTheme.typography.captionRegular,
                color = LiroutiTheme.colors.labelDefault,
            )
        }

        // "참여하기" 탭 시 참여 상태로 바뀌고 서버에 참여 신호를 보낸다(ViewModel에서 처리, 이번 범위는 로컬 상태만).
        // 참여 후 "인증하기"는 별도 인증 업로드 플로우로 연결될 예정 — 이번 범위 제외. 버튼 색은 참여 여부와
        // 무관하게 항상 파란색으로 유지하고(Figma 두 상태 모두 동일), 클릭만 참여 전에만 동작하도록 막는다.
        LiroutiPrimaryButton(
            text = if (uiState.isJoined) "인증하기" else "참여하기",
            onClick = { if (!uiState.isJoined) onJoinClick() },
        )
    }
}

private object PreviewChallengeDetailScreenActions : ChallengeDetailScreenActions {
    override fun onJoinClick() = Unit
    override fun onTabSelected(tab: CertificationTab) = Unit
    override fun onLoadMore() = Unit
    override fun onLeaveChallengeClick() = Unit
    override fun onEditCertificationSubmit(certificationId: Long, content: String) = Unit
    override fun onReportCertificationClick(certificationId: Long) = Unit
    override fun onLikeToggleClick(certificationId: Long) = Unit
}

private val PreviewCertifications = List(4) { index ->
    CertificationUiModel(
        id = index.toLong(),
        authorName = "민지",
        content = "물 마시기 1일차 입니다~ 다들 열심히 하고 있지?",
        imageUrl = "",
        timeLabel = "9시간 전",
        likeCount = 1,
        liked = false,
        isMine = false,
    )
}

private val PreviewChallengeDetailUiState = ChallengeDetailUiState(
    challengeId = 1L,
    isLoading = false,
    title = "우유 한잔 마시기",
    badge = "매일 루틴",
    description = "매일 우유를 마시며 건강 관리를 해요",
    participantCount = 300,
    rewardCount = 14000,
    postCount = 80,
    allCertifications = PreviewCertifications,
    allHasNext = false,
)

@Preview(showBackground = true, heightDp = 900, name = "1. 참여 전")
@Composable
private fun ChallengeDetailScreenNotJoinedPreview() {
    LiroutiFrontendTheme {
        ChallengeDetailScreen(
            uiState = PreviewChallengeDetailUiState,
            actions = PreviewChallengeDetailScreenActions,
            onBackClick = {},
        )
    }
}

@Preview(showBackground = true, heightDp = 900, name = "2. 참여 후")
@Composable
private fun ChallengeDetailScreenJoinedPreview() {
    LiroutiFrontendTheme {
        ChallengeDetailScreen(
            uiState = PreviewChallengeDetailUiState.copy(isJoined = true),
            actions = PreviewChallengeDetailScreenActions,
            onBackClick = {},
        )
    }
}

@Preview(showBackground = true, heightDp = 900, name = "3. 로딩")
@Composable
private fun ChallengeDetailScreenLoadingPreview() {
    LiroutiFrontendTheme {
        ChallengeDetailScreen(
            uiState = ChallengeDetailUiState(challengeId = 1L, isLoading = true),
            actions = PreviewChallengeDetailScreenActions,
            onBackClick = {},
        )
    }
}
