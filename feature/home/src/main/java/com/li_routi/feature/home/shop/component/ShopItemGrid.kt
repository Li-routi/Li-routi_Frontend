package com.li_routi.feature.home.shop.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.foundation.color.NotificationUnreadBackground
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/** 상점 아이템 그리드에 표시하는 아이템 한 건. */
data class ShopItemUiModel(
    val id: String,
    val name: String,
    val price: Int,
    /** 차지하는 자리(HEAD/BODY/HAND). 한 자리엔 하나만 입을 수 있음 */
    val slot: String = "",
    /** 결제 재화. 아이템마다 달라서 가격 옆 아이콘이 이 값으로 갈림 */
    val currency: String = "GEM",
    val imageUrl: String? = null,
    /** 앱에 넣은 이미지. 서버에 없는 캐릭터를 그릴 때 씀 — 있으면 [imageUrl]보다 이걸 먼저 봄 */
    @DrawableRes val imageRes: Int? = null,
    /** 보유한 아이템도 같은 목록에 섞여 내려옴 — 구매 대신 착용만 하면 됨 */
    val owned: Boolean = false,
)

/** 모르는 재화가 와도 화면이 비지 않게 파란보석으로 둠 */
internal fun currencyIconOf(currency: String): Int =
    if (currency == "TOPAZ") R.drawable.diamond_orange else R.drawable.diamond_blue

val SampleShopItems: List<ShopItemUiModel> = List(8) { index ->
    ShopItemUiModel(
        id = "item_$index",
        name = "아이템 ${index + 1}",
        price = 600,
        currency = if (index % 2 == 0) "GEM" else "TOPAZ",
    )
}

/**
 * 상점 아이템 그리드 (Figma node `2299:23502`, 4열 x 2행).
 *
 * 탭 → 아직 안 산 아이템만 [selectedItemIds]에 파란 테두리(선택됨).
 * 보유중은 클릭 중에만 파란 테두리가 보이고, 구매 선택에는 넣지 않음.
 */
@Composable
fun ShopItemGrid(
    items: List<ShopItemUiModel>,
    selectedItemIds: Set<String>,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    /** 지금 입고 있는 아이템 id. 보유중과 구분해서 보여주려고 받음 */
    equippedItemIds: Set<String> = emptySet(),
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(items = items, key = { it.id }) { item ->
            ShopItemCell(
                item = item,
                selected = !item.owned && item.id in selectedItemIds,
                equipped = item.id in equippedItemIds,
                onClick = { onItemClick(item.id) },
            )
        }
    }
}

@Composable
private fun ShopItemCell(
    item: ShopItemUiModel,
    selected: Boolean,
    equipped: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    // 지금 장착 중인 아이템은 계속 파란 테두리+연한 파란 배경을 유지한다(Figma `background/alram`
    // #F2F8FF — 알림 미읽음 배경과 같은 토큰을 재사용). 그 외 보유중은 눌러 있는 동안만, 안 산
    // 아이템은 구매 대상으로 고른 동안만 파란 테두리가 보인다.
    val showBlueBorder = equipped || selected || (item.owned && pressed)
    val borderWidth = if (showBlueBorder) 1.5.dp else 1.dp
    val borderColor = if (showBlueBorder) {
        LiroutiTheme.colors.primaryNormal
    } else {
        LiroutiTheme.colors.borderSub
    }
    val backgroundColor = if (equipped) NotificationUnreadBackground else LiroutiTheme.colors.backgroundDefault

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .border(
                border = BorderStroke(borderWidth, borderColor),
                shape = RoundedCornerShape(6.dp),
            )
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick,
            )
            .padding(top = 8.dp, bottom = 12.dp, start = 10.dp, end = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(LiroutiTheme.colors.backgroundAlternative),
        ) {
            // 그릴 게 없으면 기존처럼 회색 자리만 보여줌
            when {
                item.imageRes != null -> Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = item.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                )

                !item.imageUrl.isNullOrBlank() -> AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        // 이미 산 아이템은 다시 살 수 없어서 가격 대신 보유/착용 상태를 보여줌
        if (item.owned) {
            Text(
                text = if (equipped) "장착 중" else "보유 중",
                style = LiroutiTheme.typography.body3SemiBold.copy(lineHeight = 16.sp),
                color = if (equipped) {
                    LiroutiTheme.colors.primaryNormal
                } else {
                    LiroutiTheme.colors.labelSub
                },
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = currencyIconOf(item.currency)),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = item.price.toString(),
                    // Figma Body4/Bold 13/16
                    style = LiroutiTheme.typography.body3SemiBold.copy(lineHeight = 16.sp),
                    color = LiroutiTheme.colors.labelStrong,
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 320, name = "기본")
@Composable
private fun ShopItemGridPreview() {
    LiroutiFrontendTheme {
        ShopItemGrid(
            items = SampleShopItems.map { it.copy(owned = it.id == "item_0") },
            selectedItemIds = emptySet(),
            onItemClick = {},
            equippedItemIds = setOf("item_0"),
        )
    }
}

@Preview(showBackground = true, heightDp = 320, name = "여러 개 선택됨")
@Composable
private fun ShopItemGridSelectedPreview() {
    LiroutiFrontendTheme {
        ShopItemGrid(
            items = SampleShopItems,
            selectedItemIds = setOf("item_1", "item_3", "item_4"),
            onItemClick = {},
        )
    }
}
