package com.li_routi.feature.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.LiroutiBottomSheet
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/** Figma `3647:46572` Bottom Sheet — 홈 `+` 메뉴. */
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
        // Figma: px 24, pb 32 (핸들 영역은 시트 기본 헤더)
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 32.dp, top = 0.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "루틴을 시작해볼까요?",
                    style = LiroutiTheme.typography.body1SemiBold.copy(
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                    ),
                    color = LiroutiTheme.colors.labelDefault,
                )
                Text(
                    text = "아래에서 원하는 방식을 선택하세요",
                    style = LiroutiTheme.typography.body3Regular.copy(lineHeight = 16.sp),
                    color = LiroutiTheme.colors.labelSub,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                AddMenuBottomSheetItem(
                    label = "내 루틴 관리",
                    description = "나만의 루틴을 만들고 관리해요",
                    onClick = {
                        onManageMyRoutineClick()
                        onDismissRequest()
                    },
                )
                AddMenuBottomSheetItem(
                    label = "방 만들기",
                    description = "친구들과 함께할 방을 새로 만들어요",
                    onClick = {
                        onCreateRoomClick()
                        onDismissRequest()
                    },
                )
                AddMenuBottomSheetItem(
                    label = "초대코드로 참여",
                    description = "받은 코드로 방에 바로 들어가요",
                    onClick = {
                        onJoinRoomWithInviteCodeClick()
                        onDismissRequest()
                    },
                )
            }
        }
    }
}

@Composable
private fun AddMenuBottomSheetItem(
    label: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(LiroutiTheme.colors.backgroundFill)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = LiroutiTheme.typography.body2LongSemiBold,
                color = LiroutiTheme.colors.labelDefault,
            )
            Text(
                text = description,
                style = LiroutiTheme.typography.body3Regular.copy(lineHeight = 16.sp),
                color = LiroutiTheme.colors.labelInfo,
            )
        }
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
        AddMenuBottomSheetItem(
            label = "내 루틴 관리",
            description = "나만의 루틴을 만들고 관리해요",
            onClick = {},
        )
    }
}
