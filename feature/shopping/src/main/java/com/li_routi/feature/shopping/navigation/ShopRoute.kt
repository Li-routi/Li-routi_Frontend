package com.li_routi.feature.shopping.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.li_routi.feature.shopping.screen.ShopScreen
import com.li_routi.feature.shopping.vm.ShopUiEvent
import com.li_routi.feature.shopping.vm.ShopUiState
import com.li_routi.feature.shopping.vm.ShopViewModel

/**
 * 아이템 상점 화면 진입점. [ShopViewModel]과 [ShopScreen]을 연결한다.
 *
 * @param onEvent 일회성 UI 이벤트 수신 콜백. Navigation 연결은 여기서 다른 담당자가 구현한다.
 */
@Composable
fun ShopRoute(
    onEvent: (ShopUiEvent) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ShopViewModel = viewModel {
        ShopViewModel(initialState = ShopUiState())
    },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            onEvent(event)
        }
    }

    ShopScreen(
        actions = viewModel,
        nickname = uiState.nickname,
        coinBalance = uiState.coinBalance,
        gemBalance = uiState.gemBalance,
        items = uiState.items,
        modifier = modifier,
    )
}
