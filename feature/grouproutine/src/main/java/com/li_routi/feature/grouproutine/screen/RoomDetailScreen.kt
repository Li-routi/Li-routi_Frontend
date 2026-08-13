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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.common.ui.calendar.LiroutiCalendarBottomSheet
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.LiroutiChevronLeftIcon
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.grouproutine.component.ChatBar
import com.li_routi.feature.grouproutine.component.ChatBox
import com.li_routi.feature.grouproutine.component.ChatDateDivider
import com.li_routi.feature.grouproutine.component.ChatEmoticonUiModel
import com.li_routi.feature.grouproutine.component.ChatMessageUiModel
import com.li_routi.feature.grouproutine.component.EmojiPannel
import com.li_routi.feature.grouproutine.component.isGroupEnd
import com.li_routi.feature.grouproutine.component.isGroupStart
import com.li_routi.feature.grouproutine.component.isNewDate
import com.li_routi.feature.grouproutine.component.toLocalDate
import java.time.LocalDate
import kotlinx.coroutines.launch

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
    replyTarget: ChatMessageUiModel? = null,
    onReplySwipe: (ChatMessageUiModel) -> Unit = {},
    onReplyCancelClick: () -> Unit = {},
    emptyChatMessage: String = "채팅을 시작해 보세요!",
    onEmojiClick: () -> Unit = {},
    onSendClick: () -> Unit = {},
    onEmojiSelected: (ChatEmoticonUiModel) -> Unit = {},
    onCalendarClick: () -> Unit = {},
    onLoadMore: () -> Unit = {},
    isInitialHistoryLoaded: Boolean = true,
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

    var isCalendarSheetVisible by remember { mutableStateOf(false) }
    var calendarSelectedDate by remember { mutableStateOf(LocalDate.now()) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // 스크롤이 맨 위(과거 채팅 방향) 근처에 도달하면 더 오래된 메시지를 불러오도록 요청한다.
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index -> if (index <= 3) onLoadMore() }
    }

    // 채팅방 진입 시 맨 위(가장 오래된 메시지) 기준으로 보이던 문제 — 메시지가 처음 채워지는 순간
    // 딱 한 번 최신(맨 아래) 메시지로 이동한다. 이후(과거 메시지 이어붙이기 등) 재실행되지 않도록
    // hasScrolledToLatest로 막는다.
    //
    // isInitialHistoryLoaded도 함께 확인해야 한다 — 소켓 구독이 REST 이력 조회보다 먼저 시작되므로,
    // 이력이 오기 전에 실시간 메시지 하나가 먼저 도착하면 messages가 그 한 건만으로 비어있지 않게
    // 되어 이 이펙트가 그 시점(맨 위=맨 아래인 index 0)에서 소모돼버린다. 이후 이력이 앞에 붙어도
    // "딱 한 번"은 이미 써버렸으니 다시 스크롤되지 않는다.
    var hasScrolledToLatest by remember { mutableStateOf(false) }
    LaunchedEffect(messages.isNotEmpty(), isInitialHistoryLoaded) {
        if (!hasScrolledToLatest && isInitialHistoryLoaded && messages.isNotEmpty()) {
            hasScrolledToLatest = true
            listState.scrollToItem(messages.lastIndex)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                // 키보드가 올라올 때 상단 바까지 통째로 밀리지 않도록, 바닥 패딩만 여기서 흡수한다 —
                // fillMaxSize()로 이미 전체 높이를 잡은 뒤라 첫 자식(상단 바)은 그대로 고정되고,
                // weight(1f)인 메시지 영역만 줄어들며 마지막 자식(채팅바)이 키보드 위로 따라 올라온다.
                .imePadding()
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
                Image(
                    painter = painterResource(id = R.drawable.calendar),
                    contentDescription = "날짜 선택",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                        .size(24.dp)
                        .clickable(onClick = {
                            isCalendarSheetVisible = true
                            onCalendarClick()
                        }),
                    colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelDefault),
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
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    itemsIndexed(items = messages, key = { _, message -> message.id }) { index, message ->
                        val previous = messages.getOrNull(index - 1)
                        val next = messages.getOrNull(index + 1)
                        Column {
                            if (message.isNewDate(previous)) {
                                ChatDateDivider(sentAtMillis = message.sentAtMillis)
                            }
                            ChatBox(
                                message = message,
                                isGroupStart = message.isGroupStart(previous),
                                isGroupEnd = message.isGroupEnd(next),
                                emojiSize = emojiSize,
                                onReplySwipe = onReplySwipe,
                            )
                        }
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
                replyTarget = replyTarget,
                onReplyCancelClick = onReplyCancelClick,
                isEmojiPanelOpen = chatInputMode == ChatInputMode.EMOJI,
                onFocusChanged = { focused -> if (focused) chatInputMode = ChatInputMode.KEYBOARD },
                onTextFieldTap = {
                    chatFieldFocusRequester.requestFocus()
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

        if (isCalendarSheetVisible) {
            LiroutiCalendarBottomSheet(
                initialDate = calendarSelectedDate,
                onDismissRequest = { isCalendarSheetVisible = false },
                onDateSelected = { date ->
                    isCalendarSheetVisible = false
                    calendarSelectedDate = date
                    val targetIndex = messages.indexOfFirst { it.sentAtMillis.toLocalDate() == date }
                    if (targetIndex >= 0) {
                        coroutineScope.launch {
                            listState.animateScrollToItem(targetIndex)
                        }
                    }
                },
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
            messages = emptyList(),
            chatDraftText = "",
            emoticons = emptyList(),
            onBackClick = {},
            onChatMessageChange = {},
        )
    }
}
