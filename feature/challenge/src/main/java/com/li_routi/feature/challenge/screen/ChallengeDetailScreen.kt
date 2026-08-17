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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.LiroutiBadge
import com.li_routi.core.designsystem.component.LiroutiBadgeColor
import com.li_routi.core.designsystem.component.LiroutiBadgeSize
import com.li_routi.core.designsystem.component.LiroutiBottomSheet
import com.li_routi.core.designsystem.component.LiroutiChevronDownIcon
import com.li_routi.core.designsystem.component.LiroutiChevronLeftIcon
import com.li_routi.core.designsystem.component.LiroutiLineTab
import com.li_routi.core.designsystem.component.LiroutiPrimaryButton
import com.li_routi.core.designsystem.component.LiroutiPullToRefreshBox
import com.li_routi.core.designsystem.component.LiroutiRoutineStatsRow
import com.li_routi.core.designsystem.component.LiroutiScrollToTopButton
import com.li_routi.core.designsystem.component.LiroutiToast
import com.li_routi.core.designsystem.foundation.color.ChallengeHeroBackground
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.core.domain.challenge.ReportType
import com.li_routi.core.domain.challenge.VerificationSort
import com.li_routi.feature.challenge.component.CertificationCard
import com.li_routi.feature.challenge.navigation.ChallengeDetailScreenActions
import com.li_routi.feature.challenge.vm.CertificationTab
import com.li_routi.feature.challenge.vm.CertificationUiModel
import com.li_routi.feature.challenge.vm.ChallengeDetailUiState
import com.li_routi.feature.challenge.vm.toDisplayLabel
import kotlinx.coroutines.launch

// 챌린지 대표 이미지 자리의 배경. Figma 목업 기준 옅은 블루 톤(디자인 시스템에 대응하는 시맨틱 컬러 없음).
private val HeroBg = ChallengeHeroBackground

