package com.li_routi.feature.grouproutine.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.grouproutine.R
import java.util.Calendar

private val ChatBubbleTextColor = Color(0xFF000000)

private val LeftMargin = 16.dp
private val AvatarSize = 40.dp
private val AvatarToNicknameGap = 8.dp
private val NicknameHeight = 22.dp
private val NicknameToBubbleGap = 4.dp

// 왼쪽 여백(16) + 프로필(40) + 프로필-닉네임 간격(8) = 화면 왼쪽 기준 64dp 지점.
private val BubbleStartOffset = LeftMargin + AvatarSize + AvatarToNicknameGap

/**
 * 채팅 메시지 한 건. [sentAtMillis]는 그룹핑(같은 분인지 판단)에만 쓰이고 화면엔 표시하지 않는다.
 *
 * [emojiResId]가 있으면 이모티콘 메시지다 — 말풍선(텍스트) 없이 이모티콘 이미지만 그대로 보여주고,
 * 이때 [message]는 쓰이지 않는다(빈 문자열).
 */
data class ChatMessageUiModel(
    val id: Long,
    val senderName: String,
    val message: String,
    val sentAtMillis: Long,
    val isMine: Boolean,
    val emojiResId: Int? = null,
)

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
    return previous.sentAtMillis.toMinuteOfHour() != sentAtMillis.toMinuteOfHour()
}

private fun Long.toMinuteOfHour(): Int =
    Calendar.getInstance().apply { timeInMillis = this@toMinuteOfHour }.get(Calendar.MINUTE)

/**
 * 채팅 말풍선 한 줄.
 *
 * 내 메시지([ChatMessageUiModel.isMine])는 오른쪽 정렬 + 프로필 없이 말풍선만 나온다.
 * 상대 메시지는 [isGroupStart]가 true일 때만 왼쪽에 프로필(40x40)+닉네임을 보여주고,
 * 그 아래(화면 왼쪽 기준 64dp 지점)에서 말풍선이 시작된다. [isGroupStart]가 false면
 * 프로필/닉네임 없이 같은 64dp 지점에 말풍선만 이어붙는다 — 세로 간격은 이 컴포저블이 아니라
 * 메시지 리스트를 그리는 쪽(LazyColumn의 verticalArrangement)에서 10dp로 통일해서 준다.
 *
 * [emojiSize]는 이모티콘 메시지([ChatMessageUiModel.emojiResId])를 그릴 때 쓰는 크기로,
 * 이모지 패널에서 실제로 보였던 아이콘 크기를 그대로 넘겨받아 패널과 동일한 크기로 보이게 한다.
 */
@Composable
fun ChatBox(
    message: ChatMessageUiModel,
    isGroupStart: Boolean,
    modifier: Modifier = Modifier,
    emojiSize: Dp = 40.dp,
) {
    if (message.isMine) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(end = LeftMargin),
            horizontalArrangement = Arrangement.End,
        ) {
            if (message.emojiResId != null) {
                Image(
                    painter = painterResource(id = message.emojiResId),
                    contentDescription = "이모티콘",
                    modifier = Modifier.size(emojiSize),
                )
            } else {
                ChatBubble(text = message.message)
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
                .padding(start = BubbleStartOffset),
        ) {
            ChatBubble(text = message.message)
        }
    }
}

@Composable
private fun ChatBubble(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = ChatBubbleTextColor,
        style = LiroutiTheme.typography.body3,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(LiroutiTheme.colors.backgroundDefault)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}