package com.li_routi.feature.login.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ri_routi.AstaSans
import com.li_routi.core.designsystem.foundation.color.Neutral10
import com.li_routi.core.designsystem.foundation.color.Neutral60
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.feature.login.component.LoadingBird

private val GuideTitleStyle = TextStyle(
    color = Neutral10,
    fontFamily = AstaSans,
    fontSize = 20.sp,
    fontWeight = FontWeight.Bold,
    lineHeight = 30.sp,
    letterSpacing = (-0.1).sp,
)

private val GuideDescriptionStyle = TextStyle(
    color = Neutral60,
    textAlign = TextAlign.Center,
    fontFamily = AstaSans,
    fontSize = 14.sp,
    fontWeight = FontWeight.Normal,
    lineHeight = 22.sp,
    letterSpacing = (-0.35).sp,
)

private val LoadingBirdBoxTop = 170.dp
private val LoadingBirdBoxBottom = 430.dp
private val LoadingBirdBoxHorizontal = 80.dp
private val GuideTextTopSpacing = 21.dp
private val GuideTextGap = 4.dp

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
) {
    var containerHeightPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .onGloballyPositioned { coordinates ->
                containerHeightPx = coordinates.size.height
            },
    ) {
        // LoadingBird 박스의 하단 경계선(화면 상단 기준 y좌표). 화면 높이에 따라 박스 높이가
        // 달라지므로, 그 아래에 배치되는 안내 문구도 이 값을 기준으로 위치를 계산한다.
        val containerHeight = with(density) { containerHeightPx.toDp() }
        // 첫 레이아웃 패스가 끝나기 전(containerHeightPx == 0)에는 음수가 나올 수 있어 0으로 clamp한다.
        val loadingBirdBoxBottomFromTop = (containerHeight - LoadingBirdBoxBottom).coerceAtLeast(0.dp)

        LoadingBird(
            modifier = Modifier
                .padding(
                    top = LoadingBirdBoxTop,
                    bottom = LoadingBirdBoxBottom,
                    start = LoadingBirdBoxHorizontal,
                    end = LoadingBirdBoxHorizontal,
                )
                .fillMaxSize(),
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = loadingBirdBoxBottomFromTop + GuideTextTopSpacing),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(GuideTextGap),
        ) {
            Text(
                text = "잠시만 기다려 주세요",
                style = GuideTitleStyle,
            )
            Text(
                text = "해당 페이지로 이동 중입니다",
                style = GuideDescriptionStyle,
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun LoadingScreenPreview() {
    LiroutiFrontendTheme {
        LoadingScreen()
    }
}