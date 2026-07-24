package com.li_routi.feature.shopping.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.feature.shopping.navigation.CurrencyShopScreenActions
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 재화 구매 화면 ViewModel.
 *
 * 상품 선택(파란 테두리) → 재탭 시 충전 팝업 → 취소/충전하기 이벤트를 처리한다.
 */
class CurrencyShopViewModel(
    initialState: CurrencyShopUiState = CurrencyShopUiState(),
) : BaseViewModel(), CurrencyShopScreenActions {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<CurrencyShopUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<CurrencyShopUiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<CurrencyShopUiEvent> = _uiEvent.asSharedFlow()

    override fun onBackClick() {
        emitEvent(CurrencyShopUiEvent.NavigateBack)
    }

    override fun onProductClick(productId: String) {
        _uiState.update { state ->
            if (state.selectedProductId == productId) {
                state.copy(chargeDialogProductId = productId)
            } else {
                state.copy(
                    selectedProductId = productId,
                    chargeDialogProductId = null,
                )
            }
        }
    }

    override fun onDismissChargeDialog() {
        _uiState.update { it.copy(chargeDialogProductId = null) }
    }

    override fun onConfirmChargeClick() {
        val productId = _uiState.value.chargeDialogProductId ?: return
        _uiState.update { it.copy(chargeDialogProductId = null) }
        emitEvent(CurrencyShopUiEvent.ConfirmCharge(productId))
    }

    private fun emitEvent(event: CurrencyShopUiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}
