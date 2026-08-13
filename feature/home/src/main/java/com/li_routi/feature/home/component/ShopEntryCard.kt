package com.li_routi.feature.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/** Design Page [1.1] `badge/rectangle_large` — 대표 배지. */
private val RepresentativeBadgeBackground = Color(0xFFF1F8E9)
private val RepresentativeBadgeText = Color(0xFF3A8009)

/** Figma `comp/myVehicle` 그라데이션 끝색 `rgba(207,228,255,0.2)`. */
private val CharacterCardGradientEnd = Color(0x33CFE4FF)

// default_character.png가 (아직 로컬엔 안 받았지만) 원격 develop에서 400x400px 정사각형으로
// 교체돼서 미리 맞춰둔다(상점(ShopScreen.kt)은 이 값을 공유하지 않고 자기 파일에 똑같은 이름으로
// 따로 두고 있어 여기만 바꿔도 영향 없음). git pull 직후 바로 맞게 반영되도록 값만 먼저 바꿔둔다.
private val CharacterWidth = 400.dp
private val CharacterHeight = 400.dp

/**
 * 홈 화면의 닉네임/캐릭터/상점가기 영역 (Figma Design Page [1.1] `comp/myVehicle`).
 *
 * Figma는 이 영역이 바텀시트 상단까지 그라데이션이 꽉 차게 채워지지만, 콘텐츠(닉네임 줄 + 캐릭터)는
 * 세로로 가운데가 아니라 위쪽에 붙는다 — 상단 바로 아래 닉네임 줄이 바로 오고, 그 아래 캐릭터,
 * 나머지 여백은 바텀시트 쪽으로 남는다(Figma node 3962:11620 확인). 그래서 [modifier]로 상위
 * (HomeScreen)가 남는 공간을 그대로 채우도록 넘기고, 여기서는 fillMaxSize + Arrangement.Top으로
 * 받는다.
 *
 * verticalScroll을 같이 둬서, 화면이 짧거나(작은 기기) 시스템 폰트 크기가 커서 닉네임 줄 + 캐릭터
 * 박스(400x400) 높이가 남는 공간보다 커지는 경우에도 위아래가 그냥 잘리지 않고 스크롤로 볼 수 있게
 * 한다.
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
            .verticalScroll(rememberScrollState())
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

        // 캐릭터 illustration (비-DS 이미지 자리). Figma 220×180 — 별도 배경/카드 없이
        // 그라데이션 위에 바로 얹힌다(캐릭터 전용 배경 박스를 두면 가운데가 네모나게 뚫려 보인다).
        AvatarCharacter(
            equippedImageUrls = equippedImageUrls,
            characterRes = characterRes,
            modifier = Modifier.size(width = CharacterWidth, height = CharacterHeight),
        )
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
