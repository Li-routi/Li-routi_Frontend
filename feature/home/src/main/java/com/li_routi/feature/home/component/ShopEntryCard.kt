package com.li_routi.feature.home.component

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * 홈 화면의 "닉네임" 카드 (Figma `comp/myVehicle`).
 *
 * DS Tooltip 컴포넌트는 고정 문구라, 상태별 안내 문구는 동일 스타일의 말풍선으로 표시한다.
 */
@Composable
fun ShopEntryCard(
    nickname: String,
    tooltipMessage: String,
    onNavigateToShop: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(LiroutiTheme.colors.backgroundDefault)
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = nickname,
                style = LiroutiTheme.typography.heading2,
                color = LiroutiTheme.colors.labelStrong,
                modifier = Modifier.weight(1f),
            )
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(onClick = onNavigateToShop)
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
            painter = painterResource(id = R.drawable.store),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelStrong),
        )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "상점가기",
                    style = LiroutiTheme.typography.body2,
                    color = LiroutiTheme.colors.labelStrong,
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 256.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(LiroutiTheme.colors.labelDefault.copy(alpha = 0.88f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = tooltipMessage,
                    style = LiroutiTheme.typography.body2,
                    color = LiroutiTheme.colors.labelReverse,
                    textAlign = TextAlign.Center,
                )
            }
            // 캐릭터 illustration (비-DS 이미지 자산)
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LiroutiTheme.colors.backgroundAlternative),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ShopEntryCardEmptyPreview() {
    LiroutiFrontendTheme {
        ShopEntryCard(
            nickname = "닉네임",
            tooltipMessage = "상단 + 버튼을 눌러 나의 루틴을 생성해보세요!",
            onNavigateToShop = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ShopEntryCardWithGroupPreview() {
    LiroutiFrontendTheme {
        ShopEntryCard(
            nickname = "닉네임",
            tooltipMessage = "반가워요!",
            onNavigateToShop = {},
        )
    }
}
