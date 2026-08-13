package com.li_routi.feature.grouproutine.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.grouproutine.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

private val ChatBubbleTextColor = Color(0xFF000000)

private val LeftMargin = 16.dp
private val AvatarSize = 40.dp
private val AvatarToNicknameGap = 8.dp
private val NicknameHeight = 22.dp
private val NicknameToBubbleGap = 4.dp

// 왼쪽 여백(16) + 프로필(40) + 프로필-닉네임 간격(8) = 화면 왼쪽 기준 64dp 지점.
private val BubbleStartOffset = LeftMargin + AvatarSize + AvatarToNicknameGap

// 말풍선을 스와이프하면 답장 대상으로 지정한다 — 최대 64dp까지만 밀리고, 48dp를 넘겨야
// 답장이 확정된다(안 넘기고 손을 떼면 스프링으로 원위치 복귀). 상대 메시지는 왼쪽으로,
// 내 메시지는 반대로 오른쪽으로 밀어야 한다([replySwipeGesture]의 dragToRight).
private val ReplySwipeMaxOffset = 64.dp
private val ReplySwipeTriggerThreshold = 48.dp

/**
 * 채팅 메시지 한 건. [sentAtMillis]는 그룹핑(같은 분인지 판단)에만 쓰이고 화면엔 표시하지 않는다.
 *
 * [emojiUrl]이 있으면 이모티콘 메시지다 — 말풍선(텍스트) 없이 이모티콘 이미지만 그대로 보여주고,
 * 이때 [message]는 쓰이지 않는다(빈 문자열).
 *
 * [replyToMessageId]가 있으면 답장으로 보낸 메시지다. 보낸 사람이 채워 넣은 닉네임/미리보기
 * 텍스트는 신뢰하지 않고(변조 가능 — CodeRabbit 리뷰 지적) id만 들고 있다가, 실제 화면에 그릴
 * 때 [resolveReplyPreview]로 지금 받은 메시지 목록에서 진짜 내용을 찾아 보여준다.
 */
data class ChatMessageUiModel(
    val id: Long,
    val senderName: String,
    val message: String,
    val sentAtMillis: Long,
    val isMine: Boolean,
    val emojiUrl: String? = null,
    val replyToMessageId: Long? = null,
)

/** 답장으로 보낸 메시지가 인용하는, 검증된 원본 메시지 정보. [resolveReplyPreview]가 만든다. */
data class ChatReplyPreviewUiModel(
    val originalMessageId: Long,
    val senderName: String,
    val previewText: String,
)

/** 원본 메시지가 이모티콘이라 텍스트 미리보기가 없을 때 대신 보여줄 라벨. */
private const val EmojiReplyPreviewLabel = "이모티콘"

/**
 * [replyToMessageId]가 가리키는 원본 메시지를 [messages](지금까지 받은 전체 메시지)에서 찾아
 * 답장 인용을 만든다.
 *
 * 보낸 사람이 content에 실어 보낸 닉네임/미리보기 텍스트를 그대로 믿지 않는다 — 변조된
 * 클라이언트가 실제로 하지 않은 말을 한 것처럼 가짜 인용을 위조해 보낼 수 있기 때문이다
 * (CodeRabbit 리뷰 지적). 대신 [replyToMessageId]로 실제 수신한 메시지를 찾아, 그 메시지의
 * 진짜 보낸 사람/내용에서 미리보기를 만든다.
 *
 * 원본을 찾지 못하면(아직 안 불러온 과거 메시지 등, 검증 불가) null을 반환해 인용 없이 일반
 * 텍스트로 보여준다 — 나중에 그 메시지를 불러오면(과거 채팅 스크롤 등) 같은 화면이 다시 그려질
 * 때 자연히 인용이 나타난다.
 */
fun resolveReplyPreview(replyToMessageId: Long?, messages: List<ChatMessageUiModel>): ChatReplyPreviewUiModel? {
    if (replyToMessageId == null) return null
    val original = messages.firstOrNull { it.id == replyToMessageId } ?: return null
    return ChatReplyPreviewUiModel(
        originalMessageId = original.id,
        senderName = original.senderName,
        previewText = if (original.emojiUrl != null) EmojiReplyPreviewLabel else original.message,
    )
}

