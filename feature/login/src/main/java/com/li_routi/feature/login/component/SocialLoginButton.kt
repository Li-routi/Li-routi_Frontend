package com.li_routi.feature.login.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * Figma 로그인 화면(node 3731:69933) 하단 버튼 스펙(44dp/14sp Medium)에 맞춘 버튼.
 * [com.li_routi.core.designsystem.component.LiroutiPrimaryButton]은 48dp/16sp SemiBold로 스펙이 달라
 * 재사용 대신 같은 토큰으로 로컬 컴포넌트를 둔다.
 */
@Composable
fun SocialLoginButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (enabled) LiroutiTheme.colors.primaryNormal else LiroutiTheme.colors.primaryDisabled)
            .clickable(enabled = enabled, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = LiroutiTheme.typography.body2Medium,
            color = LiroutiTheme.colors.backgroundAlternative,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SocialLoginButtonPreview() {
    LiroutiFrontendTheme {
        androidx.compose.foundation.layout.Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SocialLoginButton(text = "카카오로 시작하기", onClick = {})
            SocialLoginButton(text = "Google로 시작하기", onClick = {})
        }
    }
}
