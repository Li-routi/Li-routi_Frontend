package com.li_routi.feature.home.component

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * 홈 화면의 "내 루틴" 카드 (Figma `comp/menulist`, node `2187:21658`).
 *
 * 구조: 아이콘 박스(`icon_range`) + 제목/부제 텍스트 + `chevron--right` 아이콘.
 */
@Composable
fun MyRoutineCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(LiroutiTheme.colors.backgroundDefault)
            .clickable(onClick = onClick)
            .padding(start = 12.dp, end = 16.dp, top = 24.dp, bottom = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(LiroutiTheme.colors.backgroundStrong),
            contentAlignment = Alignment.Center,
        ) {
            Image(
            painter = painterResource(id = R.drawable.calendar),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelStrong),
        )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        ) {
            Text(
                text = "내 루틴",
                style = LiroutiTheme.typography.body1,
                color = LiroutiTheme.colors.labelStrong,
            )
            Text(
                text = "내 루틴을 생성해보세요!",
                style = LiroutiTheme.typography.caption,
                color = LiroutiTheme.colors.labelInfo,
            )
        }
        Image(
            painter = painterResource(id = R.drawable.chevron__right),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelInfo),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyRoutineCardPreview() {
    LiroutiFrontendTheme {
        MyRoutineCard(onClick = {})
    }
}
