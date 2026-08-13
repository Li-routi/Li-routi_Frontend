package com.li_routi.feature.home.component

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.foundation.color.NotificationUnreadBackground
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.home.vm.NotificationItemUiModel
import com.li_routi.feature.home.vm.NotificationTab

/**
 * 알림 목록 한 줄 (스크린샷 기준: 미읽음 점/배경, 카테고리·제목·시간, 3dot).
 */
@Composable
fun NotificationListItem(
    item: NotificationItemUiModel,
    onClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val background = if (item.isUnread) {
        NotificationUnreadBackground
    } else {
        LiroutiTheme.colors.backgroundDefault
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
                color = LiroutiTheme.colors.labelDefault,
            )
        }

        Image(
            painter = painterResource(id = R.drawable.overflow_menu__vertical),
            contentDescription = "더보기",
            modifier = Modifier
                .size(20.dp)
                .clickable(onClick = onMoreClick),
            colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelDefault),
        )
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
            onMoreClick = {},
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
            onMoreClick = {},
        )
    }
}
