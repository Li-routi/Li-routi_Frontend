package com.li_routi.feature.challenge.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.feature.challenge.component.ChallengeBottomNavBar

// ============================================================
// 색상 (Design Token 프레임 기준, ChallengeScreen.kt와 동일한 값) - 컴포넌트 라이브러리가
// 완성되면 AppColors 오브젝트로 옮기면 됩니다. 지금은 로컬 상수로 둡니다.
// ============================================================
private val BgDefault = Color(0xFFFFFFFF)
private val BgSecondary = Color(0xFFF4F7FB)
private val BgFill = Color(0xFFFAFAFA)
private val BorderDefault = Color(0xFFDBDCDF)
private val OutlinedMid = Color(0xFFCDD0D5)
private val LabelDefault = Color(0xFF171719)
private val LabelSub = Color(0xFF46474C)
private val LabelInfo = Color(0xFF878A93)
private val PrimaryNormal = Color(0xFF338AFF)
private val SecondaryNormal = Color(0xFF00AAD2)

// "챌린지 찾아보기" 카드 한 건. 실제 목록은 백엔드에서 내려주며, 지금은 Figma 목업과 동일한
// 샘플 2건으로 화면을 구성한다(빈 목록 상태는 기획상 존재하지 않음 — 항상 1건 이상 노출).
data class ChallengeCardUiModel(
    val id: Long,
    val title: String,
    val tagLabel: String,
    val description: String,
    val badge: String,
    val participantCount: Int,
    val activityCount: Int,
    val postCount: Int,
)

private val filterOptions = listOf("전체", "건강", "운동", "공부", "생활", "취미")

private val SampleChallenges = listOf(
    ChallengeCardUiModel(
        id = 1L,
        title = "매일 우유 한 잔",
        tagLabel = "건강",
        description = "매일 우유를 마시며 건강 관리를 해요",
        badge = "매일 루틴",
        participantCount = 300,
        activityCount = 14000,
        postCount = 80,
    ),
    ChallengeCardUiModel(
        id = 2L,
        title = "매일 사과 한 개",
        tagLabel = "1시간 전 활동",
        description = "매일 사과를 먹으며 건강 관리를 해요",
        badge = "매일 루틴",
        participantCount = 3600,
        activityCount = 52000,
        postCount = 123,
    ),
)

// Figma node: 2380:40517 ("챌린지 찾아보기")
// "챌린지" 화면(디폴트/빈 상태)의 "새 챌린지 찾아보기" 버튼에서 넘어오는 화면.
// 카드를 누르면 상세 화면(ChallengeDetailScreen)으로 이동한다 — 상세 화면 자체는 아직 빈 화면.
// 하단 GNB는 챌린지 상세 화면에는 노출되지 않고 이 화면까지만 보인다.
@Composable
fun FindChallengeScreen(
    onBackClick: () -> Unit,
    onChallengeClick: (challengeId: Long) -> Unit,
    modifier: Modifier = Modifier,
    challenges: List<ChallengeCardUiModel> = SampleChallenges,
) {
    var searchQuery by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf("전체") }

    Column(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {

            // ---------- 상단 네비게이션 (뒤로가기 + "챌린지 찾아보기") ----------
            // TODO: 컴포넌트 완성되면 NavHeader(title, onBackClick)로 교체
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgDefault)
                    .statusBarsPadding()
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.align(Alignment.CenterStart),
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                }
                Text(text = "챌린지 찾아보기", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = LabelDefault)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BgSecondary)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 25.dp, bottom = 50.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {

                // ---------- 검색바 ----------
                // TODO: 컴포넌트 완성되면 SearchField(value, onValueChange)로 교체 예정 (Figma: Search, node 2298:12656)
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search", color = LabelInfo, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = LabelInfo) },
                    singleLine = true,
                    shape = RoundedCornerShape(6.dp),
                )

                // ---------- 필터 칩 ----------
                // TODO: 컴포넌트 완성되면 FilterChip(label, state, onClick)로 교체 예정 (Figma: Dim > Filter, node 2380:40523)
                // 선택된 칩("전체")은 체크 아이콘이 같이 표시됨(스크린샷 확인).
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    filterOptions.forEach { label ->
                        val isSelected = label == activeFilter
                        Row(
                            modifier = Modifier
                                .height(38.dp)
                                .background(
                                    color = if (isSelected) PrimaryNormal else BgDefault,
                                    shape = RoundedCornerShape(40.dp),
                                )
                                .let {
                                    if (isSelected) it else it.border(1.dp, OutlinedMid, RoundedCornerShape(40.dp))
                                }
                                .clickable { activeFilter = label }
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                            Text(
                                text = label,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) Color.White else LabelDefault,
                            )
                        }
                    }
                }

                // ---------- 챌린지 카드 리스트 ----------
                // TODO: 컴포넌트 완성되면 ChallengeCard(title, tag, description, badge, stats)로 교체 예정
                // (Figma node 2380:40530 / 2380:40558). 리스트 자체는 백엔드에서 제공되며,
                // 빈 목록 상태는 기획상 없음(항상 1건 이상).
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    challenges.forEach { challenge ->
                        ChallengeCardItem(
                            challenge = challenge,
                            onClick = { onChallengeClick(challenge.id) },
                        )
                    }
                }
            }
        }

        // 홈/그룹 루틴/챌린지/마이 4탭 하단 GNB.
        ChallengeBottomNavBar()
    }
}

// 카드 한 건 전체가 클릭 가능한 버튼 역할을 함 → 챌린지 상세 화면으로 이동.
@Composable
private fun ChallengeCardItem(
    challenge: ChallengeCardUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(BgDefault, RoundedCornerShape(6.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 썸네일 (비-DS 이미지 자산, 백엔드에서 내려줄 실제 이미지의 자리) — 실제 에셋 반영은 이번 범위 제외
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(BgSecondary, RoundedCornerShape(6.dp)),
            )

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = challenge.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = LabelDefault,
                    )
                    Text(text = challenge.tagLabel, fontSize = 12.sp, color = PrimaryNormal)
                }
                Text(text = challenge.description, fontSize = 11.sp, color = Color.Black)
            }

            Box(
                modifier = Modifier
                    .background(BgSecondary, RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 3.dp),
            ) {
                Text(text = challenge.badge, fontSize = 11.sp, color = SecondaryNormal)
            }
        }

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BorderDefault),
        )

        // ---------- 참여자/활동/인증 게시글 통계 ----------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgFill, RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            ChallengeStat(value = challenge.participantCount, label = "참여자")
            ChallengeStatDivider()
            ChallengeStat(value = challenge.activityCount, label = "활동")
            ChallengeStatDivider()
            ChallengeStat(value = challenge.postCount, label = "인증 게시글")
        }
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

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun FindChallengeScreenPreview() {
    LiroutiFrontendTheme {
        FindChallengeScreen(onBackClick = {}, onChallengeClick = {})
    }
}
