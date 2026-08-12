package com.li_routi.feature.home.shop.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.LiroutiToast
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.li_routi.feature.home.shop.screen.CurrencyShopScreen
import com.li_routi.feature.home.shop.vm.CurrencyShopUiEvent
import com.li_routi.feature.home.shop.vm.CurrencyShopUiState
import com.li_routi.feature.home.shop.vm.CurrencyShopViewModel

/**
 * 재화 구매 화면 진입점. [CurrencyShopViewModel]과 [CurrencyShopScreen]을 연결한다.
 *
 * @param onEvent 일회성 UI 이벤트 수신 콜백. Navigation/결제 연결은 여기서 다른 담당자가 구현한다.
 */
@Composable
fun CurrencyShopRoute(
    onEvent: (CurrencyShopUiEvent) -> Unit = {},
    modifier: Modifier = Modifier,
    initialTabIndex: Int = 0,
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

    Box(modifier = modifier.fillMaxSize()) {
        CurrencyShopScreen(
            actions = viewModel,
            coinBalance = uiState.coinBalance,
            gemBalance = uiState.gemBalance,
            orangeProducts = uiState.orangeProducts,
            blueProducts = uiState.blueProducts,
            selectedProductId = uiState.selectedProductId,
            chargeDialogProduct = uiState.chargeDialogProduct,
            initialTabIndex = initialTabIndex,
        )

        // 교환 실패(잔액 부족 등) 사유를 서버 메시지 그대로 보여줌
        uiState.message?.let { message ->
            LiroutiToast(
                message = message,
                onCloseClick = viewModel::onDismissMessage,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 100.dp),
            )
        }
    }
}
