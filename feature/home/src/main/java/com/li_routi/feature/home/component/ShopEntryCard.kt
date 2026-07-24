package com.li_routi.feature.home.component

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.DsPlaceholder
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * 홈 화면의 "닉네임" 카드 (Figma `comp/myVehicle`).
 *
 * 상단 Row: 닉네임 + 상점가기(아이콘 `store` + 텍스트).
 * 하단: 상태별 안내 문구 + 캐릭터 illustration(비-DS 이미지 자산 placeholder).
 *
 * Figma Tooltip은 Design System instance라 스타일 구현은 [DsPlaceholder]로 두고,
 * 화면 상태별로 달라지는 **문구만** 실제 텍스트로 표시한다(나중에 DS Tooltip로 교체).
 *
 * @param tooltipMessage 캐릭터 위 안내 문구. 홈 상태에 따라 달라진다.
 * - 처음 진입: "상단 + 버튼을 눌러 나의 루틴을 생성해보세요!"
 * - 내 루틴 O / 그룹방 X: "상단 + 버튼을 눌러 친구와 방을 만들어봐요."
 * - 내 루틴 O / 그룹방 O: "반가워요!"
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
                DsPlaceholder(
                    componentName = "Icon/store",
                    modifier = Modifier.size(16.dp),
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
            // TODO(design-system): Figma Tooltip 완성 시 DsPlaceholder + 문구를 실제 Tooltip으로 교체
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
            // 캐릭터 illustration (비-DS 이미지 자산) — 실제 에셋 반영은 이번 범위 제외
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
