package com.li_routi.feature.home.shop.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.component.LiroutiLineTab
import com.li_routi.core.designsystem.component.LiroutiSwitch
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.home.shop.component.SampleShopItems
import com.li_routi.feature.home.shop.component.ShopItemGrid
import com.li_routi.feature.home.shop.component.ShopItemUiModel
import com.li_routi.feature.home.shop.component.ShopTopBar
import com.li_routi.feature.home.shop.component.currencyIconOf
import com.li_routi.feature.home.shop.navigation.ShopScreenActions
import com.li_routi.core.data.appearance.FallbackCharacterId
import com.li_routi.core.domain.shop.AvatarLayer
import com.li_routi.feature.home.component.AvatarCharacter
import com.li_routi.feature.home.shop.vm.EquippedUiModel
import com.li_routi.feature.home.shop.vm.ShopCategoryUiModel
import com.li_routi.feature.home.shop.vm.ShopMainTab

// 높이를 180 -> 230으로 올리면서 기존 220:180 비율을 유지해 너비도 같이 늘림(220 * 230/180 ≈ 281).
private val CharacterHeight = 230.dp
private val CharacterWidth = 281.dp

/** [ShopMainTab.entries] 순서(CHARACTER, CLOTHING)와 짝을 맞춘 탭 라벨. */
private val ShopMainTabLabels = listOf("캐릭터", "의상")

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
    selectedMainTab: ShopMainTab = ShopMainTab.CHARACTER,
    categories: List<ShopCategoryUiModel> = emptyList(),
    equipped: Map<String, EquippedUiModel> = emptyMap(),
    savedEquippedItemIds: Set<Long> = emptySet(),
    selectedCategoryIndex: Int = 0,
    showOwnedOnly: Boolean = false,
    items: List<ShopItemUiModel> = SampleShopItems,
    selectedItemIds: Set<String> = emptySet(),
    /** 고른 것 중 안 산 아이템. 다른 탭에서 고른 것도 섞여 있어서 [items]와 따로 받음 */
    purchaseTargets: List<ShopItemUiModel> = emptyList(),
    /** 미리보기 중인 모습이 저장된 모습과 다른지. 하단 버튼 활성화 여부를 가름 */
    hasUnsavedChanges: Boolean = false,
    /** 격자를 처음(또는 탭 전환 직후) 불러오는 중인지 — 비어 있는 격자가 잠깐 보이는 걸 막는다. */
    isLoading: Boolean = false,
    /** 지금 캐릭터 카드에 그릴 캐릭터. 캐릭터 탭에서 고르면 저장 전에도 바로 바뀜 */
    previewCharacterId: Long = FallbackCharacterId,
    /** 캐릭터 카드에 지금 그릴 레이어(캐릭터·둥지·착장 미리보기 반영됨) */
    previewLayers: List<AvatarLayer> = emptyList(),
    /** 서버에 저장된 캐릭터. 격자 `착용중`은 이걸 따름 */
    savedCharacterId: Long = FallbackCharacterId,
    modifier: Modifier = Modifier,
) {

    Scaffold(
        modifier = modifier.fillMaxSize(),
        // LiroutiFrontendTheme이 MaterialTheme에 colorScheme을 안 넘겨서, 명시하지 않으면
        // Compose Material3 기본 배경색(붉은끼가 도는 기본 팔레트)이 깔린다 — Figma(순백)와 맞춘다.
        containerColor = LiroutiTheme.colors.backgroundDefault,
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
            // 저장된 모습과 달라진 게 있어야 활성화(파란 배경 + 클릭 가능)한다 — 안 산 아이템이
            // 섞여 있으면 "N개 구매" 문구로, 이미 보유한 것끼리만 바꿨으면 "저장하기"로 보여준다.
            // 아무것도 안 바꿨으면 "현재 모습" 문구와 함께 실제로 눌리지 않는 회색 상태로 둔다
            // (Figma node `6057:20321` 등 — 단, "저장하기"/비활성 클릭 방지는 Figma에 없는 로직).
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (hasUnsavedChanges) {
                            LiroutiTheme.colors.primaryNormal
                        } else {
                            LiroutiTheme.colors.backgroundAlternative
                        },
                    )
                    .then(
                        if (hasUnsavedChanges) Modifier.clickable(onClick = actions::onSaveClick) else Modifier,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    !hasUnsavedChanges -> Text(
                        text = "현재 모습",
                        // Figma: Medium 16/24
                        style = LiroutiTheme.typography.body1Medium,
                        color = LiroutiTheme.colors.labelInfo,
                    )
                    purchaseTargets.isEmpty() -> Text(
                        text = "저장하기",
                        style = LiroutiTheme.typography.body1Medium,
                        color = LiroutiTheme.colors.labelReverse,
                    )
                    else -> PurchaseButtonLabel(targets = purchaseTargets)
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
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
                    layers = previewLayers,
                    modifier = Modifier.size(width = CharacterWidth, height = CharacterHeight),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Figma `6057:20320`: 최상위 탭("캐릭터"/"의상")과 "의상" 하위 카테고리 필터가
            // 분리된 구조 — 최상위 탭은 항상 보이고, 하위 필터·보유 토글은 "의상"일 때만 보인다.
            // 탭 띠는 Figma에서 다른 섹션들의 16dp 여백 밖까지 흰 배경으로 엣지투엣지로 깔리고,
            // 두 탭이 화면 폭을 나눠 채우는 넓은 형태라 equalWidth를 쓴다.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LiroutiTheme.colors.backgroundDefault),
            ) {
                LiroutiLineTab(
                    tabs = ShopMainTabLabels,
                    selectedIndex = selectedMainTab.ordinal,
                    onTabSelected = { index -> actions.onMainTabSelected(ShopMainTab.entries[index]) },
                    equalWidth = true,
                )
            }

            if (selectedMainTab == ShopMainTab.CLOTHING) {
                if (categories.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    ShopCategoryFilterRow(
                        categories = categories,
                        selectedIndex = selectedCategoryIndex,
                        onCategorySelected = actions::onCategorySelected,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
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
            } else {
                Spacer(modifier = Modifier.height(14.dp))
            }

            // 탭/카테고리를 바꿀 때마다 다시 조회하는데, 아직 하나도 못 받아온 순간엔 격자가
            // 비어 있어 "빈 상점"처럼 보였다 — 그 순간만 스피너로 대체한다.
            if (isLoading && items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = LiroutiTheme.colors.primaryNormal)
                }
            } else {
                // 탭/카테고리가 바뀌면 그리드를 통째로 새로 만들어서 스크롤이 맨 위로 돌아가게 함 —
                // 안 그러면 이전 목록의 스크롤 위치가 새 목록에 그대로 남아 있었다
                key(selectedMainTab, selectedCategoryIndex, showOwnedOnly) {
                    ShopItemGrid(
                        items = items,
                        selectedItemIds = selectedItemIds,
                        onItemClick = actions::onItemClick,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        equippedItemIds = savedEquippedItemIds.mapTo(mutableSetOf()) { it.toString() } +
                            savedCharacterId.toString(),
                    )
                }
            }
        }
    }
}

