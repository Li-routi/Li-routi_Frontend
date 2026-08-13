package com.li_routi.feature.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.foundation.color.MemberHeroGradientEnd
import com.li_routi.core.designsystem.foundation.color.RepresentativeBadgeBackground
import com.li_routi.core.designsystem.foundation.color.RepresentativeBadgeText
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/** Figma `comp/myVehicle` 그라데이션 끝색 `rgba(207,228,255,0.2)` — 그룹 루틴 화면과 동일한 값. */
private val CharacterCardGradientEnd = MemberHeroGradientEnd

// 닉네임 아래 남는 공간을 쓰되, 400.dp처럼 화면을 덮지 않게 상한을 둠.
// PNG는 400×400이고 그림은 그보다 작아서, 박스보다 실제 새는 조금 작게 보임
private val CharacterMaxSize = 320.dp

/**
 * 홈 화면의 닉네임/캐릭터/상점가기 영역 (Figma Design Page [1.1] `comp/myVehicle`).
 *
 * Figma는 이 영역이 바텀시트 상단까지 그라데이션이 꽉 차게 채워지지만, 콘텐츠(닉네임 줄 + 캐릭터)는
 * 세로로 가운데가 아니라 위쪽에 붙는다 — 상단 바로 아래 닉네임 줄이 바로 오고, 그 아래 캐릭터,
 * 나머지 여백은 바텀시트 쪽으로 남는다(Figma node 3962:11620 확인). 그래서 [modifier]로 상위
 * (HomeScreen)가 남는 공간을 그대로 채우도록 넘기고, 여기서는 fillMaxSize + Arrangement.Top으로
 * 받는다.
 */
@Composable
fun ShopEntryCard(
    nickname: String,
    onNavigateToShop: () -> Unit,
    modifier: Modifier = Modifier,
    showRepresentativeBadge: Boolean = false,
    /** 착용 중인 아이템 이미지. 겹칠 순서대로 들어옴 */
    equippedImageUrls: List<String> = emptyList(),
    /** 겹쳐 입기의 바탕이 되는 캐릭터 */
    characterRes: Int = R.drawable.default_character,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        LiroutiTheme.colors.backgroundDefault,
                        CharacterCardGradientEnd,
                    ),
                ),
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = nickname,
                    style = LiroutiTheme.typography.heading2,
                    color = LiroutiTheme.colors.labelDefault,
                    maxLines = 1,
                )
                if (showRepresentativeBadge) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "대표 배지",
                        // Figma badge: Caption Bold 11
                        style = LiroutiTheme.typography.captionSemiBold.copy(
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                        ),
                        color = RepresentativeBadgeText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(RepresentativeBadgeBackground)
                            .padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp),
                    )
                }
            }
            Row(
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .clickable(onClick = onNavigateToShop)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.store),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelDefault),
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "상점가기",
                    // Figma: Body3/Regular 14/22
                    style = LiroutiTheme.typography.body2LongRegular,
                    color = LiroutiTheme.colors.labelDefault,
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 별도 배경/카드 없이 그라데이션 위에 바로 얹힘
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            AvatarCharacter(
                equippedImageUrls = equippedImageUrls,
                characterRes = characterRes,
                modifier = Modifier
                    .sizeIn(maxWidth = CharacterMaxSize, maxHeight = CharacterMaxSize)
                    .fillMaxHeight()
                    .aspectRatio(1f),
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
            onNavigateToShop = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ShopEntryCardWithBadgePreview() {
    LiroutiFrontendTheme {
        ShopEntryCard(
            nickname = "닉네임",
            onNavigateToShop = {},
            showRepresentativeBadge = true,
        )
    }
}
