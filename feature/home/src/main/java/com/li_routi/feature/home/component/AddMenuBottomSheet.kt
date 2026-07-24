package com.li_routi.feature.home.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.DsPlaceholder
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * 홈 화면 상단 바 `+` 아이콘 탭으로 열리는 메뉴 바텀시트 (Figma `Bottom Sheet`, node `2156:37178`).
 *
 * "내 루틴 관리" / "방 만들기" / "초대코드로 참여" 3개 메뉴로 구성된다. 각 메뉴 탭 시 해당 콜백을 호출한
 * 뒤 시트를 닫는다. 실제 화면 전환은 콜백을 구현하는 다른 담당자가 처리한다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMenuBottomSheet(
    onDismissRequest: () -> Unit,
    onManageMyRoutineClick: () -> Unit,
    onCreateRoomClick: () -> Unit,
    onJoinRoomWithInviteCodeClick: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        containerColor = LiroutiTheme.colors.backgroundDefault,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                DsPlaceholder(
                    componentName = "Icon/close",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(onClick = onDismissRequest),
                )
            }
            AddMenuBottomSheetItem(
                label = "내 루틴 관리",
                onClick = { onManageMyRoutineClick(); onDismissRequest() },
            )
            AddMenuBottomSheetItem(
                label = "방 만들기",
                onClick = { onCreateRoomClick(); onDismissRequest() },
            )
            AddMenuBottomSheetItem(
                label = "초대코드로 참여",
                onClick = { onJoinRoomWithInviteCodeClick(); onDismissRequest() },
            )
        }
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
        DsPlaceholder(
            componentName = "Icon/chevron--right",
            modifier = Modifier.size(20.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddMenuBottomSheetItemPreview() {
    LiroutiFrontendTheme {
        Column {
            AddMenuBottomSheetItem(label = "내 루틴 관리", onClick = {})
            AddMenuBottomSheetItem(label = "방 만들기", onClick = {})
            AddMenuBottomSheetItem(label = "초대코드로 참여", onClick = {})
        }
    }
}
