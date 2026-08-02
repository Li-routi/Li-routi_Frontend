package com.li_routi.feature.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * 알림/알림 설정 공용 상단 바.
 */
@Composable
fun NotificationTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSettingsClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Image(
            painter = painterResource(id = R.drawable.chevron__left),
            contentDescription = "뒤로가기",
            modifier = Modifier
                .size(20.dp)
                .clickable(onClick = onBackClick),
            colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelDefault),
        )
        Text(
            text = title,
            style = LiroutiTheme.typography.heading2,
            color = LiroutiTheme.colors.labelDefault,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )
        if (onSettingsClick != null) {
            Image(
                painter = painterResource(id = R.drawable.settings),
                contentDescription = "알림 설정",
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = onSettingsClick),
                colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelDefault),
            )
        } else {
            Spacer(modifier = Modifier.width(20.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationTopBarWithSettingsPreview() {
    LiroutiFrontendTheme {
        NotificationTopBar(
            title = "루틴 알림",
            onBackClick = {},
            onSettingsClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationTopBarBackOnlyPreview() {
    LiroutiFrontendTheme {
        NotificationTopBar(
            title = "알림 설정",
            onBackClick = {},
        )
    }
}
