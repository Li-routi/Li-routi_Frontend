package com.li_routi.feature.login.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme

private val TitleStyle = TextStyle(
    fontWeight = FontWeight.Bold,
    fontSize = 20.sp,
    lineHeight = 28.sp,
    letterSpacing = (-0.025).em,
    textAlign = TextAlign.Center,
)

private val DescriptionStyle = TextStyle(
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 22.sp,
    letterSpacing = (-0.025).em,
    textAlign = TextAlign.Center,
)

/**
 * 로그인 화면 상단 페이지별 소개 문구. 줄바꿈 위치는 title/description 문자열에 담긴 '\n'으로
 * 결정되고, 상자 크기를 고정하지 않아 텍스트 길이가 바뀌어도 자연스럽게 늘어난다.
 */
@Composable
fun LoginPageText(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = title, style = TitleStyle)
        Text(text = description, style = DescriptionStyle)
    }
}

@Preview(showBackground = false)
@Composable
private fun LoginPageTextPreview() {
    LiroutiFrontendTheme {
        LoginPageText(
            title = "반복되는 루틴을 만들고,\n스와이프 한 번으로 인증해요",
            description = "오늘 할 일과 완료 현황을 한 화면에서 확인해요",
        )
    }
}