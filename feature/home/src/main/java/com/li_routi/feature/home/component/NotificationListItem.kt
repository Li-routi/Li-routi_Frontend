package com.li_routi.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.foundation.color.NotificationUnreadBackground
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.home.vm.NotificationItemUiModel
import com.li_routi.feature.home.vm.NotificationTab

/**
 * 알림 목록 한 줄이다.
 *
 * 읽지 않은 알림만 점과 배경으로 강조한다. 읽은 알림도 관련 화면을 다시 열 수 있어야 하므로
 * 클릭은 유지하고 제목 색상만 낮춰 읽음 상태를 표현한다.
 */
@Composable
fun NotificationListItem(
    item: NotificationItemUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val background = if (item.isUnread) {
        NotificationUnreadBackground
    } else {
        LiroutiTheme.colors.backgroundDefault
    }
    val titleColor = if (item.isUnread) {
        LiroutiTheme.colors.labelDefault
    } else {
        LiroutiTheme.colors.labelInfo
    }
    // Figma Body4 13/16
    val metaStyle = LiroutiTheme.typography.body3Regular.copy(lineHeight = 16.sp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 19.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(width = 6.dp, height = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (item.isUnread) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(LiroutiTheme.colors.primaryNormal),
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.categoryLabel,
                    style = metaStyle,
                    color = LiroutiTheme.colors.labelInfo,
                )
                Text(
                    text = item.timeLabel,
                    style = metaStyle,
                    color = LiroutiTheme.colors.labelInfo,
                )
            }
            Text(
                text = item.title,
                // Figma Body3/Bold 14/22
                style = LiroutiTheme.typography.body2LongSemiBold,
                color = titleColor,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationListItemUnreadPreview() {
    LiroutiFrontendTheme {
        NotificationListItem(
            item = NotificationItemUiModel(
                id = "1",
                tab = NotificationTab.MyRoutine,
                categoryLabel = "내루틴 · 건강",
                title = "물 마시기",
                timeLabel = "오전 9:00",
                isUnread = true,
            ),
            onClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationListItemReadPreview() {
    LiroutiFrontendTheme {
        NotificationListItem(
            item = NotificationItemUiModel(
                id = "2",
                tab = NotificationTab.MyRoutine,
                categoryLabel = "내루틴 · 건강",
                title = "물 마시기",
                timeLabel = "오전 9:00",
                isUnread = false,
            ),
            onClick = {},
        )
    }
}
