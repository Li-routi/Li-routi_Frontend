package com.li_routi.feature.home.shop.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.li_routi.feature.home.shop.component.PurchaseConfirmDialog
import com.li_routi.feature.home.shop.screen.ShopScreen
import com.li_routi.feature.home.shop.vm.ShopUiEvent
import com.li_routi.feature.home.shop.vm.ShopUiState
import com.li_routi.feature.home.shop.vm.ShopViewModel
import kotlinx.coroutines.delay

/** 구매 실패 사유까지 읽을 시간은 주되 계속 남지는 않게 함 */
private const val ToastDurationMillis = 3_000L

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

    DisposableEffect(viewModel) {
        onDispose { viewModel.clearPurchaseSelection() }
    }

    // 새 문구가 오면 타이머를 다시 시작하려고 메시지를 key로 둠
    LaunchedEffect(uiState.message) {
        if (uiState.message == null) return@LaunchedEffect
        delay(ToastDurationMillis)
        viewModel.onDismissMessage()
    }

    Box(modifier = modifier.fillMaxSize()) {
        ShopScreen(
            actions = viewModel,
            nickname = uiState.nickname,
            coinBalance = uiState.coinBalance,
            gemBalance = uiState.gemBalance,
            selectedMainTab = uiState.selectedMainTab,
            categories = uiState.categories,
            equipped = uiState.equipped,
            savedEquippedItemIds = uiState.savedEquippedItemIds,
            selectedCategoryIndex = uiState.selectedCategoryIndex,
            showOwnedOnly = uiState.showOwnedOnly,
            items = uiState.items,
            isLoading = uiState.isLoading,
            selectedItemIds = uiState.selectedItems.keys,
            purchaseTargets = uiState.purchaseTargets,
            hasUnsavedChanges = uiState.hasUnsavedChanges,
            previewCharacterId = uiState.previewCharacterId,
            previewLayers = uiState.previewLayers,
            savedCharacterId = uiState.savedCharacterId,
        )

        if (uiState.isPurchaseConfirmVisible) {
            PurchaseConfirmDialog(
                targets = uiState.purchaseTargets,
                // 다이얼로그는 아이템을 고른 뒤에만 뜨므로 이 시점엔 잔액 조회가 이미 끝나 있다.
                coinBalance = uiState.coinBalance ?: 0,
                gemBalance = uiState.gemBalance ?: 0,
                onRemoveItem = viewModel::onItemClick,
                onDismissRequest = viewModel::onPurchaseDialogDismiss,
                onConfirmPurchase = viewModel::onPurchaseConfirmClick,
                onChargeClick = viewModel::onPurchaseChargeClick,
            )
        }

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
