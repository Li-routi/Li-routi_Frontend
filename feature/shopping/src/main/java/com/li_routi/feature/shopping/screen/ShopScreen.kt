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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.DsPlaceholder
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.shopping.component.SampleShopItems
import com.li_routi.feature.shopping.component.ShopItemGrid
import com.li_routi.feature.shopping.component.ShopItemUiModel
import com.li_routi.feature.shopping.component.ShopTopBar
import com.li_routi.feature.shopping.navigation.ShopScreenActions

/** Figma node `2299:22819`(tabbar)의 탭 라벨. 카테고리 탭 2개는 Figma 원본에도 확정 명칭이 없다. */
private val ShopCategoryTabLabels = listOf("전체", "카테고리", "카테고리")

/**
 * 상점(아이템 상점) 화면 (Figma node `2222:25595`).
 *
 * 레이아웃과 사용자 흐름(카테고리 탭/보유 아이템 토글 선택)만 구현한다.
 * Design System instance는 전부 [DsPlaceholder]로 대체하며, 실제 화면 전환은 [actions]를
 * 구현하는 다른 담당자가 처리한다.
 *
 * 실제 앱에서는 [com.li_routi.feature.shopping.navigation.ShopRoute]를 통해
 * [com.li_routi.feature.shopping.vm.ShopViewModel]과 연결한다.
 */
@Composable
fun ShopScreen(
    actions: ShopScreenActions,
    nickname: String = "닉네임",
    coinBalance: Int = 450,
    gemBalance: Int = 30,
    items: List<ShopItemUiModel> = SampleShopItems,
    modifier: Modifier = Modifier,
) {
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
            DsPlaceholder(
                componentName = "Button_Filled",
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .height(44.dp)
                    .clickable(onClick = actions::onSaveClick),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
            // "닉네임" 카드: 캐릭터 illustration(비-DS) + Tooltip(DS instance)
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
                DsPlaceholder(
                    componentName = "Tooltip",
                    modifier = Modifier.height(48.dp),
                )
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
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showOwnedOnly = !showOwnedOnly },
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "보유 중인 아이템만 보기",
                    style = LiroutiTheme.typography.caption,
                    color = LiroutiTheme.colors.labelStrong,
                )
                Spacer(modifier = Modifier.width(6.dp))
                DsPlaceholder(
                    componentName = "Toggle",
                    modifier = Modifier.size(width = 28.dp, height = 16.dp),
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                ShopCategoryTabLabels.forEachIndexed { index, _ ->
                    DsPlaceholder(
                        componentName = "Tab",
                        modifier = Modifier
                            .height(36.dp)
                            .clickable { selectedCategoryIndex = index },
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            ShopItemGrid(
                items = items,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

private object PreviewShopScreenActions : ShopScreenActions {
    override fun onBackClick() = Unit
    override fun onCurrencyChipClick() = Unit
    override fun onSaveClick() = Unit
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun ShopScreenPreview() {
    LiroutiFrontendTheme {
        ShopScreen(actions = PreviewShopScreenActions)
    }
}
