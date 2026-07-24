package com.li_routi.feature.grouproutine.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.LiroutiBottomSheetCloseButton
import com.li_routi.core.designsystem.component.LiroutiChevronLeftIcon
import com.li_routi.core.designsystem.component.LiroutiPrimaryButton
import com.li_routi.core.designsystem.component.LiroutiTextField
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

private const val RoomNameMaxLength = 20

@Composable
fun MakeRoomScreen(
    roomName: String,
    onRoomNameChange: (String) -> Unit,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundDefault)
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LiroutiChevronLeftIcon(
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = onBackClick),
                color = LiroutiTheme.colors.labelDefault,
            )
            Text(
                text = "방 만들기",
                style = LiroutiTheme.typography.heading2SemiBold,
                color = LiroutiTheme.colors.labelDefault,
            )
            LiroutiBottomSheetCloseButton(onClick = onCloseClick)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "어떤 방을 만들까요?",
            style = LiroutiTheme.typography.heading2SemiBold,
            color = LiroutiTheme.colors.labelDefault,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "친구들이 방 목록에서 이 이름으로 보게 돼요.",
            style = LiroutiTheme.typography.body2LongRegular,
            color = LiroutiTheme.colors.labelSub,
        )

        Spacer(modifier = Modifier.height(24.dp))

        LiroutiTextField(
            value = roomName,
            onValueChange = { onRoomNameChange(it.take(RoomNameMaxLength)) },
            placeholder = "최대 20자",
            labelText = "방이름",
            showHelper = false,
        )

        Spacer(modifier = Modifier.weight(1f))

        LiroutiPrimaryButton(
            text = "다음",
            enabled = roomName.isNotBlank(),
            onClick = onNextClick,
            modifier = Modifier.padding(bottom = 16.dp),
        )
    }
}

@Preview(showBackground = true, heightDp = 500)
@Composable
private fun MakeRoomScreenPreview() {
    var roomName by remember { mutableStateOf("") }
    LiroutiFrontendTheme {
        MakeRoomScreen(
            roomName = roomName,
            onRoomNameChange = { roomName = it },
            onNextClick = {},
            onBackClick = {},
            onCloseClick = {},
        )
    }
}