/**
 * 이 메시지 앞에 프로필/닉네임을 새로 보여줘야 하는지 판단한다.
 *
 * 직전 메시지와 보낸 사람이 다르거나(내 메시지 <-> 상대 메시지 전환 포함), 분(分)이 달라졌으면
 * 새 그룹으로 보고 true를 반환한다. 같은 사람이 같은 분 안에 연달아 보낸 메시지는 false가 되어
 * 프로필/닉네임 없이 말풍선만 이어붙는다.
 */
fun ChatMessageUiModel.isGroupStart(previous: ChatMessageUiModel?): Boolean {
    if (previous == null) return true
    if (previous.isMine != isMine) return true
    if (!isMine && previous.senderName != senderName) return true
    return previous.sentAtMillis / 60_000 != sentAtMillis / 60_000
}

/**
 * 이 메시지가 자신이 속한 그룹의 마지막 말풍선인지 판단한다 — [isGroupStart]와 반대 방향으로,
 * 다음 메시지와 비교한다. 같은 사람이 같은 분 안에 연달아 보낸 그룹에서는 마지막 말풍선에만
 * 시간(오후 1:00 등)을 붙여서 보여준다.
 */
fun ChatMessageUiModel.isGroupEnd(next: ChatMessageUiModel?): Boolean {
    if (next == null) return true
    if (next.isMine != isMine) return true
    if (!isMine && next.senderName != senderName) return true
    return next.sentAtMillis / 60_000 != sentAtMillis / 60_000
}

/** [sentAtMillis]를 기기 로컬 타임존 기준 날짜로 변환한다. */
internal fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

/** [sentAtMillis]를 "오후 1:00" 형식의 한국어 12시간제 시각으로 변환한다. */
internal fun Long.toKoreanTimeLabel(): String {
    val time = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalTime()
    val hour12 = when (val hour = time.hour % 12) {
        0 -> 12
        else -> hour
    }
    val period = if (time.hour < 12) "오전" else "오후"
    return "%s %d:%02d".format(period, hour12, time.minute)
}

/**
 * 이 메시지 앞에 날짜 구분선([ChatDateDivider])을 새로 보여줘야 하는지 판단한다.
 *
 * 이전 메시지가 없으면(=첫 채팅) true, 있으면 같은 날짜인지 비교해서 날짜가 하루라도
 * 달라졌을 때만 true를 반환한다.
 */
fun ChatMessageUiModel.isNewDate(previous: ChatMessageUiModel?): Boolean {
    if (previous == null) return true
    return previous.sentAtMillis.toLocalDate() != sentAtMillis.toLocalDate()
}

private val ChatDateDividerShape = RoundedCornerShape(6.dp)
private val ChatDateDividerBackground = Color(0xFF5D5D5D).copy(alpha = 0.60f)

// CSS padding: 8px 8px 8px 12px (top right bottom left)
private val ChatDateDividerPadding = PaddingValues(start = 12.dp, top = 8.dp, end = 8.dp, bottom = 8.dp)

// 위쪽은 LazyColumn의 verticalArrangement(spacedBy 10dp)가 이전 말풍선과의 간격을 이미
// 채워주고 있어서, 그 10dp를 뺀 나머지(10dp)만 여기서 더하면 합쳐서 20dp가 된다. 아래쪽은
// 같은 아이템(Column) 안에서 바로 다음 닉네임/말풍선이 이어지므로 spacedBy가 관여하지 않아
// 20dp를 그대로 준다.
private val ChatDateDividerTopPadding = 10.dp
private val ChatDateDividerBottomPadding = 20.dp

private val ChatTimestampGap = 4.dp
private val ChatTimestampTextStyle = TextStyle(
    fontSize = 11.sp,
    lineHeight = 14.sp,
)

