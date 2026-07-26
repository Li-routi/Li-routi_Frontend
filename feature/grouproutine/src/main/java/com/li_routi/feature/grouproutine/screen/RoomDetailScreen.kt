package com.li_routi.feature.grouproutine.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.LiroutiChevronLeftIcon
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * 모임방 상세 화면 (Figma `ROOM_DETAIL`)의 최소 placeholder.
 *
 * 실제 채팅/인증/방 관리 등은 별도 디자인 섹션(06번)이라 이번 범위에서 빠지고,
 * "그룹 루틴" 탭 접근 흐름을 확인할 수 있을 정도로만 이름·멤버·루틴 수를 보여준다.
 */
@Composable
fun RoomDetailScreen(
    room: GroupRoomUiModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundDefault),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
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
                text = room.name,
                style = LiroutiTheme.typography.heading2SemiBold,
                color = LiroutiTheme.colors.labelDefault,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "멤버 ${room.memberCount}명 · 루틴 ${room.routineCount}개",
                style = LiroutiTheme.typography.body1SemiBold,
                color = LiroutiTheme.colors.labelDefault,
            )
            Text(
                text = "채팅·인증·방 관리 등은 추후 제공될 예정이에요.",
                style = LiroutiTheme.typography.captionRegular,
                color = LiroutiTheme.colors.labelInfo,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun RoomDetailScreenPreview() {
    LiroutiFrontendTheme {
        RoomDetailScreen(
            room = GroupRoomUiModel(id = "1", name = "갓생살자", memberCount = 3, routineCount = 7),
            onBackClick = {},
        )
    }
}
