package com.li_routi.feature.shopping.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.feature.shopping.navigation.ShopScreenActions
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 아이템 상점 화면 ViewModel.
 *
 * [ShopScreenActions]를 구현해 아이템 선택/뒤로가기/재화 chip/저장을 처리한다.
 * 실제 API/구매·의상 미리보기 로직은 이후 단계에서 추가한다.
 */
class ShopViewModel(
    initialState: ShopUiState = ShopUiState(),
) : BaseViewModel(), ShopScreenActions {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<ShopUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ShopUiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<ShopUiEvent> = _uiEvent.asSharedFlow()

    override fun onBackClick() {
        emitEvent(ShopUiEvent.NavigateBack)
    }

    override fun onOrangeGemClick() {
        emitEvent(ShopUiEvent.NavigateToCurrencyShop(tabIndex = 0))
    }

    override fun onBlueGemClick() {
        emitEvent(ShopUiEvent.NavigateToCurrencyShop(tabIndex = 1))
    }

    override fun onItemClick(itemId: String) {
        _uiState.update { state ->
            state.copy(
                selectedItemId = if (state.selectedItemId == itemId) null else itemId,
            )
        }
    }

    override fun onSaveClick() {
        emitEvent(ShopUiEvent.SaveSelectedItems)
    }

    private fun emitEvent(event: ShopUiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}
