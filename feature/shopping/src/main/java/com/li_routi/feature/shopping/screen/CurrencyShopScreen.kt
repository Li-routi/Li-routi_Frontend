package com.li_routi.feature.shopping.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.feature.shopping.component.CurrencyChargeDialog
import com.li_routi.feature.shopping.component.CurrencyChargeDialogContent
import com.li_routi.feature.shopping.component.CurrencyProductList
import com.li_routi.feature.shopping.component.CurrencyProductUiModel
import com.li_routi.feature.shopping.component.CurrencyShopLineTab
import com.li_routi.feature.shopping.component.SampleCurrencyProducts
import com.li_routi.feature.shopping.component.ShopTopBar
import com.li_routi.feature.shopping.navigation.CurrencyShopScreenActions

/** Figma node `2305:14471` 탭 라벨. */
private val CurrencyTabLabels = listOf("주황보석", "파란보석")

/**
 * 재화 구매 리스트 화면 (Figma node `2299:22979`).
 *
 * API 연동 전: 주황보석/파란보석 탭은 UI 선택만 반영하고 상품 목록 필터는 하지 않는다.
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
    // API 연동 전: 탭 선택 UI만. 상품 리스트 교체는 미연결.
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
            CurrencyShopLineTab(
                tabs = CurrencyTabLabels,
                selectedIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it },
            )

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

/**
 * Preview용: [Dialog] Window는 Preview에서 안 보이는 경우가 많아,
 * 딤 + [CurrencyChargeDialogContent]를 같은 화면에 오버레이로 그린다.
 */
@Preview(showBackground = true, heightDp = 800, name = "충전 팝업")
@Composable
private fun CurrencyShopScreenChargeDialogPreview() {
    LiroutiFrontendTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            CurrencyShopScreen(
                actions = PreviewCurrencyShopScreenActions,
                selectedProductId = "currency_2",
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center,
            ) {
                CurrencyChargeDialogContent(
                    product = SampleCurrencyProducts[2],
                    onDismiss = {},
                    onConfirmCharge = {},
                )
            }
        }
    }
}
