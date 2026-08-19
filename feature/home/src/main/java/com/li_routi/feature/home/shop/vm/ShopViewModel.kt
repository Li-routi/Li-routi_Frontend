package com.li_routi.feature.home.shop.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.appearance.MemberAppearanceStore
import com.li_routi.core.data.di.AuthContainer
import com.li_routi.core.data.di.CharacterContainer
import com.li_routi.core.data.di.ShopContainer
import com.li_routi.core.domain.auth.GetMyInfoUseCase
import com.li_routi.core.domain.character.Character
import com.li_routi.core.domain.character.GetCharactersUseCase
import com.li_routi.core.domain.shop.CurrencyBalance
import com.li_routi.core.domain.shop.EquipAvatarUseCase
import com.li_routi.core.domain.shop.GetShopAvatarItemsUseCase
import com.li_routi.core.domain.shop.GetShopCategoriesUseCase
import com.li_routi.core.domain.shop.GetWalletBalancesUseCase
import com.li_routi.core.domain.shop.PurchaseShopItemsUseCase
import com.li_routi.core.domain.shop.MemberAvatar
import com.li_routi.core.domain.shop.ShopAvatarItem
import com.li_routi.core.domain.shop.ShopPurchasePayment
import com.li_routi.feature.home.shop.component.ShopItemUiModel
import com.li_routi.feature.home.shop.navigation.ShopScreenActions
import java.util.UUID
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
    private val purchaseShopItemsUseCase: PurchaseShopItemsUseCase = ShopContainer.purchaseShopItemsUseCase,
    private val getWalletBalancesUseCase: GetWalletBalancesUseCase = ShopContainer.getWalletBalancesUseCase,
    private val getMyInfoUseCase: GetMyInfoUseCase = AuthContainer.getMyInfoUseCase,
    private val equipAvatarUseCase: EquipAvatarUseCase = ShopContainer.equipAvatarUseCase,
    private val getCharactersUseCase: GetCharactersUseCase = CharacterContainer.getCharactersUseCase,
    private val appearanceStore: MemberAppearanceStore = ShopContainer.memberAppearanceStore,
) : BaseViewModel(), ShopScreenActions {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<ShopUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ShopUiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<ShopUiEvent> = _uiEvent.asSharedFlow()

    private var itemsJob: Job? = null
    /** 늦게 온 착장 GET이 사용자가 고른 미리보기를 덮지 않게 */
    private var hasUserPreviewed = false

    // 재시도 시 같은 키를 보내야 두 번 결제되지 않음. 장바구니(아이템 id 집합)별로 진행 중인 키를
    // 들고 있다가 성공하면 버림 — 장바구니가 바뀌면 다른 키로 새로 발급된다(서버가 같은 키에 다른
    // 장바구니가 오면 거절함).
    private val purchaseKeys = mutableMapOf<Set<String>, String>()

    init {
        loadNickname()
        hydrateAppearance()
        loadCategories()
        loadBalances()
    }

    /**
     * "의상" 탭 하위 필터. 서버가 내려준 순서 그대로 그림.
     *
     * "캐릭터"는 목록 API가 없는 별도 최상위 탭([ShopMainTab.CHARACTER])이라 여기 안 섞는다
     * (Figma node `6057:20320`: 상위 탭 "캐릭터"/"의상" + "의상" 하위 필터로 분리된 구조).
     */
    private fun loadCategories() {
        viewModelScope.launch {
            val result = getShopCategoriesUseCase()
            if (result is ResultState.Success) {
                val categories = result.data
                    // 캐릭터는 별도 최상위 탭이라 "의상" 하위 필터에 섞으면 안 되는데, 그동안
                    // 걸러내는 코드가 없어서 그대로 새고 있었음
                    .filterNot { it.source == "CHARACTER" }
                    .map {
                        ShopCategoryUiModel(
                            key = it.key,
                            name = it.name,
                            source = it.source,
                            slot = it.slot,
                        )
                    }
                _uiState.update { state ->
                    state.copy(
                        categories = categories,
                        selectedCategoryIndex = state.selectedCategoryIndex
                            .coerceAtMost((categories.size - 1).coerceAtLeast(0)),
                    )
                }
            }
            // 탭을 못 받아도 전체 목록은 불러야 빈 상점이 안 됨
            loadItems()
        }
    }

    /**
     * 최상위 탭("캐릭터"/"의상") 전환. 하위 필터 선택은 유지해서 "의상"으로 돌아오면 그대로 보임.
     *
     * 새 목록이 올 때까지 이전 탭 것을 그대로 두면, 캐릭터 id와 상점 아이템 id가 같은 문자열로
     * 우연히 겹칠 때 그리드가 서로 다른 아이템을 같은 자리로 착각해 잠깐 깜빡인다 — 바로 비운다
     */
    override fun onMainTabSelected(tab: ShopMainTab) {
        if (_uiState.value.selectedMainTab == tab) return
        _uiState.update { it.copy(selectedMainTab = tab, items = emptyList()) }
        loadItems()
    }

    /**
     * 상점에 들어올 때마다 서버 착장을 다시 받아 홈과 맞춤.
     * 이미 미리보기 중이면 저장본만 갱신하고 캐릭터 카드는 그대로 둠 —
     * 늦게 온 응답이 고른 옷을 덮어쓰지 않게
     */
    private fun hydrateAppearance() {
        viewModelScope.launch {
            appearanceStore.reloadAvatar()
            val appearance = appearanceStore.appearance.value
            val saved = MemberAvatar(equipped = appearance.equipped, layers = appearance.layers)
            _uiState.update { state ->
                val hydrated = state.applyServerAvatar(saved)
                if (hasUserPreviewed) {
                    state.copy(
                        savedEquippedItemIds = hydrated.savedEquippedItemIds,
                        savedLayers = hydrated.savedLayers,
                        savedCharacterId = appearance.characterId,
                    )
                } else {
                    hydrated.copy(
                        savedCharacterId = appearance.characterId,
                        previewCharacterId = appearance.characterId,
                        previewCharacterImageUrl = appearance.characterImageUrl,
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

    /** 재화 상점 등 다른 화면으로 나가면 구매 선택은 풀어줌. 미리보기는 그대로 둠 */
    fun clearPurchaseSelection() {
        _uiState.update { it.copy(selectedItems = emptyMap()) }
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
        if (state.selectedMainTab == ShopMainTab.CHARACTER) {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getCharactersUseCase()) {
                is ResultState.Success -> _uiState.update {
                    it.copy(isLoading = false, items = result.data.map { character -> character.toShopItem() })
                }
                is ResultState.Error -> _uiState.update {
                    it.copy(isLoading = false, items = emptyList(), message = result.message)
                }
                ResultState.Loading -> Unit
            }
            return
        }
        // 전체 탭은 slot 없이 부르는 것이라 null을 그대로 넘김
        val category = state.categories.getOrNull(state.selectedCategoryIndex)
        val slot = category?.slot
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
        onDismissMessage()
        emitEvent(ShopUiEvent.NavigateToCurrencyShop(tabIndex = 0))
    }

    override fun onBlueGemClick() {
        onDismissMessage()
        emitEvent(ShopUiEvent.NavigateToCurrencyShop(tabIndex = 1))
    }

    /**
     * 셀 탭. 안 산 것도 캐릭터에 바로 올려서 입어볼 수 있게 함.
     *
     * 구매 선택은 선택한 집합으로 판단함. 같은 자리 미리보기는 마지막에 고른 것만 올라감 —
     * 다른 옷을 고른 뒤 이전 옷을 다시 누르면 구매 목록에서만 빠지고, 미리보기는 그대로 둠.
     * 보유중은 구매 선택에 넣지 않음
     */
    override fun onItemClick(itemId: String) {
        hasUserPreviewed = true
        _uiState.update { state ->
            val item = state.items.firstOrNull { it.id == itemId } ?: return@update state
            if (state.selectedMainTab == ShopMainTab.CHARACTER) {
                // 알 상태(unlocked=false)는 서버가 선택을 거절하므로 미리보기 자체를 막는다 —
                // ShopItemUiModel.owned는 여기서 항상 true로 채워져 있어(등급 격자 UI 유지용) 대신
                // unlocked로 가른다.
                if (!item.unlocked) return@update state
                val characterId = item.id.toLongOrNull() ?: return@update state
                return@update state.copy(
                    previewCharacterId = characterId,
                    previewCharacterImageUrl = item.imageUrl,
                )
            }
            val numericId = item.id.toLongOrNull() ?: return@update state
            val slot = item.slot.uppercase()
            val unselecting = itemId in state.selectedItems
            val isActivePreview = slot.isNotEmpty() && state.equipped[slot]?.itemId == numericId

            val equipped = when {
                slot.isEmpty() -> state.equipped
                item.owned && isActivePreview -> state.equipped - slot
                unselecting && isActivePreview -> state.equipped - slot
                unselecting -> state.equipped
                else -> state.equipped + (slot to EquippedUiModel(
                    itemId = numericId,
                    imageUrl = item.imageUrl,
                    owned = item.owned,
                ))
            }
            val selectedItems = when {
                item.owned -> state.selectedItems - itemId
                unselecting -> state.selectedItems - itemId
                else -> state.selectedItems + (itemId to item.copy(slot = slot))
            }
            state.copy(selectedItems = selectedItems, equipped = equipped)
        }
    }

    /**
     * 하단 버튼. 고른 것 중 안 산 게 있으면 구매 확인 다이얼로그를 띄우고, 다 샀으면(=구매 대상이
     * 없으면) 바로 착장을 저장함 — 저장은 결제가 아니라 확인이 필요 없음.
     */
    override fun onSaveClick() {
        val state = _uiState.value
        if (state.isPurchasing || state.isEquipping) return
        if (state.purchaseTargets.isNotEmpty()) {
            _uiState.update { it.copy(isPurchaseConfirmVisible = true) }
            return
        }
        // 캐릭터 선택을 먼저 서버에 반영한 뒤 착장을 저장함. 동시에 보내면 착장 PUT 응답
        // layers가 옛 CHARACTER를 들고 와 홈이 잠깐 이전 새를 그릴 수 있음.
        viewModelScope.launch {
            if (persistPreviewCharacter()) equipSelected()
        }
    }

    /** 구매 확인 다이얼로그의 "취소" 또는 바깥 탭 — 선택은 그대로 두고 다이얼로그만 닫음 */
    fun onPurchaseDialogDismiss() {
        _uiState.update { it.copy(isPurchaseConfirmVisible = false) }
    }

    /** 구매 확인 다이얼로그의 "구매하기" — 실제 결제를 진행함 */
    fun onPurchaseConfirmClick() {
        val state = _uiState.value
        _uiState.update { it.copy(isPurchaseConfirmVisible = false) }
        viewModelScope.launch {
            if (persistPreviewCharacter()) purchaseAll(state.purchaseTargets)
        }
    }

    /**
     * 구매 확인 다이얼로그의 "충전하러 가기" — 잔액이 모자란 재화의 충전 탭으로 보냄.
     * 둘 다 모자라면 블루젬(GEM) 탭을 우선 보여줌.
     */
    fun onPurchaseChargeClick() {
        val state = _uiState.value
        _uiState.update { it.copy(isPurchaseConfirmVisible = false) }
        onDismissMessage()
        emitEvent(ShopUiEvent.NavigateToCurrencyShop(tabIndex = if (state.isGemShort) 1 else 0))
    }

    /** 고른 캐릭터를 서버에 저장함(`PUT /api/characters/selection`). 홈은 같은 캐시를 보고 바로 따라옴 */
    private suspend fun persistPreviewCharacter(): Boolean {
        val state = _uiState.value
        if (state.previewCharacterId == state.savedCharacterId) return true
        return when (val result = appearanceStore.saveCharacter(state.previewCharacterId, state.previewCharacterImageUrl)) {
            is ResultState.Success -> {
                _uiState.update {
                    it.copy(
                        savedCharacterId = it.previewCharacterId,
                        savedLayers = appearanceStore.appearance.value.layers,
                    )
                }
                true
            }
            is ResultState.Error -> {
                _uiState.update { it.copy(message = result.message) }
                false
            }
            ResultState.Loading -> false
        }
    }

    /**
     * 고른 아이템을 한 번에 삼(`POST /api/shop/items/purchase`). 재화가 섞여도 되고, 전부 되거나
     * 전부 안 됨(부분 성공 없음). 이 응답 자체는 착용을 바꾸지 않으므로, 성공하면 지금 미리보기
     * 중인 자리(방금 산 것 포함)를 [equipAfterPurchase]로 이어서 저장한다.
     */
    private fun purchaseAll(targets: List<ShopItemUiModel>) {
        val itemIds = targets.mapNotNull { it.id.toLongOrNull() }
        if (itemIds.isEmpty()) return
        val targetIdSet = targets.mapTo(mutableSetOf()) { it.id }

        _uiState.update { it.copy(isPurchasing = true) }
        viewModelScope.launch {
            try {
                val key = purchaseKeys.getOrPut(targetIdSet) { UUID.randomUUID().toString() }
                when (val result = purchaseShopItemsUseCase(itemIds, key)) {
                    is ResultState.Success -> {
                        // 성공한 키를 다시 쓰면 서버가 새 구매 대신 이전 결과를 돌려줘서 버려야 함
                        purchaseKeys.remove(targetIdSet)
                        _uiState.update { state ->
                            state.copy(
                                selectedItems = state.selectedItems - targetIdSet,
                                coinBalance = result.data.payments.paymentBalanceOf("TOPAZ") ?: state.coinBalance,
                                gemBalance = result.data.payments.paymentBalanceOf("GEM") ?: state.gemBalance,
                            )
                        }
                        equipAfterPurchase(purchasedCount = result.data.purchasedItemIds.size)
                    }

                    // 실패한 키는 남겨둬야 재시도할 때 중복 결제되지 않음
                    is ResultState.Error -> _uiState.update { it.copy(message = result.message) }
                    ResultState.Loading -> Unit
                }
            } finally {
                _uiState.update { it.copy(isPurchasing = false) }
            }
        }
    }

    /**
     * 구매 직후 지금 미리보기 중인 자리를 그대로 착용 저장한다.
     *
     * [ShopUiState.equipped]는 안 산 아이템도 미리보기로 이미 올라가 있어서(owned 여부와 무관),
     * 방금 산 아이템까지 자연히 포함된다 — 굳이 owned로 다시 걸러낼 필요가 없다.
     */
    private suspend fun equipAfterPurchase(purchasedCount: Int) {
        val itemIds = _uiState.value.equipped.values.map { it.itemId }
        when (val result = equipAvatarUseCase(itemIds)) {
            is ResultState.Success -> {
                appearanceStore.applyAvatar(result.data)
                _uiState.update {
                    it.applyServerAvatar(result.data).copy(message = "${purchasedCount}개를 구매했어요.")
                }
            }
            is ResultState.Error -> _uiState.update {
                it.copy(message = "구매는 완료됐지만 착용에는 실패했어요. ${result.message}")
            }
            ResultState.Loading -> Unit
        }
        // 갱신을 기다려야 함. 먼저 풀어주면 owned가 반영되기 전에 또 살 수 있음
        awaitItemsRefresh()
        loadBalances()
        emitEvent(ShopUiEvent.SaveSelectedItems)
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
                    is ResultState.Success -> {
                        appearanceStore.applyAvatar(result.data)
                        _uiState.update {
                            it.applyServerAvatar(result.data).copy(message = "저장했어요.")
                        }
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

// 캐릭터는 상점에서 사는 게 아니라 업적으로 해금하는 것이라 price는 항상 0, owned는 항상 true로
// 채운다(그리드가 가격 대신 보유/장착 표시 쪽 분기를 타게 함). 실제 해금 여부는 onItemClick이
// 참고하는 unlocked로 따로 가른다 — 알 상태(unlocked=false)는 서버가 선택을 거절한다.
private fun Character.toShopItem(): ShopItemUiModel = ShopItemUiModel(
    id = id.toString(),
    name = name,
    price = 0,
    imageUrl = imageUrl,
    owned = true,
    unlocked = unlocked,
)

private fun List<CurrencyBalance>.balanceOf(currency: String): Int? =
    firstOrNull { it.currency == currency }?.balance

// balanceOf(List<CurrencyBalance>)와 이름이 같으면 타입 소거로 JVM 시그니처가 겹쳐 컴파일 에러가 남
private fun List<ShopPurchasePayment>.paymentBalanceOf(currency: String): Int? =
    firstOrNull { it.currency == currency }?.balanceAfter

/**
 * 서버가 준 착장으로 화면과 저장 상태를 함께 맞춤.
 *
 * 자리마다 하나씩이라 자리를 키로 씀
 */
private fun ShopUiState.applyServerAvatar(avatar: MemberAvatar): ShopUiState = copy(
    equipped = avatar.equipped.associate {
        it.slot.uppercase() to EquippedUiModel(itemId = it.itemId, imageUrl = it.imageUrl)
    },
    savedEquippedItemIds = avatar.equipped.mapTo(mutableSetOf()) { it.itemId },
    savedLayers = avatar.layers,
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
