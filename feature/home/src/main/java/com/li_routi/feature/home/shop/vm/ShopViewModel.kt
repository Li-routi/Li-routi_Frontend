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

    /** 탭을 옮겨도 고른 것은 그대로 둠 — 여러 탭에서 고른 걸 한 번에 사는 게 목적임 */
    override fun onCategorySelected(index: Int) {
        if (_uiState.value.selectedCategoryIndex == index) return
        _uiState.update { it.copy(selectedCategoryIndex = index) }
        loadItems()
    }

    override fun onOwnedOnlyChange(ownedOnly: Boolean) {
        if (_uiState.value.showOwnedOnly == ownedOnly) return
        _uiState.update { it.copy(showOwnedOnly = ownedOnly) }
        loadItems()
    }

    /** 지금 입고 있는 착장. 안 입은 자리는 응답에 실리지 않아서 그대로 비워둠 */
    private fun loadEquipped() {
        viewModelScope.launch {
            val result = getMyAvatarUseCase()
            if (result is ResultState.Success) {
                _uiState.update { it.applyServerAvatar(result.data) }
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

    /** 구매 직후처럼 목록 갱신을 기다려야 하면 여기로 감. 탭을 옮기면 [itemsJob]이 취소돼 옛 응답이 덮지 않음 */
    private suspend fun awaitItemsRefresh() {
        itemsJob?.cancel()
        val job = viewModelScope.launch { refreshItems() }
        itemsJob = job
        job.join()
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
     * 셀 탭. 안 산 것도 캐릭터에 바로 올려서 입어볼 수 있게 함.
     *
     * 구매 선택은 선택한 집합으로 판단함. 같은 자리 미리보기는 마지막에 고른 것만 올라감 —
     * 다른 옷을 고른 뒤 이전 옷을 다시 누르면 구매 목록에서만 빠지고, 미리보기는 그대로 둠
     */
    override fun onItemClick(itemId: String) {
        _uiState.update { state ->
            val item = state.items.firstOrNull { it.id == itemId } ?: return@update state
            val numericId = item.id.toLongOrNull() ?: return@update state
            val unselecting = itemId in state.selectedItems

            val equipped = when {
                item.slot.isEmpty() -> state.equipped
                unselecting && state.equipped[item.slot]?.itemId == numericId ->
                    state.equipped - item.slot
                unselecting -> state.equipped
                else -> state.equipped + (item.slot to EquippedUiModel(
                    itemId = numericId,
                    imageUrl = item.imageUrl,
                    owned = item.owned,
                ))
            }
            val selectedItems = if (unselecting) {
                state.selectedItems - itemId
            } else {
                state.selectedItems + (itemId to item)
            }
            state.copy(selectedItems = selectedItems, equipped = equipped)
        }
    }

    /**
     * 하단 버튼. 고른 것 중 안 산 게 있으면 그것들을 사고, 다 샀으면 착장을 저장함.
     *
     * 구매는 가격·결제 재화를 서버가 갖고 있어서 body가 없음
     */
    override fun onSaveClick() {
        val state = _uiState.value
        if (state.isPurchasing || state.isEquipping) return
        val targets = state.purchaseTargets
        if (targets.isEmpty()) {
            equipSelected()
            return
        }
        purchaseAll(targets)
    }

    /**
     * 고른 아이템을 차례로 사들임.
     *
     * 서버에 일괄 구매 엔드포인트가 없어서 하나씩 부름. 하나라도 실패하면 거기서 멈춤 —
     * 잔액이 모자란 경우가 대부분이라 밀어붙여도 같은 이유로 계속 실패함.
     * 앞서 산 것은 되돌릴 수 없으니 몇 개가 넘어갔는지 함께 알려줌
     */
    private fun purchaseAll(targets: List<ShopItemUiModel>) {
        _uiState.update { it.copy(isPurchasing = true) }
        viewModelScope.launch {
            try {
                val purchased = mutableListOf<String>()
                var latestAvatar: MemberAvatar? = null
                var error: String? = null

                for (target in targets) {
                    val itemId = target.id.toLongOrNull() ?: continue
                    when (val result = purchaseShopAvatarItemUseCase(itemId)) {
                        is ResultState.Success -> {
                            purchased += target.name
                            // 구매하면 서버가 그 자리에 바로 입혀줘서 응답이 곧 새 착장임
                            latestAvatar = result.data
                            _uiState.update { it.copy(selectedItems = it.selectedItems - target.id) }
                        }

                        is ResultState.Error -> error = result.message
                        ResultState.Loading -> Unit
                    }
                    if (error != null) break
                }

                _uiState.update { state ->
                    val next = latestAvatar?.let { state.applyServerAvatar(it) } ?: state
                    next.copy(message = purchaseMessageOf(purchased, error))
                }
                if (purchased.isNotEmpty()) {
                    // 갱신을 기다려야 함. 먼저 풀어주면 owned가 반영되기 전에 또 살 수 있음
                    awaitItemsRefresh()
                    loadBalances()
                    emitEvent(ShopUiEvent.SaveSelectedItems)
                }
            } finally {
                _uiState.update { it.copy(isPurchasing = false) }
            }
        }
    }

    /**
     * 지금 올려둔 착장을 통째로 저장함. 서버가 보낸 목록을 곧 전체 착장으로 봄.
     *
     * 미리보기로만 올려둔 안 산 아이템은 서버가 받아주지 않아서 빼고 보냄
     */
    private fun equipSelected() {
        val state = _uiState.value
        if (state.isEquipping) return

        val itemIds = state.equipped.values.filter { it.owned }.map { it.itemId }
        _uiState.update { it.copy(isEquipping = true) }
        viewModelScope.launch {
            try {
                when (val result = equipAvatarUseCase(itemIds)) {
                    is ResultState.Success -> _uiState.update {
                        it.applyServerAvatar(result.data).copy(message = "저장했어요.")
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

/** 하나씩 사기 때문에 중간에 끊길 수 있음 — 몇 개가 넘어갔는지 같이 밝힘 */
private fun purchaseMessageOf(purchased: List<String>, error: String?): String = when {
    purchased.isEmpty() -> error ?: "아이템 정보를 불러오지 못했어요."
    error == null && purchased.size == 1 -> "${purchased.first()}을(를) 구매했어요."
    error == null -> "${purchased.size}개를 구매했어요."
    else -> "${purchased.size}개만 구매했어요. $error"
}

private fun List<CurrencyBalance>.balanceOf(currency: String): Int? =
    firstOrNull { it.currency == currency }?.balance

/**
 * 서버가 준 착장으로 화면과 저장 상태를 함께 맞춤.
 *
 * 자리마다 하나씩이라 자리를 키로 씀
 */
private fun ShopUiState.applyServerAvatar(avatar: MemberAvatar): ShopUiState = copy(
    equipped = avatar.equipped.associate {
        it.slot to EquippedUiModel(itemId = it.itemId, imageUrl = it.imageUrl)
    },
    savedEquippedItemIds = avatar.equipped.mapTo(mutableSetOf()) { it.itemId },
)

private fun ShopAvatarItem.toUiModel(): ShopItemUiModel = ShopItemUiModel(
    id = id.toString(),
    name = name,
    price = price.toInt(),
    slot = slot,
    currency = currency,
    imageUrl = imageUrl,
    owned = owned,
)
