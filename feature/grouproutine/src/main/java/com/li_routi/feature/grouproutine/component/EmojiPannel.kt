package com.li_routi.feature.grouproutine.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

private const val EmojiGridColumns = 4
private const val EmojiGridRows = 2

/** 채팅 이모티콘 하나(서버 `GET /api/chat/emoticons` 응답을 UI용으로 변환한 값). */
data class ChatEmoticonUiModel(
    val id: Long,
    val code: String,
    val assetUrl: String,
)

/** [emoticons]가 8개(4x2)를 넘으면 스크롤 없이 앞 8개만 보여준다 — 패널 레이아웃이 고정 그리드라서. */
@Composable
fun EmojiPannel(
    emoticons: List<ChatEmoticonUiModel>,
    modifier: Modifier = Modifier,
    height: Dp = 250.dp,
    onCellSizeMeasured: (Dp) -> Unit = {},
    onEmojiSelected: (ChatEmoticonUiModel) -> Unit = {},
) {
    val density = LocalDensity.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(LiroutiTheme.colors.backgroundFill),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.header),
                contentDescription = null,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 10.dp, end = 10.dp),
        ) {
            repeat(EmojiGridRows) { rowIndex ->

                Row(modifier = Modifier.fillMaxWidth()) {
                    repeat(EmojiGridColumns) { columnIndex ->
                        val cellIndex = rowIndex * EmojiGridColumns + columnIndex
                        val emoticon = emoticons.getOrNull(cellIndex)
                        if (emoticon != null) {
                            AsyncImage(
                                model = emoticon.assetUrl,
                                contentDescription = "이모티콘",
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    // 실제 렌더링된 셀 크기를 재서 알려준다 — 채팅으로 전송된 이모티콘도
                                    // 이 크기 그대로 보이게 하기 위함(패널 너비에 따라 달라지는 값이라
                                    // 고정 dp로 미리 정해둘 수 없다).
                                    .onGloballyPositioned { coordinates ->
                                        onCellSizeMeasured(with(density) { coordinates.size.width.toDp() })
                                    }
                                    .clickable(onClick = { onEmojiSelected(emoticon) }),
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmojiPannelPreview() {
    LiroutiFrontendTheme {
        EmojiPannel(emoticons = emptyList())
    }
}
