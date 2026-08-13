package com.li_routi.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.foundation.color.DragHandleColor
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

private val SheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
private val HandleColor = DragHandleColor

/**
 * 알림 더보기 → 삭제 바텀시트 (Figma `알림 삭제` / `4741:44644`).
 *
 * 핸들 + 「알림 삭제」행 + 「닫기」버튼.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDeleteBottomSheet(
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        shape = SheetShape,
        containerColor = LiroutiTheme.colors.backgroundDefault,
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .width(44.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(HandleColor),
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(LiroutiTheme.colors.backgroundAlternative)
                        .clickable {
                            onDeleteClick()
                            onDismissRequest()
                        }
                        .padding(horizontal = 12.dp, vertical = 11.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "알림 삭제",
                        style = LiroutiTheme.typography.body2LongRegular,
                        color = LiroutiTheme.colors.labelDefault,
                        textAlign = TextAlign.Center,
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                        .height(44.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(LiroutiTheme.colors.backgroundAlternative)
                        .clickable(onClick = onDismissRequest),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "닫기",
                        style = LiroutiTheme.typography.body2LongMedium,
                        color = LiroutiTheme.colors.labelDefault,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun NotificationDeleteBottomSheetPreview() {
    LiroutiFrontendTheme {
        NotificationDeleteBottomSheet(
            onDismissRequest = {},
            onDeleteClick = {},
        )
    }
}
