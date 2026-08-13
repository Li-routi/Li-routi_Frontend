package com.li_routi.feature.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.foundation.color.DragHandleColor
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/** Figma Bottom Sheet `home` variant — top radius 20. */
private val SheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

/** Figma Header drag handle `#DEDEDE`, 44×4. */
private val SheetDragHandleColor = DragHandleColor

/**
 * Figma Design Page [1.1] Home `+` 클릭 Bottom Sheet (`property1=home`, node `4766:60645`).
 *
 * 드래그 핸들 + 메뉴 3행(회색 카드). 상단 타이틀/설명 문구 없음.
 * [LiroutiBottomSheet]는 nav inset을 조절할 수 없어 edge-to-edge에서 하단이 잘리므로
 * 홈 전용으로 [ModalBottomSheet]를 직접 구성한다.
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
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        shape = SheetShape,
        containerColor = LiroutiTheme.colors.backgroundDefault,
        contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 0.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(SheetDragHandleColor),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 24.dp, end = 24.dp, top = 14.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
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
                // Figma Body3/Bold 14/22
                style = LiroutiTheme.typography.body2LongSemiBold.copy(fontWeight = FontWeight.Bold),
                color = LiroutiTheme.colors.labelDefault,
            )
            Text(
                text = description,
                // Figma Body4/Regular 13/16
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