// Figma node: 2380:40108(참여 전) / 2372:49856(참여 후, 버튼 문구만 다름) / 2222:22836(더보기 바텀시트)
// "챌린지 찾아보기" 카드를 눌렀을 때 넘어오는 챌린지 상세 화면. 헤더(뒤로가기/더보기)는 고정, 나머지만 스크롤된다(LazyColumn).
//
// "인증하기"(신규 촬영)는 이 화면 로컬이 아니라 `app` 모듈이 소유한 공유 인증 플로우(개인/그룹/챌린지
// 공용)로 넘어간다 — [onStartVerification]으로 위임한다. 반면 "인증 수정"(기존 게시글 수정)은 챌린지
// 전용 기능이라 계속 이 화면 로컬 [editingCertification] 상태로 처리한다.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeDetailScreen(
    uiState: ChallengeDetailUiState,
    actions: ChallengeDetailScreenActions,
    onBackClick: () -> Unit,
    onStartVerification: () -> Unit,
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
    // "신고하기"를 누르면 이 값이 채워지고, 화면 전체가 신고 사유 선택 화면으로 전환된다.
    var reportingCertification by remember { mutableStateOf<CertificationUiModel?>(null) }
    // 정렬 바텀시트 노출 여부만 화면 로컬 상태다. 실제 정렬 기준(uiState.selectedSort)은 바뀔 때마다
    // 목록을 다시 불러와야 해서 ViewModel이 소유한다.
    var showSortSheet by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    editingCertification?.let { certification ->
        // uiState.editedCertificationId는 수정 성공 시에만 채워지는 명시적 신호라("제출 중 아님 &&
        // 에러 없음"이라는 이중 부정으로 성공을 추론하지 않음), 이 값이 지금 열려 있는 게시글과 같아지면
        // 곧바로 닫고 dismiss로 신호를 지운다.
        LaunchedEffect(uiState.editedCertificationId) {
            if (uiState.editedCertificationId == certification.id) {
                editingCertification = null
                actions.onEditCertificationDismiss()
            }
        }
        CertificationEditScreen(
            certification = certification,
            onClose = {
                editingCertification = null
                actions.onEditCertificationDismiss()
            },
            onSubmit = { content -> actions.onEditCertificationSubmit(certification.id, content) },
            isSubmitting = uiState.isSubmittingEdit,
            errorMessage = uiState.editCertificationError,
            onDeleteClick = {
                editingCertification = null
                actions.onDeleteCertificationClick(certification.id)
            },
            modifier = modifier,
        )
        return
    }

    reportingCertification?.let { certification ->
        ReportReasonScreen(
            onClose = { reportingCertification = null },
            onSubmit = { reportType, reason ->
                actions.onReportCertificationClick(certification.id, reportType, reason)
                reportingCertification = null
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

    // 조금이라도 스크롤을 내리면(맨 위가 아니면) "맨 위로" 버튼을 보여준다.
    val showScrollToTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0 }
    }

    // 정렬 기준이 바뀌면 목록이 통째로 리셋되므로, 스크롤 위치도 맨 위로 되돌린다 — 그렇지 않으면
    // 이전 스크롤 위치가 남아 무한 스크롤 트리거가 불필요하게 다시 발동할 수 있다.
    LaunchedEffect(uiState.selectedSort) {
        listState.scrollToItem(0)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LiroutiTheme.colors.backgroundDefault),
        ) {
            // 뒤로가기/더보기 버튼이 있는 헤더는 다른 화면들과 동일하게 별도 고정 영역으로 분리한다
            // (흰 배경 + statusBarsPadding으로 시스템 상태바 영역까지 채움). 히어로 이미지는 일반 콘텐츠로
            // 취급해 아래 스크롤 영역 맨 위에 배치한다.
            ChallengeDetailHeader(
                onBackClick = onBackClick,
                onMoreClick = { showMoreSheet = true },
                showMoreButton = uiState.isJoined,
            )

            // 당겨서 새로고침은 고정 헤더 아래 스크롤 영역만 감싼다 — 헤더는 그대로 있고, 이 영역이
            // 늘어나며 새로고침 인디케이터가 뜬다.
            LiroutiPullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = actions::onRefresh,
                modifier = Modifier.fillMaxSize(),
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    item {
                        ChallengeHeroImage(imageUrl = uiState.heroImageUrl)
                    }
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(top = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                        ) {
                            ChallengeInfoSection(
                                uiState = uiState,
                                onJoinClick = actions::onJoinClick,
                                onVerifyClick = onStartVerification,
                            )
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
                    item {
                        CertificationSortRow(
                            selectedLabel = uiState.selectedSort.toDisplayLabel(),
                            onClick = { showSortSheet = true },
                        )
                    }
                    // 각 탭의 목록이 실제로 다 불러와진 뒤(로딩 중이 아닐 때)만 "없음"으로 판단한다.
                    // uiState.isLoading(화면 최초 로딩 전용 플래그)로 판단하면, "인증"/"내 인증 보기"
                    // 각 탭 자체의 조회가 아직 끝나기 전에도 "없음"으로 잘못 그려졌다가 응답이 오면
                    // 사라지는 깜빡임이 생긴다 — 탭별 로딩 상태를 따로 봐야 한다.
                    val certificationEmptyMessage = when {
                        uiState.selectedTab == CertificationTab.Mine &&
                            uiState.visibleCertifications.isEmpty() &&
                            uiState.myLoaded && !uiState.isLoadingMoreMy -> "아직 인증을 올리지 않았어요"
                        uiState.selectedTab == CertificationTab.All &&
                            uiState.visibleCertifications.isEmpty() &&
                            !uiState.isLoadingMoreAll -> "아직 올라온 인증이 없어요"
                        else -> null
                    }
                    if (certificationEmptyMessage != null) {
                        item {
                            CertificationEmptyState(
                                message = certificationEmptyMessage,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 40.dp),
                            )
                        }
                    }
                    items(uiState.visibleCertifications, key = { it.id }) { certification ->
                        CertificationCard(
                            certification = certification,
                            // "인증"(전체) 탭에서 본인 글은 신고할 수 없어야 하므로 더보기 버튼 자체를 숨긴다.
                            // "내 인증 보기" 탭은 항상 본인 글이라 수정하기/삭제하기를 위해 그대로 노출한다.
                            onMoreClick = if (uiState.selectedTab == CertificationTab.All && certification.isMine) {
                                null
                            } else {
                                { moreSheetCertification = certification }
                            },
                            // "내 인증 보기" 응답 자체엔 liked가 없지만 ViewModel이 "인증"(전체) 쪽 값으로
                            // 맞춰주므로, 두 탭 모두 좋아요를 누를 수 있다(자기 글에도 좋아요 가능).
                            onLikeClick = { actions.onLikeToggleClick(certification.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 20.dp),
                        )
                    }
                    item { Spacer(modifier = Modifier.height(30.dp)) }
                }
            }
        }

        LiroutiScrollToTopButton(
            visible = showScrollToTop,
            onClick = { coroutineScope.launch { listState.animateScrollToItem(0) } },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(16.dp),
        )

        if (uiState.actionErrorMessage != null) {
            LiroutiToast(
                message = uiState.actionErrorMessage,
                onCloseClick = actions::onActionErrorDismissed,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp),
            )
        }
    }

    if (showMoreSheet) {
        // Figma node 4424:51870 ("더보기" 바텀시트, 챌린지 상세). title을 null로 둬서 X 닫기 버튼
        // 대신 "닫기" 행으로 닫는다(신고/수정 바텀시트와 동일한 패턴).
        LiroutiBottomSheet(
            onDismissRequest = { showMoreSheet = false },
            title = null,
        ) {
            MoreSheetActionRow(
                text = "챌린지 나가기",
                onClick = {
                    actions.onLeaveChallengeClick()
                    showMoreSheet = false
                },
            )
            MoreSheetActionRow(text = "닫기", onClick = { showMoreSheet = false })
        }
    }

    // 인증 게시글 더보기 바텀시트. 내 글이면 수정하기(Figma 4424:51903, 삭제하기는 인증 수정 화면
    // 안으로 옮겨감), 타인 글이면 신고하기(Figma 4424:51887)를 보여준다. 둘 다 "닫기" 행으로 닫는다.
    moreSheetCertification?.let { certification ->
        LiroutiBottomSheet(
            onDismissRequest = { moreSheetCertification = null },
            title = null,
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
                        reportingCertification = certification
                        moreSheetCertification = null
                    },
                )
            }
            MoreSheetActionRow(text = "닫기", onClick = { moreSheetCertification = null })
        }
    }

    // 인증 게시글 정렬 바텀시트 (Figma 4424:51942).
    if (showSortSheet) {
        LiroutiBottomSheet(
            onDismissRequest = { showSortSheet = false },
            title = null,
        ) {
            MoreSheetActionRow(
                text = "최신순",
                onClick = {
                    actions.onSortSelected(VerificationSort.LATEST)
                    showSortSheet = false
                },
            )
            MoreSheetActionRow(
                text = "인기순",
                onClick = {
                    actions.onSortSelected(VerificationSort.LIKES)
                    showSortSheet = false
                },
            )
            MoreSheetActionRow(text = "닫기", onClick = { showSortSheet = false })
        }
    }
}

