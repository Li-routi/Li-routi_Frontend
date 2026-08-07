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
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

private const val EmojiGridColumns = 4
private const val EmojiGridRows = 2

/** [emojiId](=칸 번호)에 대응하는 이모티콘 드로어블. 아직 칸 0에만 실제 이모티콘이 있다. */
internal fun emojiDrawableRes(emojiId: Int): Int? = when (emojiId) {
    0 -> R.drawable.emoji
    else -> null
}

@Composable
fun EmojiPannel(
    modifier: Modifier = Modifier,
    height: Dp = 250.dp,
    onCellSizeMeasured: (Dp) -> Unit = {},
    onEmojiSelected: (emojiId: Int) -> Unit = {},
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
                        val emojiResId = emojiDrawableRes(cellIndex)
                        if (emojiResId != null) {
                            Image(
                                painter = painterResource(id = emojiResId),
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