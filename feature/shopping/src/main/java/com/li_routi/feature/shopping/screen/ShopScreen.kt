package com.li_routi.feature.shopping.screen

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.LiroutiLineTab
import com.li_routi.core.designsystem.component.LiroutiSwitch
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.shopping.component.SampleShopItems
import com.li_routi.feature.shopping.component.ShopItemGrid
import com.li_routi.feature.shopping.component.ShopItemUiModel
import com.li_routi.feature.shopping.component.ShopTopBar
import com.li_routi.feature.shopping.navigation.ShopScreenActions

private val ShopCategoryTabLabels = listOf("전체", "카테고리", "카테고리")

/**
 * 상점(아이템 상점) 화면 (Figma node `2222:25595`).
 *
 * 아이템 선택은 [actions.onItemClick] → ViewModel [selectedItemId]로 관리한다.
 *
 * API 연동 전: 카테고리 탭·보유 아이템 토글은 UI 선택만 반영하고 목록 필터는 하지 않는다.
 * 저장 버튼 실처리는 [ShopUiEvent.SaveSelectedItems] 수신 측(API)에서 연결한다.
 */
@Composable
fun ShopScreen(
    actions: ShopScreenActions,
    nickname: String = "닉네임",
    coinBalance: Int = 450,
    gemBalance: Int = 30,
    items: List<ShopItemUiModel> = SampleShopItems,
    selectedItemId: String? = null,
    modifier: Modifier = Modifier,
) {
    // API 연동 전: 선택 UI만. 목록 필터/서버 조회는 미연결.
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    var showOwnedOnly by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            ShopTopBar(
                title = "상점",
                coinBalance = coinBalance,
                gemBalance = gemBalance,
                onBackClick = actions::onBackClick,
                onCurrencyChipClick = actions::onCurrencyChipClick,
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(LiroutiTheme.colors.primaryNormal)
                    .clickable(onClick = actions::onSaveClick),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "저장",
                    style = LiroutiTheme.typography.body2,
                    color = LiroutiTheme.colors.labelReverse,
                )
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
                Box(
                    modifier = Modifier
                        .widthIn(max = 256.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(LiroutiTheme.colors.labelDefault.copy(alpha = 0.88f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "원하는 아이템을 골라보세요!",
                        style = LiroutiTheme.typography.body2,
                        color = LiroutiTheme.colors.labelReverse,
                        textAlign = TextAlign.Center,
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(LiroutiTheme.colors.backgroundAlternative),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "보유 중인 아이템만 보기",
                    style = LiroutiTheme.typography.caption,
                    color = LiroutiTheme.colors.labelStrong,
                    modifier = Modifier.padding(end = 6.dp),
                )
                LiroutiSwitch(
                    checked = showOwnedOnly,
                    onCheckedChange = { showOwnedOnly = it },
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            LiroutiLineTab(
                tabs = ShopCategoryTabLabels,
                selectedIndex = selectedCategoryIndex,
                onTabSelected = { selectedCategoryIndex = it },
            )

            Spacer(modifier = Modifier.height(10.dp))

            ShopItemGrid(
                items = items,
                selectedItemId = selectedItemId,
                onItemClick = actions::onItemClick,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

private object PreviewShopScreenActions : ShopScreenActions {
    override fun onBackClick() = Unit
    override fun onCurrencyChipClick() = Unit
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

@Preview(showBackground = true, heightDp = 800, name = "선택됨")
@Composable
private fun ShopScreenSelectedPreview() {
    LiroutiFrontendTheme {
        ShopScreen(
            actions = PreviewShopScreenActions,
            selectedItemId = "item_1",
        )
    }
}
