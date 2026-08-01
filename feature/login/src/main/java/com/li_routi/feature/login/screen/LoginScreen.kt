package com.li_routi.feature.login.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.login.component.LoginPageText
import com.li_routi.feature.login.component.LoginProgressBar
import com.li_routi.feature.login.component.SocialLoginButton

private data class LoginPageContent(
    val title: String,
    val description: String,
)

private val LoginPages = listOf(
    LoginPageContent(
        title = "반복되는 루틴을 만들고,\n스와이프 한 번으로 인증해요",
        description = "오늘 할 일과 완료 현황을 한 화면에서 확인해요",
    ),
    LoginPageContent(
        title = "초대코드로 친구를 초대하고\n함께 루틴을 인증해요",
        description = "그룹방에서 서로의 진행 상황을 확인할 수 있어요",
    ),
    LoginPageContent(
        title = "다양한 챌린지에 참여하고\n꾸준함에 대한 리워드를 받아요",
        description = "다른 사용자들과 함께 목표를 인증하는 공간이에요",
    ),
    LoginPageContent(
        title = "연속 달성 기록이 쌓여\n나만의 업적이 돼요",
        description = "리포트로 루틴 습관을 한눈에 확인할 수 있어요",
    ),
)


@Composable
fun LoginScreen(
    isLoading: Boolean,
    onKakaoClick: () -> Unit,
    onGoogleClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { LoginPages.size })

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundDefault),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                // 버튼 영역을 제외한 이 안 어디를 스와이프해도 페이지가 넘어가도록,
                // 페이저 자체를 이 영역 전체 크기로 깔아둔다.
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                ) { page ->
                    val content = LoginPages[page]
                    Box(modifier = Modifier.fillMaxSize()) {
                        LoginPageText(
                            title = content.title,
                            description = content.description,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 94.dp)
                                .fillMaxWidth(),
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 240.dp)
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(Color(0xFFFAFAFA)),
                )
                LoginProgressBar(
                    currentPage = pagerState.currentPage,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 65.dp),
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding()
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                SocialLoginButton(
                    text = "카카오로 시작하기",
                    iconRes = R.drawable.kakao,
                    backgroundColor = Color(0xFFFEE500),
                    textColor = Color(0xFF171719),
                    onClick = onKakaoClick,
                    enabled = !isLoading,
                )
                SocialLoginButton(
                    text = "Google로 시작하기",
                    iconRes = R.drawable.google,
                    backgroundColor = Color(0xFFFAFAFA),
                    textColor = Color(0xFF171719),
                    onClick = onGoogleClick,
                    enabled = !isLoading,
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun LoginScreenPreview() {
    LiroutiFrontendTheme {
        LoginScreen(
            isLoading = false,
            onKakaoClick = {},
            onGoogleClick = {},
        )
    }
}
