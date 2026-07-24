package com.li_routi.feature.shopping.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.li_routi.feature.shopping.screen.CurrencyShopScreen
import com.li_routi.feature.shopping.vm.CurrencyShopUiEvent
import com.li_routi.feature.shopping.vm.CurrencyShopUiState
import com.li_routi.feature.shopping.vm.CurrencyShopViewModel

/**
 * 재화 구매 화면 진입점. [CurrencyShopViewModel]과 [CurrencyShopScreen]을 연결한다.
 *
 * @param onEvent 일회성 UI 이벤트 수신 콜백. Navigation/결제 연결은 여기서 다른 담당자가 구현한다.
 */
@Composable
fun CurrencyShopRoute(
    onEvent: (CurrencyShopUiEvent) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: CurrencyShopViewModel = viewModel {
        CurrencyShopViewModel(initialState = CurrencyShopUiState())
    },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            onEvent(event)
        }
    }

    CurrencyShopScreen(
        actions = viewModel,
        coinBalance = uiState.coinBalance,
        gemBalance = uiState.gemBalance,
        products = uiState.products,
        selectedProductId = uiState.selectedProductId,
        chargeDialogProduct = uiState.chargeDialogProduct,
        modifier = modifier,
    )
}
