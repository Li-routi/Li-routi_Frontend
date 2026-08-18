package com.li_routi.feature.grouproutine.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
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
import com.li_routi.feature.grouproutine.component.EmojiCellSizeProbe
import com.li_routi.feature.grouproutine.component.EmojiPannel
import com.li_routi.feature.grouproutine.component.isGroupEnd
import com.li_routi.feature.grouproutine.component.isGroupStart
import com.li_routi.feature.grouproutine.component.isNewDate
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.launch

/** 채팅바 하단에 무엇이 떠 있는지: 아무것도 없음 / 소프트 키보드 / 이모지 패널. */
private enum class ChatInputMode { NONE, KEYBOARD, EMOJI }

/** 키보드를 한 번도 띄운 적이 없어 실제 높이를 측정 못 했을 때 [EmojiPannel]에 쓸 기본 높이. */
private val DefaultEmojiPanelHeight = 250.dp

/**
 * [EmojiCellSizeProbe]가 첫 측정을 마치기 전(아주 짧은 순간) 쓸 기본 크기.
 * 실제 크기는 화면 진입 즉시 [EmojiCellSizeProbe]가 계산해 덮어쓴다.
 */
private val DefaultEmojiSize = 40.dp

/**
 * 모임방 상세 화면 (Figma `ROOM_DETAIL`)의 최소 placeholder.
 *
 * 실제 채팅/인증/방 관리 등은 별도 디자인 섹션(06번)이라 이번 범위에서 빠지고,
 * "그룹 루틴" 탭 접근 흐름을 확인할 수 있을 정도로만 이름·멤버·루틴 수를 보여준다.
 */