/**
 * 채팅 목록 중간에 들어가는 날짜 구분선. [ChatMessageUiModel.isNewDate]가 true인 메시지
 * 바로 앞에 표시한다. 이전 그룹의 마지막 말풍선 기준 20dp 아래, 다음 그룹 첫 메시지의 닉네임
 * 기준 20dp 위에 오도록 [ChatDateDividerTopPadding]/[ChatDateDividerBottomPadding]으로 맞춘다.
 *
 * 배경은 반투명 회색 pill 모양이다 — 실제 CSS 시안엔 `backdrop-filter: blur(5px)`(뒤에 있는
 * 말풍선을 블러 처리)도 있지만, Compose 기본 `blur()`는 이 컴포저블 자기 자신(글씨 포함)을
 * 블러 처리해버려서 못 쓴다. 진짜 backdrop blur는 API 31+ 전용이거나 별도 라이브러리가 필요해서
 * (minSdk 24라 바로 못 씀) 반투명 배경색만으로 대체했다.
 */
@Composable
fun ChatDateDivider(sentAtMillis: Long, modifier: Modifier = Modifier) {
    val date = sentAtMillis.toLocalDate()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = ChatDateDividerTopPadding, bottom = ChatDateDividerBottomPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "${date.year}년 ${date.monthValue}월 ${date.dayOfMonth}일",
            style = LiroutiTheme.typography.captionMedium,
            color = Color.White,
            modifier = Modifier
                .clip(ChatDateDividerShape)
                .background(ChatDateDividerBackground)
                .padding(ChatDateDividerPadding),
        )
    }
}

/**
 * 답장 스와이프 제스처를 붙인다. [dragToRight]가 true면 오른쪽으로(내 메시지), false면
 * 왼쪽으로(상대 메시지) 밀 때만 [ReplySwipeTriggerThreshold]를 넘겨 [onReplySwipe]가 불린다.
 * 이모티콘 메시지는 답장에 실을 텍스트가 없으므로 스와이프 자체를 받지 않는다.
 */
@Composable
private fun Modifier.replySwipeGesture(
    message: ChatMessageUiModel,
    dragToRight: Boolean,
    onReplySwipe: (ChatMessageUiModel) -> Unit,
): Modifier {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    val maxOffsetPx = remember(density) { with(density) { ReplySwipeMaxOffset.toPx() } }
    val thresholdPx = remember(density) { with(density) { ReplySwipeTriggerThreshold.toPx() } }
    // message.id로 remember해야 리스트가 갱신돼도 이전 아이템의 드래그 상태가 새 메시지로 새지 않는다.
    val offsetX = remember(message.id) { Animatable(0f) }
    val minOffsetPx = if (dragToRight) 0f else -maxOffsetPx
    val maxOffsetLimitPx = if (dragToRight) maxOffsetPx else 0f

    return this
        .offset { IntOffset(offsetX.value.roundToInt(), 0) }
        .then(
            if (message.emojiUrl == null) {
                Modifier.pointerInput(message.id, dragToRight) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            val triggered = if (dragToRight) {
                                offsetX.value > thresholdPx
                            } else {
                                offsetX.value < -thresholdPx
                            }
                            coroutineScope.launch {
                                if (triggered) onReplySwipe(message)
                                offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                            }
                        },
                        onDragCancel = {
                            coroutineScope.launch { offsetX.animateTo(0f) }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            val next = (offsetX.value + dragAmount).coerceIn(minOffsetPx, maxOffsetLimitPx)
                            coroutineScope.launch { offsetX.snapTo(next) }
                        },
                    )
                }
            } else {
                Modifier
            },
        )
}

