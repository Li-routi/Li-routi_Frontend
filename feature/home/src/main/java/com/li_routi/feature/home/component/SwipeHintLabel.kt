package com.li_routi.feature.home.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.DsPlaceholder
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * 홈 화면 "‹ 밀어서 빠른 인증" 안내 라벨 (Figma `Chevron`, node `2221:28481`).
 *
 * 루틴 또는 그룹 루틴방이 있을 때 표시한다. 화면을 왼쪽→오른쪽으로 스와이프하면
 * 카메라(빠른 인증) 페이지로 이동할 수 있음을 안내한다.
 */
@Composable
fun SwipeHintLabel(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DsPlaceholder(
            componentName = "Icon/chevron--left",
            modifier = Modifier.size(16.dp),
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
