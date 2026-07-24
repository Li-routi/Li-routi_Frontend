package com.li_routi.feature.shopping.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.DsPlaceholder
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/** 상점 아이템 그리드에 표시하는 아이템 한 건. */
data class ShopItemUiModel(
    val id: String,
    val name: String,
    val price: Int,
)

/**
 * Preview/개발 확인용 샘플 데이터(8건, Figma `item-coin-3` 인스턴스 개수와 동일). 실제 데이터 연동은
 * 이번 범위 제외.
 */
val SampleShopItems: List<ShopItemUiModel> = List(8) { index ->
    ShopItemUiModel(id = "item_$index", name = "아이템 ${index + 1}", price = 600)
}

/**
 * 상점 아이템 그리드 (Figma node `2299:23502`, 4열 x 2행).
 *
 * 썸네일은 비-DS 이미지 자산이라 단순 [Box]로, 가격 옆 다이아 아이콘은 Design System instance라
 * [DsPlaceholder]로 대체한다. 가격 텍스트만 실제 구현.
 */
@Composable
fun ShopItemGrid(
    items: List<ShopItemUiModel>,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(items = items, key = { it.id }) { item ->
            ShopItemCell(item = item)
        }
    }
}

@Composable
private fun ShopItemCell(item: ShopItemUiModel, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .border(
                border = BorderStroke(1.dp, LiroutiTheme.colors.borderSub),
                shape = RoundedCornerShape(6.dp),
            )
            .padding(top = 8.dp, bottom = 12.dp, start = 10.dp, end = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 아이템 썸네일 (비-DS 이미지 자산) — 실제 에셋 반영은 이번 범위 제외
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(LiroutiTheme.colors.backgroundAlternative),
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            DsPlaceholder(
                componentName = "Diamond",
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = item.price.toString(),
                style = LiroutiTheme.typography.caption,
                color = LiroutiTheme.colors.labelStrong,
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 320)
@Composable
private fun ShopItemGridPreview() {
    LiroutiFrontendTheme {
        ShopItemGrid(items = SampleShopItems)
    }
}
