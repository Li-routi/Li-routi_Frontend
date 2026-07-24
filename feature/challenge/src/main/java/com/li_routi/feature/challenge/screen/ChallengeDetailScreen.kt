package com.li_routi.feature.challenge.screen

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.component.LiroutiBottomSheet
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.feature.challenge.navigation.ChallengeDetailScreenActions
import com.li_routi.feature.challenge.vm.CertificationTab
import com.li_routi.feature.challenge.vm.CertificationUiModel
import com.li_routi.feature.challenge.vm.ChallengeDetailUiState

// ============================================================
// 색상 (Design Token 프레임 기준, 다른 챌린지 화면들과 동일한 값) - 컴포넌트 라이브러리가
// 완성되면 AppColors 오브젝트로 옮기면 됩니다. 지금은 로컬 상수로 둡니다.
// ============================================================
private val BgDefault = Color(0xFFFFFFFF)
private val BgSecondary = Color(0xFFF4F7FB)
private val BgFill = Color(0xFFFAFAFA)
private val BorderDefault = Color(0xFFDBDCDF)
private val LabelDefault = Color(0xFF171719)
private val LabelSub = Color(0xFF46474C)
private val LabelInfo = Color(0xFF878A93)
private val PrimaryNormal = Color(0xFF338AFF)
private val SecondaryNormal = Color(0xFF00AAD2)
private val HeroBg = Color(0xFFF3F6FF)
private val ButtonLabel = Color(0xFFF7F7F8)

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
    // 더보기 바텀시트 노출 여부는 화면 로컬 UI 상태(서버/재사용 데이터가 아님).
    var showMoreSheet by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // 리스트 끝에 가까워지면 다음 페이지를 불러오는 간단한 무한 스크롤 트리거.
    // TODO: 컴포넌트/유틸 완성되면 공용 페이지네이션 헬퍼로 교체
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
            .background(BgDefault),
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
                ChallengeStatsRow(uiState = uiState)
            }
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
            ChallengeCertificationTabRow(
                selectedTab = uiState.selectedTab,
                onTabSelected = actions::onTabSelected,
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
        items(uiState.visibleCertifications, key = { it.id }) { certification ->
            CertificationCard(
                certification = certification,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 20.dp),
            )
        }
        item { Spacer(modifier = Modifier.height(30.dp)) }
    }

    if (showMoreSheet) {
        // Figma node 2222:22836 ("더보기" 바텀시트). LiroutiBottomSheet가 dim 배경 + 시트 형태를
        // 이미 처리해준다. title을 빈 문자열로 둬서 닫기(X) 버튼만 우측에 뜨도록 함(디자인엔 타이틀 텍스트가 없음).
        LiroutiBottomSheet(
            onDismissRequest = { showMoreSheet = false },
            title = "",
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        actions.onLeaveChallengeClick()
                        showMoreSheet = false
                    }
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "챌린지 나가기", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = LabelDefault)
                Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = LabelDefault)
            }
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
                .background(BgSecondary, RoundedCornerShape(8.dp)),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
            }
            IconButton(onClick = onMoreClick) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "더보기")
            }
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
            Box(
                modifier = Modifier
                    .background(BgSecondary, RoundedCornerShape(6.dp))
                    .padding(horizontal = 7.dp, vertical = 2.dp),
            ) {
                Text(text = uiState.badge, fontSize = 11.sp, color = SecondaryNormal)
            }
            Text(text = uiState.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LabelDefault)
            Text(text = uiState.description, fontSize = 12.sp, color = LabelDefault)
        }

        // "참여하기" 탭 시 참여 상태로 바뀌고 서버에 참여 신호를 보낸다(ViewModel에서 처리, 이번 범위는 로컬 상태만).
        // 참여 후 "인증하기"는 별도 인증 업로드 플로우로 연결될 예정 — 이번 범위 제외.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(PrimaryNormal, RoundedCornerShape(6.dp))
                .clickable(enabled = !uiState.isJoined, onClick = onJoinClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (uiState.isJoined) "인증하기" else "참여하기",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = ButtonLabel,
            )
        }
    }
}

