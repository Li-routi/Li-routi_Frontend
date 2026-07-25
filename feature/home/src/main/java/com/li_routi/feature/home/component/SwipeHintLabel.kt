package com.li_routi.feature.home.component

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * 홈 화면 "‹ 밀어서 빠른 인증" 안내 라벨 (Figma `Chevron`, node `2221:28481`).
 */
@Composable
fun SwipeHintLabel(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = R.drawable.chevron__left),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelStrong),
        )
        Text(
            text = "밀어서 빠른 인증",
            style = LiroutiTheme.typography.body2,
            color = LiroutiTheme.colors.labelStrong,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SwipeHintLabelPreview() {
    LiroutiFrontendTheme {
        SwipeHintLabel()
    }
}