/**
 * 채팅 말풍선 한 줄.
 *
 * 내 메시지([ChatMessageUiModel.isMine])는 오른쪽 정렬 + 프로필 없이 말풍선만 나온다.
 * 상대 메시지는 [isGroupStart]가 true일 때만 왼쪽에 프로필(40x40)+닉네임을 보여주고,
 * 그 아래(화면 왼쪽 기준 64dp 지점)에서 말풍선이 시작된다. [isGroupStart]가 false면
 * 프로필/닉네임 없이 같은 64dp 지점에 말풍선만 이어붙는다 — 세로 간격은 이 컴포저블이 아니라
 * 메시지 리스트를 그리는 쪽(LazyColumn의 verticalArrangement)에서 10dp로 통일해서 준다.
 *
 * 두 종류 모두 스와이프하면 답장 대상으로 지정된다([replySwipeGesture]) — 상대 메시지는
 * 왼쪽으로, 내 메시지는 반대로 오른쪽으로 밀어야 한다. 말풍선을 꾹 누르면(길게 누르기)
 * "복사하기"/"답장하기" 메뉴가 뜨고([ChatMessageActionMenu]), 답장으로 보낸 메시지는 말풍선
 * 위에 원본 인용이 함께 보이며 탭하면 [onReplyPreviewClick]으로 원본 메시지 id를 알려준다.
 *
 * [replyPreview]는 호출부(RoomDetailScreen)가 [resolveReplyPreview]로 미리 계산해 넘긴다 —
 * [ChatMessageUiModel.replyToMessageId] 자체는 검증되지 않은 값이라 이 컴포저블에서 직접
 * 신뢰하지 않는다.
 *
 * [emojiSize]는 이모티콘 메시지([ChatMessageUiModel.emojiUrl])를 그릴 때 쓰는 크기로,
 * 이모지 패널에서 실제로 보였던 아이콘 크기를 그대로 넘겨받아 패널과 동일한 크기로 보이게 한다.
 */
@Composable
fun ChatBox(
    message: ChatMessageUiModel,
    isGroupStart: Boolean,
    modifier: Modifier = Modifier,
    isGroupEnd: Boolean = true,
    emojiSize: Dp = 40.dp,
    replyPreview: ChatReplyPreviewUiModel? = null,
    onReplySwipe: (ChatMessageUiModel) -> Unit = {},
    onReplyPreviewClick: (Long) -> Unit = {},
) {
    if (message.isMine) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(end = LeftMargin),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom,
        ) {
            Row(
                modifier = Modifier.replySwipeGesture(
                    message = message,
                    dragToRight = true,
                    onReplySwipe = onReplySwipe,
                ),
                verticalAlignment = Alignment.Bottom,
            ) {
                if (isGroupEnd) {
                    Text(
                        text = message.sentAtMillis.toKoreanTimeLabel(),
                        style = ChatTimestampTextStyle,
                        color = LiroutiTheme.colors.labelDefault,
                    )
                    Spacer(modifier = Modifier.width(ChatTimestampGap))
                }
                ChatMessageActionMenu(message = message, onReplySwipe = onReplySwipe) {
                    if (message.emojiUrl != null) {
                        AsyncImage(
                            model = message.emojiUrl,
                            contentDescription = "이모티콘",
                            modifier = Modifier.size(emojiSize),
                        )
                    } else {
                        ChatBubble(
                            text = message.message,
                            isMine = true,
                            replyPreview = replyPreview,
                            onReplyPreviewClick = onReplyPreviewClick,
                        )
                    }
                }
            }
        }
        return
    }

    Column(modifier = modifier.fillMaxWidth()) {
        if (isGroupStart) {
            Row(
                modifier = Modifier.padding(start = LeftMargin),
                verticalAlignment = Alignment.Top,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_group_routine_character),
                    contentDescription = null,
                    modifier = Modifier
                        .size(AvatarSize)
                        .clip(CircleShape)
                        .background(Color.White),
                )
                Spacer(modifier = Modifier.width(AvatarToNicknameGap))
                Box(
                    modifier = Modifier.height(NicknameHeight),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        text = message.senderName,
                        color = LiroutiTheme.colors.labelDefault,
                        style = LiroutiTheme.typography.captionMedium,
                    )
                }
            }
            Spacer(modifier = Modifier.height(NicknameToBubbleGap))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = BubbleStartOffset)
                .replySwipeGesture(
                    message = message,
                    dragToRight = false,
                    onReplySwipe = onReplySwipe,
                ),
            verticalAlignment = Alignment.Bottom,
        ) {
            ChatMessageActionMenu(message = message, onReplySwipe = onReplySwipe) {
                if (message.emojiUrl != null) {
                    AsyncImage(
                        model = message.emojiUrl,
                        contentDescription = "이모티콘",
                        modifier = Modifier.size(emojiSize),
                    )
                } else {
                    ChatBubble(
                        text = message.message,
                        isMine = false,
                        replyPreview = replyPreview,
                        onReplyPreviewClick = onReplyPreviewClick,
                    )
                }
            }
            if (isGroupEnd) {
                Spacer(modifier = Modifier.width(ChatTimestampGap))
                Text(
                    text = message.sentAtMillis.toKoreanTimeLabel(),
                    style = ChatTimestampTextStyle,
                    color = LiroutiTheme.colors.labelDefault,
                )
            }
        }
    }
}

