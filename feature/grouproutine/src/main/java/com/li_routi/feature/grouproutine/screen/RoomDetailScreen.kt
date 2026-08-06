package com.li_routi.feature.grouproutine.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.LiroutiChevronLeftIcon
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.grouproutine.component.ChatBar
import com.li_routi.feature.grouproutine.component.EmojiPannel

/** 채팅바 하단에 무엇이 떠 있는지: 아무것도 없음 / 소프트 키보드 / 이모지 패널. */
private enum class ChatInputMode { NONE, KEYBOARD, EMOJI }

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
    emptyChatMessage: String = "채팅을 시작해 보세요!",
    onEmojiClick: () -> Unit = {},
    onSendClick: () -> Unit = {},
) {
    var chatMessage by remember { mutableStateOf("") }
    var hasSentFirstMessage by remember { mutableStateOf(false) }
    var chatInputMode by remember { mutableStateOf(ChatInputMode.NONE) }
    val chatFieldFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            if (!hasSentFirstMessage) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.send__alt__filled_blue),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = emptyChatMessage,
                        style = TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            lineHeight = 21.98.sp,
                            letterSpacing = 0.sp,
                        ),
                        color = LiroutiTheme.colors.labelInfo,
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.ime.union(WindowInsets.navigationBars))
                .padding(bottom = 3.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ChatBar(
                message = chatMessage,
                onMessageChange = { chatMessage = it },
                focusRequester = chatFieldFocusRequester,
                isEmojiPanelOpen = chatInputMode == ChatInputMode.EMOJI,
                onFocusChanged = { focused -> if (focused) chatInputMode = ChatInputMode.KEYBOARD },
                onTextFieldTap = {
                    chatInputMode = ChatInputMode.KEYBOARD
                    keyboardController?.show()
                },
                onEmojiClick = {
                    keyboardController?.hide()
                    chatInputMode = ChatInputMode.EMOJI
                    onEmojiClick()
                },
                onSendClick = {
                    if (chatMessage.isNotBlank()) {
                        onSendClick()
                        hasSentFirstMessage = true
                    }
                    chatMessage = ""
                },
            )

            if (chatInputMode == ChatInputMode.EMOJI) {
                EmojiPannel(
                    onEmojiSelected = {
                        onSendClick()
                        hasSentFirstMessage = true
                        chatInputMode = ChatInputMode.NONE
                    },
                )
            }
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
