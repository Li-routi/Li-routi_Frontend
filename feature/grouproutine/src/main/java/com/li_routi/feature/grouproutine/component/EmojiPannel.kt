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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

private const val EmojiGridColumns = 4
private const val EmojiGridRows = 2

/**
 * 채팅바의 이모지 버튼을 눌렀을 때 키보드 대신 뜨는 패널.
 *
 * 최상단엔 너비만 패널을 따라가는(fillMaxWidth) 투명한 24dp 높이의 헤더 바를 두고,
 * 그 중앙에 드래그 핸들 아이콘([R.drawable.header])을 놓는다.
 *
 * 그 아래엔 이모티콘을 4x2로 빈틈없이 배치한다 — 좌우 끝은 패널 양옆에서 10dp, 첫 줄은 헤더에서
 * 20dp 떨어지고, 그 안에서는 아이콘끼리 간격 없이 꽉 채운다. 아이콘 한 칸의 크기는 고정값이 아니라
 * `(패널 너비 - 20dp) / 4`로 정해져서, 패널 너비가 바뀌면 아이콘 크기도 같이 바뀐다.
 * 탭하면 어떤 칸을 눌렀는지 [onEmojiSelected]에 그 인덱스(현재는 항상 0)로 알려준다 — 지금은
 * 이모티콘이 1개뿐이지만, 나중에 칸마다 다른 이모티콘이 채워져도 시그니처를 바꿀 필요 없이
 * 인덱스만으로 어떤 이모티콘인지 구분할 수 있게 미리 (Int) -> Unit으로 잡아뒀다. 실제로
 * 채팅창에 반영하는 처리는 [ChatBar]/화면 쪽에서 담당한다.
 */
@Composable
fun EmojiPannel(
    modifier: Modifier = Modifier,
    onEmojiSelected: (emojiId: Int) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
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
                        if (cellIndex == 0) {
                            Image(
                                painter = painterResource(id = R.drawable.emoji),
                                contentDescription = "이모티콘",
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clickable(onClick = { onEmojiSelected(cellIndex) }),
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
        EmojiPannel()
    }
}