package com.li_routi.feature.grouproutine.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

private val PlaceholderColor = Color(0xFF878A93)
private val SendButtonBackground = Color(0xFFD6E8FF)
private val ChatBarHorizontalMargin = 16.dp

private val ChatBarTextStyle = TextStyle(
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 21.98.sp,
    letterSpacing = 0.sp,
)

/**
 * 모임방 상세 하단 채팅바 (텍스트 입력 + 이모티콘 버튼 + 전송 버튼).
 *
 * [onEmojiClick]은 추후 이모티콘 선택 창을 띄우는 용도, [onSendClick]은 메시지 전송용으로
 * 화면에서 훅을 걸 수 있도록 분리해뒀다. 지금은 실제 전송/이모티콘 로직이 없어 기본값은 no-op.
 *
 * 이모지 패널이 열려 있는 동안([isEmojiPanelOpen])에도 텍스트 필드는 포커스를 유지한 채
 * 소프트 키보드만 내려간 상태다. 이 상태에서 입력창을 다시 탭하면 [onTextFieldTap]이 호출되고,
 * 화면 쪽에서 키보드를 다시 띄우고 패널을 닫는 처리를 하도록 훅으로 분리했다.
 */
@Composable
fun ChatBar(
    message: String,
    onMessageChange: (String) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
    isEmojiPanelOpen: Boolean = false,
    onFocusChanged: (Boolean) -> Unit = {},
    onTextFieldTap: () -> Unit = {},
    onEmojiClick: () -> Unit = {},
    onSendClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .padding(horizontal = ChatBarHorizontalMargin)
            .fillMaxWidth()
            .height(44.dp)
            .background(LiroutiTheme.colors.backgroundFill),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart,
            ) {
                BasicTextField(
                    value = message,
                    onValueChange = onMessageChange,
                    modifier = Modifier
                        .fillMaxSize()
                        .focusRequester(focusRequester)
                        .onFocusChanged { onFocusChanged(it.isFocused) },
                    singleLine = true,
                    textStyle = ChatBarTextStyle.copy(color = LiroutiTheme.colors.labelDefault),
                    cursorBrush = SolidColor(LiroutiTheme.colors.primaryNormal),
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (message.isEmpty()) {
                                Text(
                                    text = "메세지 보내기",
                                    style = ChatBarTextStyle,
                                    color = PlaceholderColor,
                                )
                            }
                            innerTextField()
                        }
                    },
                )

                // 이모지 패널이 열려 있을 땐 텍스트 필드 위에 투명 스크림을 덮어, 탭했을 때
                // 커서 이동 대신 "키보드로 전환" 요청만 올라가게 한다 (포커스는 계속 유지됨).
                if (isEmojiPanelOpen) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onTextFieldTap,
                            ),
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            Image(
                painter = painterResource(id = R.drawable.smile),
                contentDescription = "이모티콘",
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = onEmojiClick),
            )

            Spacer(modifier = Modifier.width(6.dp))

            Box(
                modifier = Modifier
                    .size(width = 36.dp, height = 28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(SendButtonBackground)
                    .clickable(onClick = onSendClick),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.send__alt__filled_blue),
                    contentDescription = "전송",
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatBarPreview() {
    LiroutiFrontendTheme {
        var message by remember { mutableStateOf("") }
        ChatBar(
            message = message,
            onMessageChange = { message = it },
            focusRequester = remember { FocusRequester() },
        )
    }
}