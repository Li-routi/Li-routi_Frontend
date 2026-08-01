package com.li_routi.feature.home.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/** Design Page [1.1] `badge/rectangle_large` — 대표 배지. */
private val RepresentativeBadgeBackground = Color(0xFFF1F8E9)
private val RepresentativeBadgeText = Color(0xFF3A8009)

/** Figma `comp/myVehicle` 그라데이션 끝색 `rgba(207,228,255,0.2)`. */
private val CharacterCardGradientEnd = Color(0x33CFE4FF)

/**
 * Figma `Tooltip/Tooltip` 배경 (`neutral/neutral22` #2E2F33 @ opacity 88%).
 * DS Tooltip은 문구 고정·미완성이라 홈에서는 쓰지 않고 동일 스펙으로 직접 그린다.
 */
private val HomeTooltipFill = Color(0xFF2E2F33).copy(alpha = 0.88f)

private val CharacterWidth = 220.dp
private val CharacterHeight = 180.dp
private val TooltipArrowWidth = 20.dp
private val TooltipArrowHeight = 8.dp
private val TooltipArrowStartPadding = 8.dp
/** Figma tooltip `mb-[-12px]` — 말풍선이 캐릭터 위로 12dp 겹침. */
private val TooltipCharacterOverlap = 12.dp

/**
 * 홈 화면의 닉네임/캐릭터/상점가기 영역 (Figma Design Page [1.1] `comp/myVehicle`).
 *
 * 캐릭터 위 말풍선은 홈 메인 Home 프레임의 `Tooltip/Tooltip`과 동일 스펙이다.
 */
@Composable
fun ShopEntryCard(
    nickname: String,
    tooltipMessage: String,
    onNavigateToShop: () -> Unit,
    modifier: Modifier = Modifier,
    showRepresentativeBadge: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        LiroutiTheme.colors.backgroundDefault,
                        CharacterCardGradientEnd,
                    ),
                ),
            )
            .padding(16.dp),
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
                    .clickable(onClick = onNavigateToShop)
                    .padding(4.dp),
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

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            HomeCharacterTooltip(
                message = tooltipMessage,
                modifier = Modifier.zIndex(1f),
            )
            // 캐릭터 illustration (비-DS 이미지 자리). Figma 220×180.
            Box(
                modifier = Modifier
                    .offset(y = -TooltipCharacterOverlap)
                    .size(width = CharacterWidth, height = CharacterHeight)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LiroutiTheme.colors.backgroundAlternative),
            )
        }
    }
}

/**
 * Figma `Tooltip/Tooltip` (홈 `comp/myVehicle`용).
 * - 본문: radius 8, padding 12×8, maxWidth 256, minWidth 64
 * - 꼬리: 아래쪽, 시작점 8dp, 20×8
 * - 텍스트: Body3/Medium (14/22)
 */
@Composable
private fun HomeCharacterTooltip(
    message: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.wrapContentWidth(align = Alignment.Start),
        horizontalAlignment = Alignment.Start,
    ) {
        Box(
            modifier = Modifier
                .widthIn(min = 64.dp, max = 256.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(HomeTooltipFill)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = message,
                style = LiroutiTheme.typography.body2LongMedium,
                color = LiroutiTheme.colors.backgroundDefault,
            )
        }
        Canvas(
            modifier = Modifier
                .padding(start = TooltipArrowStartPadding)
                .size(width = TooltipArrowWidth, height = TooltipArrowHeight),
        ) {
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width / 2f, size.height)
                close()
            }
            drawPath(path = path, color = HomeTooltipFill)
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
private fun ShopEntryCardWithBadgePreview() {
    LiroutiFrontendTheme {
        ShopEntryCard(
            nickname = "닉네임",
            tooltipMessage = "반가워요!",
            onNavigateToShop = {},
            showRepresentativeBadge = true,
        )
    }
}

@Preview(showBackground = true, name = "말풍선만")
@Composable
private fun HomeCharacterTooltipPreview() {
    LiroutiFrontendTheme {
        Box(modifier = Modifier.padding(24.dp)) {
            HomeCharacterTooltip(message = "반가워요!")
        }
    }
}
