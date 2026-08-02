package com.li_routi.feature.login.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme

/**
 * Figma 로그인 화면(node 3731:69933) 하단 버튼 스펙(44dp/14sp Medium)에 맞춘 버튼.
 * [com.li_routi.core.designsystem.component.LiroutiPrimaryButton]은 48dp/16sp SemiBold로 스펙이 달라
 * 재사용 대신 같은 토큰으로 로컬 컴포넌트를 둔다.
 * 아이콘 + 텍스트는 버튼 안에서 항상 가운데 정렬되므로, 글자 상자 크기를 하드코딩하지 않아도
 * 텍스트 길이/폰트 크기에 따라 자연스럽게 배치가 유지된다.
 */
@Composable
fun SocialLoginButton(
    text: String,
    iconRes: Int,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .clickable(enabled = enabled, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color.Unspecified,
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = textColor,
        )
    }
}

@Preview(showBackground = false)
@Composable
private fun SocialLoginButtonPreview() {
    LiroutiFrontendTheme {
        androidx.compose.foundation.layout.Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SocialLoginButton(
                text = "카카오로 시작하기",
                iconRes = R.drawable.kakao,
                backgroundColor = Color(0xFFFEE500),
                textColor = Color(0xFF171719),
                onClick = {},
            )
            SocialLoginButton(
                text = "Google로 시작하기",
                iconRes = R.drawable.google,
                backgroundColor = Color(0xFFFAFAFA),
                textColor = Color(0xFF171719),
                onClick = {},
            )
        }
    }
}