/** "인증"(전체)/"내 인증 보기" 두 탭 모두, 목록이 비어 있을 때 목록 자리에 보여주는 상태. */
@Composable
private fun CertificationEmptyState(message: String, modifier: Modifier = Modifier) {
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

@Composable
private fun CertificationSortRow(
    selectedLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(text = selectedLabel, style = LiroutiTheme.typography.body1Medium, color = LiroutiTheme.colors.labelDefault)
        LiroutiChevronDownIcon(modifier = Modifier.size(16.dp), color = LiroutiTheme.colors.labelDefault)
    }
}

@Composable
private fun MoreSheetActionRow(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = LiroutiTheme.typography.body1Medium,
            color = LiroutiTheme.colors.labelDefault,
        )
    }
}

// 뒤로가기/더보기가 있는 고정 헤더. 다른 화면들의 상단 바와 동일하게 흰 배경 +
// statusBarsPadding으로 시스템 상태바 영역까지 채워서, 상태바가 항상 흰 배경으로 보이게 한다.
@Composable
private fun ChallengeDetailHeader(
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit,
    showMoreButton: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(LiroutiTheme.colors.backgroundDefault)
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LiroutiChevronLeftIcon(
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = onBackClick),
                color = LiroutiTheme.colors.labelDefault,
            )
            if (showMoreButton) {
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
}

// 챌린지 대표 이미지 — 좋아요가 가장 많은 인증 사진(ChallengeDetailViewModel.loadHeroImage)을 채운다.
// 아직 못 받아왔거나 인증이 하나도 없으면([imageUrl]이 null) 자리만 보여주는 플레이스홀더로
// 대체한다. 헤더와 달리 일반 콘텐츠이므로 스크롤 영역 안에 들어간다.
@Composable
private fun ChallengeHeroImage(imageUrl: String?, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(HeroBg),
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(84.dp)
                    .background(LiroutiTheme.colors.backgroundSecondary, RoundedCornerShape(8.dp)),
            )
        }
    }
}

@Composable
private fun ChallengeInfoSection(
    uiState: ChallengeDetailUiState,
    onJoinClick: () -> Unit,
    onVerifyClick: () -> Unit,
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

        // "참여하기" 탭 시 참여 상태로 바뀌고 서버에 참여 신호를 보낸다(ViewModel에서 처리).
        // 참여 후 현재 인증 주기에 이미 인증했다면(verifiedInCurrentPeriod) "인증 완료"로 바뀌며
        // 더 이상 누를 수 없다(재인증 불가) — 이 값은 챌린지 상세 API가 내려주므로 나갔다 들어와도 유지된다.
        LiroutiPrimaryButton(
            text = when {
                !uiState.isJoined -> "참여하기"
                uiState.verifiedInCurrentPeriod -> "인증 완료"
                else -> "인증하기"
            },
            enabled = !uiState.isJoined || !uiState.verifiedInCurrentPeriod,
            onClick = { if (uiState.isJoined) onVerifyClick() else onJoinClick() },
        )
    }
}

private object PreviewChallengeDetailScreenActions : ChallengeDetailScreenActions {
    override fun onJoinClick() = Unit
    override fun onTabSelected(tab: CertificationTab) = Unit
    override fun onLoadMore() = Unit
    override fun onLeaveChallengeClick() = Unit
    override fun onEditCertificationSubmit(certificationId: Long, content: String) = Unit
    override fun onEditCertificationDismiss() = Unit
    override fun onDeleteCertificationClick(certificationId: Long) = Unit
    override fun onReportCertificationClick(certificationId: Long, reportType: ReportType, reason: String?) = Unit
    override fun onSortSelected(sort: VerificationSort) = Unit
    override fun onLikeToggleClick(certificationId: Long) = Unit
    override fun onVerificationSubmitted() = Unit
    override fun onRefresh() = Unit
    override fun onActionErrorDismissed() = Unit
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
            onStartVerification = {},
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
            onStartVerification = {},
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
            onStartVerification = {},
        )
    }
}
