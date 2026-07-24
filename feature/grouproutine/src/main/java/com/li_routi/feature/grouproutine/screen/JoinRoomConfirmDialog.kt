package com.li_routi.feature.grouproutine.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

@Composable
fun JoinRoomConfirmDialog(
    roomName: String,
    memberCount: Int,
    totalRoutineCount: Int,
    onDismissRequest: () -> Unit,
    onJoinClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = LiroutiTheme.colors.backgroundDefault,
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Text(
                    text = "이 방에 참여할까요?",
                    style = LiroutiTheme.typography.heading2SemiBold,
                    color = LiroutiTheme.colors.labelDefault,
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LiroutiTheme.colors.backgroundFill, RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    AvatarStack(count = memberCount)
                    Text(
                        text = roomName,
                        style = LiroutiTheme.typography.body1SemiBold,
                        color = LiroutiTheme.colors.labelDefault,
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        JoinRoomInfoRow(label = "멤버", value = "${memberCount}명")
                        JoinRoomInfoRow(label = "전체 루틴", value = "${totalRoutineCount}개")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    JoinRoomDialogButton(
                        text = "취소",
                        onClick = onDismissRequest,
                        backgroundColor = LiroutiTheme.colors.backgroundAlternative,
                        textColor = LiroutiTheme.colors.labelDefault,
                        modifier = Modifier.weight(1f),
                    )
                    JoinRoomDialogButton(
                        text = "참여하기",
                        onClick = onJoinClick,
                        backgroundColor = LiroutiTheme.colors.primaryNormal,
                        textColor = LiroutiTheme.colors.backgroundAlternative,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun AvatarStack(
    count: Int,
    modifier: Modifier = Modifier,
) {
    val visibleCount = count.coerceIn(0, 3)
    Row(modifier = modifier) {
        repeat(visibleCount) { index ->
            Box(
                modifier = Modifier
                    .padding(start = if (index == 0) 0.dp else 20.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(LiroutiTheme.colors.borderStrong),
            )
        }
    }
}

@Composable
private fun JoinRoomInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = label,
            style = LiroutiTheme.typography.body3Regular,
            color = LiroutiTheme.colors.labelInfo,
        )
        Text(
            text = value,
            style = LiroutiTheme.typography.body3Regular,
            color = LiroutiTheme.colors.labelDefault,
        )
    }
}

@Composable
private fun JoinRoomDialogButton(
    text: String,
    onClick: () -> Unit,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = text, style = LiroutiTheme.typography.body2SemiBold, color = textColor)
    }
}

@Preview(showBackground = true)
@Composable
private fun JoinRoomConfirmDialogPreview() {
    LiroutiFrontendTheme {
        JoinRoomConfirmDialog(
            roomName = "갓생살자",
            memberCount = 3,
            totalRoutineCount = 7,
            onDismissRequest = {},
            onJoinClick = {},
        )
    }
}
