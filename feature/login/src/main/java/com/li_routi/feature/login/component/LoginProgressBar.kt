package com.li_routi.feature.login.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme

private const val BarCount = 4
private val BarWidth = 76.dp
private val BarHeight = 4.dp
private val BarSpacing = 8.dp
private val BarActiveColor = Color(0xFF338AFF)
private val BarInactiveColor = Color(0xFFDBDCDF)

/**
 * 로그인 화면 상단 페이지 진행 바. currentPage(0-based) 이하 인덱스는 파란색으로 채워지고,
 * 뒤로 스와이프해서 currentPage가 줄어들면 그만큼 다시 회색으로 돌아간다.
 */
@Composable
fun LoginProgressBar(
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(BarSpacing),
    ) {
        repeat(BarCount) { index ->
            Box(
                modifier = Modifier
                    .size(width = BarWidth, height = BarHeight)
                    .clip(RoundedCornerShape(BarHeight / 2))
                    .background(if (index <= currentPage) BarActiveColor else BarInactiveColor),
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
private fun LoginProgressBarPreview() {
    LiroutiFrontendTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LoginProgressBar(currentPage = 0)
            LoginProgressBar(currentPage = 1)
            LoginProgressBar(currentPage = 2)
            LoginProgressBar(currentPage = 3)
        }
    }
}