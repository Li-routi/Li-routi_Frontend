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
import com.li_routi.feature.home.shop.screen.ShopScreen
import com.li_routi.feature.home.shop.vm.ShopUiEvent
import com.li_routi.feature.home.shop.vm.ShopUiState
import com.li_routi.feature.home.shop.vm.ShopViewModel

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

    Box(modifier = modifier.fillMaxSize()) {
        ShopScreen(
            actions = viewModel,
            nickname = uiState.nickname,
            coinBalance = uiState.coinBalance,
            gemBalance = uiState.gemBalance,
            items = uiState.items,
            selectedItemId = uiState.selectedItemId,
        )

        // 구매 실패(잔액 부족 등) 사유를 서버 메시지 그대로 보여줌
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
