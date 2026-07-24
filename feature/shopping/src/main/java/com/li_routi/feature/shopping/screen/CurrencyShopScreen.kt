package com.li_routi.feature.shopping.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.DsPlaceholder
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.feature.shopping.component.CurrencyChargeDialog
import com.li_routi.feature.shopping.component.CurrencyProductList
import com.li_routi.feature.shopping.component.CurrencyProductUiModel
import com.li_routi.feature.shopping.component.SampleCurrencyProducts
import com.li_routi.feature.shopping.component.ShopTopBar
import com.li_routi.feature.shopping.navigation.CurrencyShopScreenActions

/** Figma node `2305:14471` 탭 라벨. */
private val CurrencyTabLabels = listOf("주황보석", "파란보석")

/**
 * 재화 구매 리스트 화면 (Figma node `2299:22979`).
 *
 * 상품 탭 흐름:
 * 1. 첫 탭 → [selectedProductId]에 파란 테두리
 * 2. 같은 상품 재탭 → [chargeDialogProduct] 충전 팝업
 *
 * 실제 앱에서는 [com.li_routi.feature.shopping.navigation.CurrencyShopRoute]를 통해
 * [com.li_routi.feature.shopping.vm.CurrencyShopViewModel]과 연결한다.
 */
@Composable
fun CurrencyShopScreen(
    actions: CurrencyShopScreenActions,
    coinBalance: Int = 450,
    gemBalance: Int = 30,
    products: List<CurrencyProductUiModel> = SampleCurrencyProducts,
    selectedProductId: String? = null,
    chargeDialogProduct: CurrencyProductUiModel? = null,
    modifier: Modifier = Modifier,
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            ShopTopBar(
                title = "상점",
                coinBalance = coinBalance,
                gemBalance = gemBalance,
                onBackClick = actions::onBackClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 14.dp, end = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                CurrencyTabLabels.forEachIndexed { index, _ ->
                    DsPlaceholder(
                        componentName = "Tab",
                        modifier = Modifier
                            .height(36.dp)
                            .clickable { selectedTabIndex = index },
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                CurrencyProductList(
                    items = products,
                    selectedProductId = selectedProductId,
                    onProductClick = actions::onProductClick,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

    if (chargeDialogProduct != null) {
        CurrencyChargeDialog(
            product = chargeDialogProduct,
            onDismiss = actions::onDismissChargeDialog,
            onConfirmCharge = actions::onConfirmChargeClick,
        )
    }
}

private object PreviewCurrencyShopScreenActions : CurrencyShopScreenActions {
    override fun onBackClick() = Unit
    override fun onProductClick(productId: String) = Unit
    override fun onDismissChargeDialog() = Unit
    override fun onConfirmChargeClick() = Unit
}

@Preview(showBackground = true, heightDp = 800, name = "선택됨")
@Composable
private fun CurrencyShopScreenSelectedPreview() {
    LiroutiFrontendTheme {
        CurrencyShopScreen(
            actions = PreviewCurrencyShopScreenActions,
            selectedProductId = "currency_2",
        )
    }
}

@Preview(showBackground = true, heightDp = 800, name = "충전 팝업")
@Composable
private fun CurrencyShopScreenChargeDialogPreview() {
    LiroutiFrontendTheme {
        CurrencyShopScreen(
            actions = PreviewCurrencyShopScreenActions,
            selectedProductId = "currency_2",
            chargeDialogProduct = SampleCurrencyProducts[2],
        )
    }
}
