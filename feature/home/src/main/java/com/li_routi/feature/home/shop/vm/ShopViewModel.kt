package com.li_routi.feature.home.shop.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.di.AuthContainer
import com.li_routi.core.data.di.ShopContainer
import com.li_routi.core.domain.auth.GetMyInfoUseCase
import com.li_routi.core.domain.shop.CurrencyBalance
import com.li_routi.core.domain.shop.EquipAvatarUseCase
import com.li_routi.core.domain.shop.GetMyAvatarUseCase
import com.li_routi.core.domain.shop.GetShopAvatarItemsUseCase
import com.li_routi.core.domain.shop.GetShopCategoriesUseCase
import com.li_routi.core.domain.shop.GetWalletBalancesUseCase
import com.li_routi.core.domain.shop.PurchaseShopAvatarItemUseCase
import com.li_routi.core.domain.shop.MemberAvatar
import com.li_routi.core.domain.shop.ShopAvatarItem
import com.li_routi.feature.home.shop.component.ShopItemUiModel
import com.li_routi.feature.home.shop.navigation.ShopScreenActions
import kotlinx.coroutines.Job
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
    private val getShopCategoriesUseCase: GetShopCategoriesUseCase = ShopContainer.getShopCategoriesUseCase,
    private val getShopAvatarItemsUseCase: GetShopAvatarItemsUseCase = ShopContainer.getShopAvatarItemsUseCase,
    private val purchaseShopAvatarItemUseCase: PurchaseShopAvatarItemUseCase = ShopContainer.purchaseShopAvatarItemUseCase,
    private val getWalletBalancesUseCase: GetWalletBalancesUseCase = ShopContainer.getWalletBalancesUseCase,
    private val getMyInfoUseCase: GetMyInfoUseCase = AuthContainer.getMyInfoUseCase,
    private val getMyAvatarUseCase: GetMyAvatarUseCase = ShopContainer.getMyAvatarUseCase,
    private val equipAvatarUseCase: EquipAvatarUseCase = ShopContainer.equipAvatarUseCase,
) : BaseViewModel(), ShopScreenActions {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<ShopUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ShopUiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<ShopUiEvent> = _uiEvent.asSharedFlow()

    private var itemsJob: Job? = null

    init {
        loadNickname()
        loadEquipped()
        loadCategories()
        loadItems()
        loadBalances()
    }

    /**
     * 상단 탭. 서버가 내려준 순서 그대로 그림.
     *
     * 캐릭터 탭(`source == "CHARACTER"`)은 캐릭터 목록 API가 아직 없어서 뺌 —
     * 나중에 API가 생기면 서버 목록에 그대로 실려 오므로 여기만 풀면 됨
     */
    private fun loadCategories() {
        viewModelScope.launch {
            val result = getShopCategoriesUseCase()
            if (result is ResultState.Success) {
                val categories = result.data
                    .filter { it.source != CharacterSource }
                    .map { ShopCategoryUiModel(key = it.key, name = it.name, slot = it.slot) }
                _uiState.update { state ->
                    state.copy(
                        categories = categories,
                        // 탭이 줄어들 수 있어서 선택 위치가 목록 밖으로 나가지 않게 맞춤
                        selectedCategoryIndex = state.selectedCategoryIndex
                            .coerceAtMost((categories.size - 1).coerceAtLeast(0)),
                    )
                }
            }
        }
    }

    override fun onCategorySelected(index: Int) {
        if (_uiState.value.selectedCategoryIndex == index) return
        _uiState.update { it.copy(selectedCategoryIndex = index, selectedItemId = null) }
        loadItems()
    }

    override fun onOwnedOnlyChange(ownedOnly: Boolean) {
        if (_uiState.value.showOwnedOnly == ownedOnly) return
        _uiState.update { it.copy(showOwnedOnly = ownedOnly, selectedItemId = null) }
        loadItems()
    }

    /** 지금 입고 있는 착장. 안 입은 자리는 응답에 실리지 않아서 그대로 비워둠 */
    private fun loadEquipped() {
        viewModelScope.launch {
            val result = getMyAvatarUseCase()
            if (result is ResultState.Success) {
                _uiState.update { it.copy(equipped = result.data.toEquippedMap()) }
            }
        }
    }

    /** 상단 캐릭터 카드에 쓸 내 닉네임 */
    private fun loadNickname() {
        viewModelScope.launch {
            val result = getMyInfoUseCase()
            if (result is ResultState.Success) {
                _uiState.update { it.copy(nickname = result.data.nickname) }
            }
        }
    }

    /** 상점 헤더 잔액. 구매 후에도 다시 불러서 서버 값과 어긋나지 않게 함 */
    private fun loadBalances() {
        viewModelScope.launch {
            val result = getWalletBalancesUseCase()
            if (result is ResultState.Success) {
                _uiState.update { state ->
                    state.copy(
                        coinBalance = result.data.balanceOf("TOPAZ") ?: state.coinBalance,
                        gemBalance = result.data.balanceOf("GEM") ?: state.gemBalance,
                    )
                }
            }
        }
    }

    fun onDismissMessage() {
        _uiState.update { it.copy(message = null) }
    }

    /** 상점 격자에 뿌릴 아이템을 불러옴. 보유한 것도 같이 내려와서 owned로 구분함 */
    fun loadItems() {
        // 탭을 빠르게 옮기면 늦게 온 응답이 나중에 덮어써서, 이전 조회는 버림
        itemsJob?.cancel()
        itemsJob = viewModelScope.launch { refreshItems() }
    }

    /** 목록 갱신을 기다려야 하는 곳(구매 직후)에서도 쓸 수 있게 suspend로 둠 */
    private suspend fun refreshItems() {
        val state = _uiState.value
        // 전체 탭은 slot 없이 부르는 것이라 null을 그대로 넘김
        val slot = state.categories.getOrNull(state.selectedCategoryIndex)?.slot
        val ownedOnly = state.showOwnedOnly.takeIf { it }
        _uiState.update { it.copy(isLoading = true) }
        when (val result = getShopAvatarItemsUseCase(slot = slot, ownedOnly = ownedOnly)) {
            is ResultState.Success -> _uiState.update { state ->
                state.copy(isLoading = false, items = result.data.map { it.toUiModel() })
            }

            // 실패하면 이전 목록을 남기지 않음 — 없는 상품을 고를 수 있게 되면 안 됨
            is ResultState.Error -> _uiState.update {
                it.copy(isLoading = false, items = emptyList(), message = result.message)
            }

            ResultState.Loading -> Unit
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

    /**
     * 셀 탭. 보유한 아이템이면 저장 전에도 캐릭터에 바로 올려서 보여줌.
     *
     * 같은 자리에는 하나만 입을 수 있어서 그 자리를 덮어씀. 다시 누르면 벗음
     */
    override fun onItemClick(itemId: String) {
        _uiState.update { state ->
            val unselecting = state.selectedItemId == itemId
            val item = state.items.firstOrNull { it.id == itemId }
            val equipped = when {
                item == null || !item.owned || item.slot.isEmpty() -> state.equipped
                unselecting -> state.equipped - item.slot
                else -> state.equipped + (item.slot to EquippedUiModel(
                    itemId = item.id.toLongOrNull() ?: return@update state,
                    imageUrl = item.imageUrl,
                ))
            }
            state.copy(
                selectedItemId = if (unselecting) null else itemId,
                equipped = equipped,
            )
        }
    }

    /**
     * 하단 버튼. 고른 아이템을 아직 안 샀으면 구매, 이미 샀으면 착장을 저장함.
     *
     * 구매는 서버가 사자마자 그 자리에 입혀주고, 가격·결제 재화도 서버가 갖고 있어서 body가 없음
     */
    override fun onSaveClick() {
        val state = _uiState.value
        val selected = state.items.firstOrNull { it.id == state.selectedItemId }
        if (selected == null || selected.owned) {
            equipSelected()
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
                        // 구매하면 서버가 그 자리에 바로 입혀줘서 응답이 곧 새 착장임
                        _uiState.update {
                            it.copy(
                                equipped = result.data.toEquippedMap(),
                                message = "${selected.name}을(를) 구매했어요.",
                            )
                        }
                        // 갱신을 기다려야 함. 먼저 풀어주면 owned가 반영되기 전에 또 살 수 있음
                        refreshItems()
                        loadBalances()
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

    /** 지금 올려둔 착장을 통째로 저장함. 서버가 보낸 목록을 곧 전체 착장으로 봄 */
    private fun equipSelected() {
        val state = _uiState.value
        if (state.isEquipping) return

        _uiState.update { it.copy(isEquipping = true) }
        viewModelScope.launch {
            try {
                when (val result = equipAvatarUseCase(state.equipped.values.map { it.itemId })) {
                    is ResultState.Success -> _uiState.update {
                        it.copy(equipped = result.data.toEquippedMap(), message = "저장했어요.")
                    }

                    // 서버가 부분 성공을 안 줘서 실패하면 저장 전 상태 그대로 둠
                    is ResultState.Error -> _uiState.update { it.copy(message = result.message) }
                    ResultState.Loading -> Unit
                }
            } finally {
                _uiState.update { it.copy(isEquipping = false) }
            }
        }
    }

    private fun emitEvent(event: ShopUiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}

/** 캐릭터 목록 API가 아직 없어서 이 탭은 그리지 않음 */
private const val CharacterSource = "CHARACTER"

private fun List<CurrencyBalance>.balanceOf(currency: String): Int? =
    firstOrNull { it.currency == currency }?.balance

/** 자리마다 하나씩이라 자리를 키로 씀 */
private fun MemberAvatar.toEquippedMap(): Map<String, EquippedUiModel> =
    equipped.associate { it.slot to EquippedUiModel(itemId = it.itemId, imageUrl = it.imageUrl) }

private fun ShopAvatarItem.toUiModel(): ShopItemUiModel = ShopItemUiModel(
    id = id.toString(),
    name = name,
    price = price.toInt(),
    slot = slot,
    currency = currency,
    imageUrl = imageUrl,
    owned = owned,
)
