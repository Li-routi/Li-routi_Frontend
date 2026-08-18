package com.li_routi.feature.home.shop.screen

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
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.home.shop.component.CurrencyChargeDialog
import com.li_routi.feature.home.shop.component.CurrencyChargeDialogContent
import com.li_routi.feature.home.shop.component.CurrencyProductList
import com.li_routi.feature.home.shop.component.CurrencyProductUiModel
import com.li_routi.feature.home.shop.component.CurrencyShopLineTab
import com.li_routi.feature.home.shop.component.SampleBlueGemProducts
import com.li_routi.feature.home.shop.component.SampleOrangeGemProducts
import com.li_routi.feature.home.shop.component.ShopTopBar
import com.li_routi.feature.home.shop.navigation.CurrencyShopScreenActions

/** Figma node `2305:14471` 탭 라벨. index 0 = 주황, 1 = 파란. */
private val CurrencyTabLabels = listOf("오렌지젬", "블루젬")

private const val TabOrange = 0
private const val TabBlue = 1

/**
 * 재화 구매 리스트 화면 (Figma node `2299:22979` / 파란보석 탭).
 *
 * 주황보석 → 재화 결제 리스트, 파란보석 → 원화 결제 리스트.
 */
@Composable
fun CurrencyShopScreen(
    actions: CurrencyShopScreenActions,
    coinBalance: Int = 450,
    gemBalance: Int = 30,
    orangeProducts: List<CurrencyProductUiModel> = SampleOrangeGemProducts,
    blueProducts: List<CurrencyProductUiModel> = SampleBlueGemProducts,
    selectedProductId: String? = null,
    chargeDialogProduct: CurrencyProductUiModel? = null,
    modifier: Modifier = Modifier,
    initialTabIndex: Int = TabOrange,
) {
    var selectedTabIndex by remember { mutableIntStateOf(initialTabIndex) }
    val products = if (selectedTabIndex == TabBlue) blueProducts else orangeProducts

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
                onOrangeGemClick = { selectedTabIndex = TabOrange },
                onBlueGemClick = { selectedTabIndex = TabBlue },
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

@Preview(showBackground = true, heightDp = 800, name = "파란보석 탭")
@Composable
private fun CurrencyShopScreenBlueTabPreview() {
    LiroutiFrontendTheme {
        CurrencyShopScreen(
            actions = PreviewCurrencyShopScreenActions,
            selectedProductId = "blue_2",
            initialTabIndex = TabBlue,
        )
    }
}

@Preview(showBackground = true, heightDp = 800, name = "주황보석 탭")
@Composable
private fun CurrencyShopScreenOrangeTabPreview() {
    LiroutiFrontendTheme {
        CurrencyShopScreen(
            actions = PreviewCurrencyShopScreenActions,
            selectedProductId = "orange_2",
            initialTabIndex = TabOrange,
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
                selectedProductId = "blue_2",
                initialTabIndex = TabBlue,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center,
            ) {
                CurrencyChargeDialogContent(
                    product = SampleBlueGemProducts[2],
                    onDismiss = {},
                    onConfirmCharge = {},
                )
            }
        }
    }
}