@OptIn(ExperimentalLayoutApi::class)
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
    /** 캘린더에서 선택 가능하게 표시할, 채팅이 존재하는 날짜. */
    chatDates: Set<LocalDate> = emptySet(),
    /** 캘린더 시트에 표시 중인 달이 바뀔 때마다 그 달의 [chatDates]를 조회하도록 호출부에 알린다. */
    onCalendarMonthChange: (YearMonth) -> Unit = {},
    /** 캘린더에서 날짜를 골랐을 때 그 날짜의 채팅을 실제로 불러오도록 호출부에 알린다. */
    onChatDateSelected: (LocalDate) -> Unit = {},
) {
    var chatInputMode by remember { mutableStateOf(ChatInputMode.NONE) }
    val chatFieldFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // 키보드가 떠 있는 채로 시스템 뒤로가기를 누르면(채팅 입력 중) 이 화면을 나가는(진입했던
    // 그룹 목록으로 돌아가는) 대신 키보드부터 내린다 — 안드로이드 기본 동작(뒤로가기=키보드 먼저
    // 닫힘)과 맞추기 위함(RoutineManageRoute의 동일 패턴 참고).
    BackHandler(enabled = WindowInsets.isImeVisible) {
        keyboardController?.hide()
        focusManager.clearFocus()
    }

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
    //
    // hasScrolledToLatest는 스크롤이 "끝난 뒤"에 true로 바뀐다 — LazyColumn을 아래(alpha)에서
    // 그 값이 true일 때만 보여줘서, 맨 위에서 시작했다가 맨 아래로 튀는 게 화면에 한 프레임이라도
    // 비치는 걸 막는다.
    var hasScrolledToLatest by remember { mutableStateOf(false) }
    LaunchedEffect(messages.isNotEmpty(), isInitialHistoryLoaded) {
        if (!hasScrolledToLatest && isInitialHistoryLoaded && messages.isNotEmpty()) {
            listState.scrollToItem(messages.lastIndex)
            hasScrolledToLatest = true
        }
    }

    // 새 메시지가 맨 뒤에 붙었을 때(내가 보냈든, 상대가 보냈든), 그 메시지가 오기 "직전"에 이미
    // 맨 아래를 보고 있었을 때만 자동으로 맨 아래까지 따라 스크롤한다. 과거 메시지를 읽으려고
    // 위로 스크롤해 둔 상태라면 새 메시지가 와도 보던 위치를 유지해야 하므로 스크롤하지 않는다.
    //
    // "직전에 맨 아래였는지"는 이 프레임(새 메시지가 이미 리스트에 반영된 뒤)의 layoutInfo로
    // 판단하되, 새 메시지가 추가되기 전의 마지막 아이템(oldLastIndex)이 화면에 보이고 있었는지를
    // 본다 — 뒤에 항목을 추가해도 그 앞 아이템들의 레이아웃 위치는 바뀌지 않으므로 이 값은
    // 추가 전과 추가 후가 동일하게 유효하다.
    var previousMessageCount by remember { mutableStateOf(messages.size) }
    var previousLastMessageId by remember { mutableStateOf(messages.lastOrNull()?.id) }
    LaunchedEffect(messages) {
        val lastId = messages.lastOrNull()?.id
        val isNewTailMessage = lastId != null &&
            lastId != previousLastMessageId &&
            messages.size > previousMessageCount
        if (isNewTailMessage && hasScrolledToLatest) {
            val oldLastIndex = previousMessageCount - 1
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            val wasAtBottom = oldLastIndex < 0 || lastVisibleIndex >= oldLastIndex
            if (wasAtBottom) {
                listState.animateScrollToItem(messages.lastIndex)
            }
        }
        previousMessageCount = messages.size
        previousLastMessageId = lastId
    }

    // 키보드가 올라오면 메시지 영역(weight(1f))이 그만큼 줄어드는데, LazyColumn은 컨테이너가
    // 작아져도 스크롤 위치(offset)를 그대로 유지하므로 맨 아래를 보고 있던 채팅 로그가 줄어든
    // 화면 아래로 밀려나 잘려 보인다.
    //
    // isImeVisible(불리언) 전환 시점에 한 번만 스크롤하는 방식은 실제로는 잘 안 됐다 — 그 값이
    // true로 바뀌는 시점은 키보드 애니메이션이 "시작"할 때라 아직 영역이 줄어들기 전이고, 이후
    // 프레임마다 서서히 줄어드는 동안엔 다시 스크롤을 안 하니 결국 줄어든 만큼 잘려 보이는 건
    // 똑같았다. 대신 이 영역의 실제 레이아웃 높이가 바뀔 때마다(onSizeChanged, 키보드 애니메이션
    // 도중에도 프레임마다 호출됨) 맨 아래를 다시 스크롤해 맞춘다 — 애니메이션 전 구간에 걸쳐
    // 계속 바닥에 붙어 있는 것처럼 보인다.
    //
    // 단, 과거 메시지를 읽으려고 위로 스크롤해 둔 상태에서 크기만 바뀌는 경우(예: 회전)까지
    // 강제로 맨 아래로 끌고 가지 않도록, "직전에 맨 아래를 보고 있었는지"(isAtBottom)를 계속
    // 추적해두고 그때만 다시 스크롤한다.
    var isAtBottom by remember { mutableStateOf(true) }
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                isAtBottom = lastVisibleIndex == null || lastVisibleIndex >= listState.layoutInfo.totalItemsCount - 1
            }
    }

    // 답장으로 보낸 메시지의 인용 미리보기를 탭하면 원본 메시지로 스크롤한다. 서버가 내려주는
    // reply는 원본이 지금 로드된 목록에 없어도(과거 페이지 등) 항상 채워져 있으므로, targetIndex가
    // -1인 경우(원본이 아직 안 불러와진 페이지에 있음)는 실제로 발생할 수 있다 — 그때는 조용히
    // 아무 일도 하지 않는다(방어적 가드).
    val onReplyPreviewClick: (Long) -> Unit = { targetMessageId ->
        val targetIndex = messages.indexOfFirst { it.id == targetMessageId }
        if (targetIndex >= 0) {
            coroutineScope.launch {
                listState.animateScrollToItem(targetIndex)
            }
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
                .onSizeChanged {
                    if (hasScrolledToLatest && isAtBottom && messages.isNotEmpty()) {
                        coroutineScope.launch { listState.scrollToItem(messages.lastIndex) }
                    }
                }
                .background(LiroutiTheme.colors.backgroundSecondary),
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
                    // 맨 아래로의 초기 스크롤이 끝나기 전까지는 맨 위 상태가 잠깐 비치지 않도록 숨긴다.
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(if (hasScrolledToLatest) 1f else 0f),
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
                                replyPreview = message.replyPreview,
                                onReplySwipe = onReplySwipe,
                                onReplyPreviewClick = onReplyPreviewClick,
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                // ChatBar 자체는 좌우 16dp 여백을 두고 그 안쪽만 칠해서(피그마 pill 모양), 그
                // 여백(바깥쪽 양옆)은 이 배경이 그대로 비친다 — 메시지 영역과 같은 색으로 맞춘다.
                .background(LiroutiTheme.colors.backgroundSecondary)
                .navigationBarsPadding()
                .padding(bottom = 3.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // EmojiPannel을 실제로 연 적이 없어도 그 그리드 셀 크기를 미리 계산해 emojiSize에
            // 반영한다 — 안 그러면 상대가 이모티콘을 처음 보냈을 때 기본값(DefaultEmojiSize)으로
            // 작게 그려졌다가, 이모지 패널을 한 번 열어야(내가 보내거나) 정상 크기로 커지는
            // 버그가 있었다.
            EmojiCellSizeProbe(onMeasured = { emojiSize = it })

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
                isDaySelectable = { date -> date in chatDates },
                onDisplayedMonthChange = onCalendarMonthChange,
                onDateSelected = { date ->
                    isCalendarSheetVisible = false
                    calendarSelectedDate = date
                    // 이제 messages를 그 날짜가 로드돼 있길 바라며 로컬로 뒤지는 대신, 그 날짜를
                    // 새 앵커로 실제 서버 재조회를 요청한다(호출부가 목록을 통째로 교체함). 목록이
                    // 바뀌므로 "맨 아래로 스크롤" 이펙트도 새 목록 기준으로 다시 한 번 동작해야 해서
                    // hasScrolledToLatest를 리셋한다.
                    hasScrolledToLatest = false
                    onChatDateSelected(date)
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
