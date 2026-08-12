package com.li_routi.feature.home.shop.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.di.ShopContainer
import com.li_routi.core.domain.shop.GetShopAvatarItemsUseCase
import com.li_routi.core.domain.shop.PurchaseShopAvatarItemUseCase
import com.li_routi.core.domain.shop.ShopAvatarItem
import com.li_routi.feature.home.shop.component.ShopItemUiModel
import com.li_routi.feature.home.shop.navigation.ShopScreenActions
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
 * [ShopScreenActions]를 구현해 아이템 선택/뒤로가기/재화 chip/저장을 처리함.
 */
class ShopViewModel(
    initialState: ShopUiState = ShopUiState(),
    private val getShopAvatarItemsUseCase: GetShopAvatarItemsUseCase = ShopContainer.getShopAvatarItemsUseCase,
    private val purchaseShopAvatarItemUseCase: PurchaseShopAvatarItemUseCase = ShopContainer.purchaseShopAvatarItemUseCase,
) : BaseViewModel(), ShopScreenActions {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<ShopUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ShopUiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<ShopUiEvent> = _uiEvent.asSharedFlow()

    init {
        loadItems()
    }

    fun onDismissMessage() {
        _uiState.update { it.copy(message = null) }
    }

    /** 상점 격자에 뿌릴 아이템을 불러옴. 보유한 것도 같이 내려와서 owned로 구분함 */
    fun loadItems() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            when (val result = getShopAvatarItemsUseCase()) {
                is ResultState.Success -> _uiState.update { state ->
                    state.copy(isLoading = false, items = result.data.map { it.toUiModel() })
                }

                is ResultState.Error -> _uiState.update {
                    it.copy(isLoading = false, message = result.message)
                }

                ResultState.Loading -> Unit
            }
        }
    }

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

    /**
     * 고른 아이템을 구매함. 서버가 사자마자 그 자리에 입혀줌.
     *
     * 가격이랑 결제 재화는 서버가 갖고 있어서 요청 body가 없음
     */
    override fun onSaveClick() {
        val state = _uiState.value
        val selected = state.items.firstOrNull { it.id == state.selectedItemId }
        if (selected == null) {
            _uiState.update { it.copy(message = "아이템을 선택해주세요.") }
            return
        }
        if (selected.owned) {
            _uiState.update { it.copy(message = "이미 보유한 아이템이에요.") }
            return
        }
        val itemId = selected.id.toLongOrNull() ?: run {
            _uiState.update { it.copy(message = "아이템 정보를 불러오지 못했어요.") }
            return
        }
        if (state.isPurchasing) return

        _uiState.update { it.copy(isPurchasing = true) }
        viewModelScope.launch {
            try {
                when (val result = purchaseShopAvatarItemUseCase(itemId)) {
                    is ResultState.Success -> {
                        _uiState.update { it.copy(message = "${selected.name}을(를) 구매했어요.") }
                        // 보유 상태랑 잔액이 바뀌어서 목록을 다시 받아야 함
                        loadItems()
                        emitEvent(ShopUiEvent.SaveSelectedItems)
                    }

                    is ResultState.Error -> _uiState.update { it.copy(message = result.message) }
                    ResultState.Loading -> Unit
                }
            } finally {
                _uiState.update { it.copy(isPurchasing = false) }
            }
        }
    }

    private fun emitEvent(event: ShopUiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}

private fun ShopAvatarItem.toUiModel(): ShopItemUiModel = ShopItemUiModel(
    id = id.toString(),
    name = name,
    price = price.toInt(),
    imageUrl = imageUrl,
    owned = owned,
)
