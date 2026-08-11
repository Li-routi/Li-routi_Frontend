package com.li_routi.feature.grouproutine.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.common.ui.nav.AppBottomNavBar
import com.li_routi.core.common.ui.nav.AppBottomTab
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.LabelButton
import com.li_routi.core.designsystem.component.LiroutiBottomSheet
import com.li_routi.core.designsystem.component.LiroutiButtonStyle
import com.li_routi.core.designsystem.component.LiroutiRoutineSimpleCard
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/** 모임방 카드 한 건. 백엔드 목록 API가 붙기 전까지는 항상 빈 리스트로 시작한다. */
data class GroupRoomUiModel(
    val id: String,
    val name: String,
    val memberCount: Int,
    val routineCount: Int,
)

/**
 * "그룹 루틴" 탭 루트 화면 (Figma `ROOM_LIST`, 모임 메인).
 *
 * 참여 중인 방을 카드로 보여주고, 카드를 누르면 [RoomDetailScreen]으로 이동한다.
 * 우측 상단 `+`는 홈 화면과 동일하게 "방 만들기"/"초대코드로 참여" 메뉴를 연다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomListScreen(
    rooms: List<GroupRoomUiModel>,
    onRoomClick: (roomId: String) -> Unit,
    onCreateRoomClick: () -> Unit,
    onJoinRoomWithInviteCodeClick: () -> Unit,
    onTabSelected: (AppBottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showAddMenuSheet by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {

            // ---------- 상단 네비게이션 ("그룹 루틴" + 추가 버튼) ----------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LiroutiTheme.colors.backgroundDefault)
                    .statusBarsPadding()
                    .height(48.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "그룹 루틴",
                    style = LiroutiTheme.typography.heading2SemiBold,
                    color = LiroutiTheme.colors.labelDefault,
                )
                Image(
                    painter = painterResource(id = R.drawable.add__alt),
                    contentDescription = "모임 추가",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                        .size(20.dp)
                        .clickable { showAddMenuSheet = true },
                )
            }

            if (rooms.isEmpty()) {
                EmptyRoomListContent(onCreateRoomClick = onCreateRoomClick)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LiroutiTheme.colors.backgroundSecondary)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    rooms.forEach { room ->
                        LiroutiRoutineSimpleCard(
                            title = room.name,
                            subtitle = "멤버 ${room.memberCount}명 · 루틴 ${room.routineCount}개",
                            modifier = Modifier.clickable { onRoomClick(room.id) },
                        )
                    }
                }
            }
        }

        // 홈/그룹 루틴/챌린지/마이 4탭 하단 GNB. 모임방 상세 화면에는 노출되지 않는다.
        AppBottomNavBar(selectedTab = AppBottomTab.GroupRoutine, onTabSelected = onTabSelected)
    }

    if (showAddMenuSheet) {
        RoomAddMenuBottomSheet(
            onDismissRequest = { showAddMenuSheet = false },
            onCreateRoomClick = onCreateRoomClick,
            onJoinRoomWithInviteCodeClick = onJoinRoomWithInviteCodeClick,
        )
    }
}

// 참여 중인 모임이 없을 때 (Figma 기준 아직 목업 없음 — ChallengeScreen의 빈 상태와 같은 패턴으로 통일).
@Composable
private fun EmptyRoomListContent(
    onCreateRoomClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
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
                    text = "아직 참여 중인 모임이 없어요",
                    style = LiroutiTheme.typography.body2Regular,
                    color = LiroutiTheme.colors.labelInfo,
                    modifier = Modifier.width(280.dp),
                    textAlign = TextAlign.Center,
                )
            }

            LabelButton(
                text = "방 만들기",
                onClick = onCreateRoomClick,
                style = LiroutiButtonStyle.Quaternary,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoomAddMenuBottomSheet(
    onDismissRequest: () -> Unit,
    onCreateRoomClick: () -> Unit,
    onJoinRoomWithInviteCodeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LiroutiBottomSheet(onDismissRequest = onDismissRequest, modifier = modifier) {
        RoomAddMenuItem(
            label = "방 만들기",
            onClick = {
                onCreateRoomClick()
                onDismissRequest()
            },
        )
        RoomAddMenuItem(
            label = "초대코드로 참여",
            onClick = {
                onJoinRoomWithInviteCodeClick()
                onDismissRequest()
            },
        )
    }
}

@Composable
private fun RoomAddMenuItem(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = LiroutiTheme.typography.body1,
            color = LiroutiTheme.colors.labelStrong,
            modifier = Modifier.weight(1f),
        )
        Image(
            painter = painterResource(id = R.drawable.chevron__right),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelDefault),
        )
    }
}

// Preview 전용 샘플. 실제 화면 데이터는 항상 빈 리스트로 시작한다(백엔드 연동 전).
private val PreviewRooms = listOf(
    GroupRoomUiModel(id = "1", name = "갓생살자", memberCount = 3, routineCount = 7),
)

@Preview(showBackground = true, heightDp = 800, name = "1. 참여 중인 방 있음")
@Composable
private fun RoomListScreenPreview() {
    LiroutiFrontendTheme {
        RoomListScreen(
            rooms = PreviewRooms,
            onRoomClick = {},
            onCreateRoomClick = {},
            onJoinRoomWithInviteCodeClick = {},
            onTabSelected = {},
        )
    }
}

@Preview(showBackground = true, heightDp = 800, name = "2. 참여 중인 방 없음")
@Composable
private fun RoomListScreenEmptyPreview() {
    LiroutiFrontendTheme {
        RoomListScreen(
            rooms = emptyList(),
            onRoomClick = {},
            onCreateRoomClick = {},
            onJoinRoomWithInviteCodeClick = {},
            onTabSelected = {},
        )
    }
}