/**
 * [content](말풍선 또는 이모티콘)를 꾹 누르면 "복사하기"/"답장하기" 팝업 메뉴를 띄운다.
 * 이모티콘 메시지는 복사할 텍스트가 없으므로 "복사하기"는 숨긴다.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChatMessageActionMenu(
    message: ChatMessageUiModel,
    onReplySwipe: (ChatMessageUiModel) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    var isMenuVisible by remember(message.id) { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current

    Box(modifier = modifier) {
        Box(
            modifier = Modifier.combinedClickable(
                onClick = {},
                onLongClick = { isMenuVisible = true },
            ),
        ) {
            content()
        }
        DropdownMenu(expanded = isMenuVisible, onDismissRequest = { isMenuVisible = false }) {
            if (message.emojiUrl == null) {
                DropdownMenuItem(
                    text = { Text("복사하기") },
                    onClick = {
                        clipboardManager.setText(AnnotatedString(message.message))
                        isMenuVisible = false
                    },
                )
            }
            DropdownMenuItem(
                text = { Text("답장하기") },
                onClick = {
                    onReplySwipe(message)
                    isMenuVisible = false
                },
            )
        }
    }
}

/**
 * [replyPreview]가 있으면 실제 텍스트 위에 원본 메시지 인용을 함께 보여준다 — [ChatBar.kt]의
 * 채팅바 윗상자(답장 작성 중 미리보기)와 같은 글꼴 스타일([ReplyNicknameTextStyle]/
 * [ReplyMessageTextStyle])을 재사용해 두 UI가 하나로 통일되어 보이게 한다.
 */
@Composable
private fun ChatBubble(
    text: String,
    isMine: Boolean,
    modifier: Modifier = Modifier,
    replyPreview: ChatReplyPreviewUiModel? = null,
    onReplyPreviewClick: (Long) -> Unit = {},
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isMine) LiroutiTheme.colors.primaryNormal else LiroutiTheme.colors.backgroundDefault)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        if (replyPreview != null) {
            ChatBubbleReplyQuote(
                replyPreview = replyPreview,
                isMine = isMine,
                onClick = onReplyPreviewClick,
            )
            Spacer(modifier = Modifier.height(6.dp))
        }
        Text(
            text = text,
            color = if (isMine) LiroutiTheme.colors.labelReverse else ChatBubbleTextColor,
            style = LiroutiTheme.typography.body3,
        )
    }
}

private val ChatBubbleReplyQuoteShape = RoundedCornerShape(4.dp)
private val ChatBubbleReplyQuoteTintOnPrimary = Color.White.copy(alpha = 0.16f)

/**
 * 답장으로 보낸 메시지의 말풍선 안, 실제 텍스트 위에 붙는 원본 메시지 인용 블록.
 * [replyPreview]는 [resolveReplyPreview]가 실제 수신한 메시지에서 검증해 만든 값이라 항상
 * [ChatReplyPreviewUiModel.originalMessageId]를 갖고 있으므로, 탭하면 바로 원본 메시지로
 * 이동할 수 있다.
 */
@Composable
private fun ChatBubbleReplyQuote(
    replyPreview: ChatReplyPreviewUiModel,
    isMine: Boolean,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(ChatBubbleReplyQuoteShape)
            .background(if (isMine) ChatBubbleReplyQuoteTintOnPrimary else LiroutiTheme.colors.backgroundFill)
            .clickable { onClick(replyPreview.originalMessageId) }
            .padding(horizontal = 8.dp, vertical = 6.dp),
    ) {
        Text(
            text = replyPreview.senderName,
            style = ReplyNicknameTextStyle,
            color = if (isMine) LiroutiTheme.colors.labelReverse else LiroutiTheme.colors.labelDefault,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = replyPreview.previewText,
            style = ReplyMessageTextStyle,
            color = if (isMine) {
                LiroutiTheme.colors.labelReverse.copy(alpha = 0.8f)
            } else {
                LiroutiTheme.colors.labelSub
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}