/**
 * "의상" 탭 하위 카테고리 필터(전체/머리장식/옷/소품/세트). Figma node `6057:20320`: 알약형
 * 칩 — 선택된 칩은 파란 배경 + 체크마크, 그 외는 흰 배경 + 테두리.
 */
@Composable
private fun ShopCategoryFilterRow(
    categories: List<ShopCategoryUiModel>,
    selectedIndex: Int,
    onCategorySelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        categories.forEachIndexed { index, category ->
            val selected = index == selectedIndex
            Row(
                modifier = Modifier
                    .height(38.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .then(
                        if (selected) {
                            Modifier.background(LiroutiTheme.colors.primaryNormal)
                        } else {
                            Modifier
                                .background(LiroutiTheme.colors.backgroundDefault)
                                .border(
                                    width = 1.dp,
                                    color = LiroutiTheme.colors.borderDefault,
                                    shape = RoundedCornerShape(percent = 50),
                                )
                        },
                    )
                    .clickable { onCategorySelected(index) }
                    .padding(horizontal = if (selected) 12.dp else 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (selected) {
                    Image(
                        painter = painterResource(id = R.drawable.checkmark),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelReverse),
                    )
                }
                Text(
                    text = category.name,
                    style = LiroutiTheme.typography.body2LongMedium,
                    color = if (selected) LiroutiTheme.colors.labelReverse else LiroutiTheme.colors.labelDefault,
                )
            }
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
    override fun onMainTabSelected(tab: ShopMainTab) = Unit
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
            selectedMainTab = ShopMainTab.CLOTHING,
            selectedItemIds = selected.mapTo(mutableSetOf()) { it.id },
            purchaseTargets = selected,
        )
    }
}
