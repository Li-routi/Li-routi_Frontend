package com.li_routi.feature.home.shop.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.R
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
    /** 보유한 아이템도 같은 목록에 섞여 내려옴 — 구매 대신 착용만 하면 됨 */
    val owned: Boolean = false,
)

/** 모르는 재화가 와도 화면이 비지 않게 파란보석으로 둠 */
private fun currencyIconOf(currency: String): Int =
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
 * 탭 → [selectedItemId]에 파란 테두리(선택됨). 재화구매 리스트와 동일한 선택 스타일.
 */
@Composable
fun ShopItemGrid(
    items: List<ShopItemUiModel>,
    selectedItemId: String?,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
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
                selected = item.id == selectedItemId,
                onClick = { onItemClick(item.id) },
            )
        }
    }
}

@Composable
private fun ShopItemCell(
    item: ShopItemUiModel,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderWidth = if (selected) 1.5.dp else 1.dp
    val borderColor = if (selected) {
        LiroutiTheme.colors.primaryNormal
    } else {
        LiroutiTheme.colors.borderSub
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .border(
                border = BorderStroke(borderWidth, borderColor),
                shape = RoundedCornerShape(6.dp),
            )
            .clickable(onClick = onClick)
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
            // imageUrl이 없으면 기존처럼 회색 자리만 보여줌
            if (!item.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
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

@Preview(showBackground = true, heightDp = 320, name = "기본")
@Composable
private fun ShopItemGridPreview() {
    LiroutiFrontendTheme {
        ShopItemGrid(
            items = SampleShopItems,
            selectedItemId = null,
            onItemClick = {},
        )
    }
}

@Preview(showBackground = true, heightDp = 320, name = "선택됨")
@Composable
private fun ShopItemGridSelectedPreview() {
    LiroutiFrontendTheme {
        ShopItemGrid(
            items = SampleShopItems,
            selectedItemId = "item_1",
            onItemClick = {},
        )
    }
}
