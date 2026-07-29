package com.li_routi.feature.login.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.login.component.SocialLoginButton

/**
 * Figma 로그인 화면(node 3731:69933) 기본 화면. 상태 바 아래는 빈 배경이고,
 * 하단에 카카오/구글 로그인 버튼 두 개만 놓인다.
 */
@Composable
fun LoginScreen(
    isLoading: Boolean,
    onKakaoClick: () -> Unit,
    onGoogleClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundDefault),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SocialLoginButton(
                text = "카카오로 시작하기",
                onClick = onKakaoClick,
                enabled = !isLoading,
            )
            SocialLoginButton(
                text = "Google로 시작하기",
                onClick = onGoogleClick,
                enabled = !isLoading,
            )
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
