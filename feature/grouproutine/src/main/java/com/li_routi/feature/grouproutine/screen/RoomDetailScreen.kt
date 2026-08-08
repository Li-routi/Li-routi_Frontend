package com.li_routi.feature.grouproutine.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.LiroutiChevronLeftIcon
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.grouproutine.component.ChatBar
import com.li_routi.feature.grouproutine.component.ChatBox
import com.li_routi.feature.grouproutine.component.ChatEmoticonUiModel
import com.li_routi.feature.grouproutine.component.ChatMessageUiModel
import com.li_routi.feature.grouproutine.component.EmojiPannel
import com.li_routi.feature.grouproutine.component.isGroupStart

/** 채팅바 하단에 무엇이 떠 있는지: 아무것도 없음 / 소프트 키보드 / 이모지 패널. */
private enum class ChatInputMode { NONE, KEYBOARD, EMOJI }

/** 키보드를 한 번도 띄운 적이 없어 실제 높이를 측정 못 했을 때 [EmojiPannel]에 쓸 기본 높이. */
private val DefaultEmojiPanelHeight = 250.dp

/** [EmojiPannel]이 아직 실제 셀 크기를 측정해 알려주기 전(=한 번도 연 적 없음)에 쓸 기본 크기. */
private val DefaultEmojiSize = 40.dp

/** 상단 바/채팅바를 제외한 메시지 영역의 배경색. */
private val MessageAreaBackground = Color(0xFFE8EAED)

/**
 * 모임방 상세 화면 (Figma `ROOM_DETAIL`)의 최소 placeholder.
 *
 * 실제 채팅/인증/방 관리 등은 별도 디자인 섹션(06번)이라 이번 범위에서 빠지고,
 * "그룹 루틴" 탭 접근 흐름을 확인할 수 있을 정도로만 이름·멤버·루틴 수를 보여준다.
 */
@Composable
fun RoomDetailScreen(
    room: GroupRoomUiModel,
    messages: List<ChatMessageUiModel>,
    chatDraftText: String,
    emoticons: List<ChatEmoticonUiModel>,
    onBackClick: () -> Unit,
    onChatMessageChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    emptyChatMessage: String = "채팅을 시작해 보세요!",
    onEmojiClick: () -> Unit = {},
    onSendClick: () -> Unit = {},
    onEmojiSelected: (ChatEmoticonUiModel) -> Unit = {},
) {
    var chatInputMode by remember { mutableStateOf(ChatInputMode.NONE) }
    val chatFieldFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // 이모지 버튼을 누르는 "그 순간"의 키보드 높이(ime 인셋)를 스냅샷으로 찍어 패널 높이로 쓴다.
    // keyboardController.hide()를 부르고 나서 값을 읽으면 키보드가 내려가는 애니메이션 도중의
    // (거의 0에 가까운) 값을 잡아버리므로, 반드시 hide() 호출 "전에" 읽어야 한다.
    val density = LocalDensity.current
    val currentImeHeightPx = WindowInsets.ime.getBottom(density)
    var emojiPanelHeight by remember { mutableStateOf(DefaultEmojiPanelHeight) }
    // 이모지 패널에서 실제로 그려진 셀 크기 — 채팅으로 전송된 이모티콘도 같은 크기로 보여주는 데 쓴다.
    var emojiSize by remember { mutableStateOf(DefaultEmojiSize) }

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
                .weight(1f)
                .background(MessageAreaBackground),
        ) {
            if (messages.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
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
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    itemsIndexed(messages) { index, message ->
                        ChatBox(
                            message = message,
                            isGroupStart = message.isGroupStart(messages.getOrNull(index - 1)),
                            emojiSize = emojiSize,
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 3.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ChatBar(
                message = chatDraftText,
                onMessageChange = onChatMessageChange,
                focusRequester = chatFieldFocusRequester,
                isEmojiPanelOpen = chatInputMode == ChatInputMode.EMOJI,
                onFocusChanged = { focused -> if (focused) chatInputMode = ChatInputMode.KEYBOARD },
                onTextFieldTap = {
                    chatInputMode = ChatInputMode.KEYBOARD
                    keyboardController?.show()
                },
                onEmojiClick = {
                    if (currentImeHeightPx > 0) {
                        emojiPanelHeight = with(density) { currentImeHeightPx.toDp() }
                    }
                    keyboardController?.hide()
                    chatInputMode = ChatInputMode.EMOJI
                    onEmojiClick()
                },
                onSendClick = {
                    if (chatDraftText.isNotBlank()) onSendClick()
                },
            )

            if (chatInputMode == ChatInputMode.EMOJI) {
                EmojiPannel(
                    emoticons = emoticons,
                    height = emojiPanelHeight,
                    onCellSizeMeasured = { emojiSize = it },
                    onEmojiSelected = { emoticon ->
                        // 채팅바에 입력되거나 전송 버튼을 거치지 않고, 탭한 즉시 채팅으로 전송된다.
                        onEmojiSelected(emoticon)
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
            messages = emptyList(),
            chatDraftText = "",
            emoticons = emptyList(),
            onBackClick = {},
            onChatMessageChange = {},
        )
    }
}
