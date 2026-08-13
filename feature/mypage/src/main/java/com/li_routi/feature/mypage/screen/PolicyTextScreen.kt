package com.li_routi.feature.mypage.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.mypage.component.EditProfileTopBar
import com.li_routi.feature.mypage.component.PolicyLine
import com.li_routi.feature.mypage.component.PolicyLineStyle
import com.li_routi.feature.mypage.component.TermsOfServiceLines

/**
 * "글만 쭉 보이는" 정책/약관류 화면 공용 템플릿. 이용약관/개인정보 처리방침/신고 및 운영
 * 정책/오픈소스 라이선스/코인 및 환불 정책이 전부 같은 레이아웃(상단 바 + 스크롤되는 본문
 * 텍스트)이라 하나로 구현했다 — Figma node `6008:18193`(이용약관) 기준이며 나머지 4개 문서도
 * 같은 구조([PolicyLine] 목록만 다름)를 공유한다.
 */
@Composable
fun PolicyTextScreen(
    topBarTitle: String,
    lines: List<PolicyLine>,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // 앱 타이포 스케일에 14sp/22 Bold 조합이 따로 없어서(SemiBold까지만 있음), 가장 가까운
    // body2LongSemiBold에서 굵기만 Bold로 덮어써 Figma 스펙(14/22/Bold)에 맞춘다.
    val headingStyle = LiroutiTheme.typography.body2LongSemiBold.copy(fontWeight = FontWeight.Bold)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundDefault),
    ) {
        EditProfileTopBar(title = topBarTitle, onBackClick = onBackClick)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 30.dp),
        ) {
            lines.forEach { line ->
                Text(
                    text = line.text,
                    style = when (line.style) {
                        PolicyLineStyle.Title -> LiroutiTheme.typography.heading2Bold
                        PolicyLineStyle.Heading -> headingStyle
                        PolicyLineStyle.Body -> LiroutiTheme.typography.body2LongRegular
                    },
                    color = LiroutiTheme.colors.labelDefault,
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun PolicyTextScreenPreview() {
    LiroutiFrontendTheme {
        PolicyTextScreen(
            topBarTitle = "이용 약관",
            lines = TermsOfServiceLines,
            onBackClick = {},
        )
    }
}
