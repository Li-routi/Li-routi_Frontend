package com.li_routi.feature.home.shop.screen

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.component.LiroutiLineTab
import com.li_routi.core.designsystem.component.LiroutiSwitch
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.home.shop.component.SampleShopItems
import com.li_routi.feature.home.shop.component.ShopItemGrid
import com.li_routi.feature.home.shop.component.ShopItemUiModel
import com.li_routi.feature.home.shop.component.ShopTopBar
import com.li_routi.feature.home.shop.component.currencyIconOf
import com.li_routi.feature.home.shop.navigation.ShopScreenActions
import com.li_routi.feature.home.component.AvatarCharacter
import com.li_routi.feature.home.component.equippedImageUrlsOf
import com.li_routi.feature.home.shop.vm.EquippedUiModel
import com.li_routi.feature.home.shop.vm.ShopCategoryUiModel

private val CharacterWidth = 220.dp
private val CharacterHeight = 180.dp

/**
 * 상점(아이템 상점) 화면 (Figma node `2222:25595` / Design Page [1.1] 상점).
 *
 * 아이템 선택은 [actions.onItemClick] → ViewModel `selectedItems`로 관리한다.
 *
 * 카테고리 탭은 `GET /api/shop/categories`로 받아 순서대로 그린다 — 이름으로 분기하지 않는다.
 * 탭/보유 토글을 바꾸면 그 조건으로 목록을 다시 조회한다.
 * 저장 버튼 실처리는 [ShopUiEvent.SaveSelectedItems] 수신 측(API)에서 연결한다.
 */
@Composable
fun ShopScreen(
    actions: ShopScreenActions,
    nickname: String = "닉네임",
    coinBalance: Int = 450,
    gemBalance: Int = 30,
    categories: List<ShopCategoryUiModel> = emptyList(),
    equipped: Map<String, EquippedUiModel> = emptyMap(),
    savedEquippedItemIds: Set<Long> = emptySet(),
    selectedCategoryIndex: Int = 0,
    showOwnedOnly: Boolean = false,
    items: List<ShopItemUiModel> = SampleShopItems,
    selectedItemIds: Set<String> = emptySet(),
    /** 고른 것 중 안 산 아이템. 다른 탭에서 고른 것도 섞여 있어서 [items]와 따로 받음 */
    purchaseTargets: List<ShopItemUiModel> = emptyList(),
    modifier: Modifier = Modifier,
) {

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            ShopTopBar(
                title = "상점",
                coinBalance = coinBalance,
                gemBalance = gemBalance,
                onBackClick = actions::onBackClick,
                onOrangeGemClick = actions::onOrangeGemClick,
                onBlueGemClick = actions::onBlueGemClick,
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(LiroutiTheme.colors.primaryNormal)
                    .clickable(onClick = actions::onSaveClick),
                contentAlignment = Alignment.Center,
            ) {
                // 안 산 걸 골랐으면 결제라는 게 분명하도록 개수와 합계를 보여줌
                if (purchaseTargets.isEmpty()) {
                    Text(
                        text = "저장하기",
                        // Figma: Medium 16/24
                        style = LiroutiTheme.typography.body1Medium,
                        color = LiroutiTheme.colors.labelReverse,
                    )
                } else {
                    PurchaseButtonLabel(targets = purchaseTargets)
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(LiroutiTheme.colors.backgroundDefault)
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = nickname,
                    style = LiroutiTheme.typography.heading2,
                    color = LiroutiTheme.colors.labelStrong,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(20.dp))
                AvatarCharacter(
                    equippedImageUrls = equippedImageUrlsOf(equipped.mapValues { it.value.imageUrl }),
                    modifier = Modifier.size(width = CharacterWidth, height = CharacterHeight),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Figma `2222:25595`: 카테고리 탭 → 보유 토글 → 아이템 그리드
            // 탭을 못 받아오면 줄 자체를 빼서 빈 띠가 남지 않게 함
            if (categories.isNotEmpty()) {
                LiroutiLineTab(
                    tabs = categories.map { it.name },
                    selectedIndex = selectedCategoryIndex,
                    onTabSelected = actions::onCategorySelected,
                )

                Spacer(modifier = Modifier.height(14.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "보유 중인 아이템만 보기",
                    style = LiroutiTheme.typography.body3Regular.copy(lineHeight = 16.sp),
                    color = LiroutiTheme.colors.labelStrong,
                    modifier = Modifier.padding(end = 6.dp),
                )
                LiroutiSwitch(
                    checked = showOwnedOnly,
                    onCheckedChange = actions::onOwnedOnlyChange,
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            ShopItemGrid(
                items = items,
                selectedItemIds = selectedItemIds,
                onItemClick = actions::onItemClick,
                modifier = Modifier.weight(1f),
                equippedItemIds = savedEquippedItemIds.mapTo(mutableSetOf()) { it.toString() },
            )
        }
    }
}

/**
 * 구매 버튼 문구 — `주황 350 · 파랑 120 · 3개 구매` 꼴.
 *
 * 아이템마다 결제 재화가 달라서 재화별로 합계를 나눠 보여줌
 */
@Composable
private fun PurchaseButtonLabel(
    targets: List<ShopItemUiModel>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        targets.groupBy { it.currency }.forEach { (currency, sameCurrency) ->
            Image(
                painter = painterResource(id = currencyIconOf(currency)),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = sameCurrency.sumOf { it.price }.toString(),
                style = LiroutiTheme.typography.body1Medium,
                color = LiroutiTheme.colors.labelReverse,
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = "${targets.size}개 구매",
            style = LiroutiTheme.typography.body1Medium,
            color = LiroutiTheme.colors.labelReverse,
        )
    }
}

private object PreviewShopScreenActions : ShopScreenActions {
    override fun onBackClick() = Unit
    override fun onOrangeGemClick() = Unit
    override fun onBlueGemClick() = Unit
    override fun onCategorySelected(index: Int) = Unit
    override fun onOwnedOnlyChange(ownedOnly: Boolean) = Unit
    override fun onItemClick(itemId: String) = Unit
    override fun onSaveClick() = Unit
}

@Preview(showBackground = true, heightDp = 800, name = "기본")
@Composable
private fun ShopScreenPreview() {
    LiroutiFrontendTheme {
        ShopScreen(actions = PreviewShopScreenActions)
    }
}

@Preview(showBackground = true, heightDp = 800, name = "여러 개 골라 일괄 구매")
@Composable
private fun ShopScreenSelectedPreview() {
    LiroutiFrontendTheme {
        val selected = SampleShopItems.filter { it.id in setOf("item_1", "item_2", "item_3") }
        ShopScreen(
            actions = PreviewShopScreenActions,
            selectedItemIds = selected.mapTo(mutableSetOf()) { it.id },
            purchaseTargets = selected,
        )
    }
}
