package com.li_routi.feature.challenge.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.feature.challenge.component.ChallengeBottomNavBar

// ============================================================
// 색상 (Design Token 프레임 기준) - 컴포넌트 라이브러리가 완성되면
// AppColors 오브젝트로 옮기면 됩니다. 지금은 로컬 상수로 둡니다.
// ============================================================
private val BgDefault = Color(0xFFFFFFFF)
private val BgSecondary = Color(0xFFF4F7FB)
private val BorderDefault = Color(0xFFDBDCDF)
private val OutlinedMid = Color(0xFFCDD0D5)
private val LabelDefault = Color(0xFF171719)
private val LabelSub = Color(0xFF46474C)
private val LabelInfo = Color(0xFF878A93)
private val PrimaryNormal = Color(0xFF338AFF)
private val SecondaryNormal = Color(0xFF00AAD2)

// 챌린지에 딸린 루틴 하나. categories가 여러 개일 수 있음 (하나의 챌린지에 여러 카테고리 매핑 가능).
data class RoutineUiModel(
    val id: Long,
    val title: String,
    val categories: List<String>,
    val badge: String,
)

private val filterOptions = listOf("전체", "건강", "운동", "공부", "생활", "취미")

// Figma node: 2380:37243 (참여 중인 챌린지 있음) / 2187:37494 (참여 중인 챌린지 없음, 디폴트 화면)
// routines가 비어있으면 2187:37494(빈 상태)를, 있으면 2380:37243(리스트)를 보여줍니다.
@Composable
fun ChallengeScreen(
    routines: List<RoutineUiModel>,
    onFindNewChallengeClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {

            // ---------- 상단 네비게이션 (챌린지 + 추가 버튼) ----------
            // TODO: 컴포넌트 완성되면 NavHeader(title = "챌린지", onAddClick = ...)로 교체
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgDefault)
                    .statusBarsPadding()
                    .height(48.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "챌린지", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = LabelDefault)
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "새 챌린지 추가",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                        .size(20.dp),
                )
            }

            if (routines.isEmpty()) {
                EmptyChallengeContent(onFindNewChallengeClick = onFindNewChallengeClick)
            } else {
                ChallengeListContent(routines = routines)
            }
        }

        // 홈/그룹 루틴/챌린지/마이 4탭 하단 GNB. 챌린지 상세 화면에는 노출되지 않는다.
        ChallengeBottomNavBar()
    }
}

// ============================================================
// 2번 디자인 (Figma node: 2187:37494) - 참여 중인 챌린지가 없을 때 디폴트로 보이는 화면
// TODO: 컴포넌트 완성되면 EmptyCase(icon, message, buttonLabel, onButtonClick) 형태로 교체 예정
// ============================================================
@Composable
private fun EmptyChallengeContent(onFindNewChallengeClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgSecondary),
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
                // TODO: 디자인 원본은 warning 아이콘(28x28) - 실제 아이콘 에셋으로 교체 필요
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = LabelInfo,
                    modifier = Modifier.size(28.dp),
                )
                Text(
                    text = "참여 중인 챌린지가 없어요",
                    fontSize = 14.sp,
                    color = LabelInfo,
                    modifier = Modifier.width(280.dp),
                    textAlign = TextAlign.Center,
                )
            }

            // TODO: 컴포넌트 완성되면 Button_Combined(Outlined) 형태로 교체 예정
            OutlinedButton(
                onClick = onFindNewChallengeClick,
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, BorderDefault),
                modifier = Modifier
                    .width(162.dp)
                    .height(44.dp),
            ) {
                Text(text = "새 챌린지 찾아보기", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = LabelDefault)
            }
        }
    }
}

// ============================================================
// 1번 디자인 (Figma node: 2380:37243) - 참여 중인 챌린지가 있을 때 (검색 + 필터 + 리스트)
// ============================================================
@Composable
private fun ChallengeListContent(routines: List<RoutineUiModel>) {
    var searchQuery by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf("전체") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgSecondary)
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
        // TODO: 컴포넌트 완성되면 FilterChip(label, state, onClick)로 교체 예정 (Figma: Dim > Filter, node 2187:22130)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            filterOptions.forEach { label ->
                val isSelected = label == activeFilter
                Box(
                    modifier = Modifier
                        .height(38.dp)
                        .background(
                            color = if (isSelected) PrimaryNormal else BgDefault,
                            shape = RoundedCornerShape(40.dp),
                        )
                        .let {
                            if (isSelected) it else it.border(1.dp, OutlinedMid, RoundedCornerShape(40.dp))
                        }
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .let { mod ->
                            // 필터 클릭 시 activeFilter 갱신 (실제 클릭 가능 영역은 컴포넌트 교체 시 Modifier.clickable로 대체)
                            mod
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color.White else LabelDefault,
                    )
                }
            }
        }

        // ---------- 루틴 리스트 ----------
        // TODO: 컴포넌트 완성되면 RoutineListItem(title, categories, badge, icon)로 교체 예정
        // (Figma: List > Property1=routine_simple, node 2398:11076)
        // 카테고리가 여러 개인 경우 콤마로 이어붙임 (예: "건강, 취미")
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            routines.forEach { routine ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BgDefault, RoundedCornerShape(6.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(BgSecondary, RoundedCornerShape(6.dp)),
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = routine.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = LabelDefault,
                        )
                        // 상단에 카테고리 나열 (여러 개 가능) - description 없이 카테고리만 표기
                        Text(
                            text = routine.categories.joinToString(", "),
                            fontSize = 11.sp,
                            color = LabelSub,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(BgSecondary, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                    ) {
                        Text(text = routine.badge, fontSize = 11.sp, color = SecondaryNormal)
                    }
                }
            }
        }
    }
}