@Composable
private fun ChallengeStatsRow(uiState: ChallengeDetailUiState, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(74.dp)
            .background(BgFill, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(25.dp, Alignment.CenterHorizontally),
    ) {
        ChallengeStat(value = uiState.participantCount, label = "참여자")
        ChallengeStatDivider()
        ChallengeStat(value = uiState.activityCount, label = "활동")
        ChallengeStatDivider()
        ChallengeStat(value = uiState.postCount, label = "인증 게시글")
    }
}

@Composable
private fun ChallengeStat(value: Int, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.width(60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(text = value.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LabelSub)
        Text(text = label, fontSize = 13.sp, color = LabelSub)
    }
}

@Composable
private fun ChallengeStatDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(1.dp)
            .height(50.dp)
            .background(BorderDefault),
    )
}

// "인증"/"내 인증 보기" 탭. 탭 전환 시 아래 리스트 내용이 바뀐다(화면 이동 아님).
@Composable
private fun ChallengeCertificationTabRow(
    selectedTab: CertificationTab,
    onTabSelected: (CertificationTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CertificationTabItem(
                label = "인증",
                selected = selectedTab == CertificationTab.All,
                onClick = { onTabSelected(CertificationTab.All) },
                modifier = Modifier.weight(1f),
            )
            CertificationTabItem(
                label = "내 인증 보기",
                selected = selectedTab == CertificationTab.Mine,
                onClick = { onTabSelected(CertificationTab.Mine) },
                modifier = Modifier.weight(1f),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BorderDefault),
        )
    }
}

@Composable
private fun CertificationTabItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(top = 14.dp, bottom = if (selected) 0.dp else 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) LabelDefault else LabelInfo,
        )
        if (selected) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(LabelDefault),
            )
        }
    }
}

// 인증 게시글 카드 한 건 (Figma "Certification_IMG", node 2372:39010).
@Composable
private fun CertificationCard(
    certification: CertificationUiModel,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // 프로필 이미지(비-DS 이미지 자산) — 실제 에셋은 백엔드에서 제공, 지금은 자리만
            Box(modifier = Modifier.size(32.dp).background(BgSecondary, CircleShape))
            Text(
                text = certification.authorName,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = LabelDefault,
                modifier = Modifier.weight(1f),
            )
            // 게시글별 수정/삭제/신고 메뉴는 이후 단계에서 연결 — 지금은 아이콘만
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = LabelInfo,
                modifier = Modifier.size(20.dp),
            )
        }
        Text(text = certification.content, fontSize = 14.sp, color = LabelDefault)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            // 인증 사진(비-DS 이미지 자산) — 실제 에셋은 백엔드에서 제공, 지금은 자리만
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(144.dp)
                    .background(BgSecondary, RoundedCornerShape(6.dp)),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = LabelDefault,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(text = certification.likeCount.toString(), fontSize = 13.sp, fontWeight = FontWeight.Medium, color = LabelDefault)
                }
                Text(text = certification.timeLabel, fontSize = 13.sp, color = LabelInfo)
            }
        }
    }
}

private object PreviewChallengeDetailScreenActions : ChallengeDetailScreenActions {
    override fun onJoinClick() = Unit
    override fun onTabSelected(tab: CertificationTab) = Unit
    override fun onLoadMore() = Unit
    override fun onLeaveChallengeClick() = Unit
}

@Preview(showBackground = true, heightDp = 900, name = "1. 참여 전")
@Composable
private fun ChallengeDetailScreenNotJoinedPreview() {
    LiroutiFrontendTheme {
        ChallengeDetailScreen(
            uiState = ChallengeDetailUiState(challengeId = 1L),
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
            uiState = ChallengeDetailUiState(challengeId = 1L, isJoined = true),
            actions = PreviewChallengeDetailScreenActions,
            onBackClick = {},
        )
    }
}
