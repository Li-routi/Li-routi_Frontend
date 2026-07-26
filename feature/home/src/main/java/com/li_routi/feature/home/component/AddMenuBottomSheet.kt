package com.li_routi.feature.home.component

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.LiroutiBottomSheet
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * 홈 화면 상단 바 `+` 아이콘 탭으로 열리는 메뉴 바텀시트 (Figma `Bottom Sheet`, node `2156:37178`).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMenuBottomSheet(
    onDismissRequest: () -> Unit,
    onManageMyRoutineClick: () -> Unit,
    onCreateRoomClick: () -> Unit,
    onJoinRoomWithInviteCodeClick: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    LiroutiBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
    ) {
        AddMenuBottomSheetItem(
            label = "내 루틴 관리",
            onClick = {
                onManageMyRoutineClick()
                onDismissRequest()
            },
        )
        AddMenuBottomSheetItem(
            label = "방 만들기",
            onClick = {
                onCreateRoomClick()
                onDismissRequest()
            },
        )
        AddMenuBottomSheetItem(
            label = "초대코드로 참여",
            onClick = {
                onJoinRoomWithInviteCodeClick()
                onDismissRequest()
            },
        )
    }
}

@Composable
private fun AddMenuBottomSheetItem(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = LiroutiTheme.typography.body1,
            color = LiroutiTheme.colors.labelStrong,
            modifier = Modifier.weight(1f),
        )
        Image(
            painter = painterResource(id = R.drawable.chevron__right),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelDefault),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddMenuBottomSheetItemPreview() {
    LiroutiFrontendTheme {
        AddMenuBottomSheetItem(label = "내 루틴 관리", onClick = {})
    }
}
