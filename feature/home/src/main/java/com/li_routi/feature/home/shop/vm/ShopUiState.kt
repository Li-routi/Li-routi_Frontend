package com.li_routi.feature.home.shop.vm

import com.li_routi.feature.home.shop.component.ShopItemUiModel

/**
 * 상점 상단 탭 하나.
 *
 * 이름으로 분기하지 않고 [slot]을 그대로 조회에 실어 보냄 — 탭이 늘어도 앱을 안 고치려는 것임
 */
/**
 * 캐릭터 위에 겹쳐 그릴 착용 아이템 한 건.
 *
 * 안 산 아이템도 미리보기로 올라오므로 [owned]로 갈라둠 — 저장은 보유한 것만 보낼 수 있음
 */
data class EquippedUiModel(
    val itemId: Long,
    val imageUrl: String?,
    val owned: Boolean = true,
)

data class ShopCategoryUiModel(
    val key: String,
    val name: String,
    val slot: String?,
)

/**
 * 아이템 상점 화면 UI 상태.
 *
 * [items]는 `GET /api/shop/items` 결과로 채운다. 실패하면 빈 목록이라 샘플이 실제 상품처럼
 * 보이는 일이 없다 — 샘플 id로는 구매도 되지 않는다.
 *
 * 탭 선택과 보유 토글은 곧바로 서버 재조회로 이어져서 여기서 함께 관리한다.
 */
data class ShopUiState(
    val nickname: String = "닉네임",
    val coinBalance: Int = 450,
    val gemBalance: Int = 30,
    /** 서버가 내려준 탭. 받은 순서대로 그린다 */
    val categories: List<ShopCategoryUiModel> = emptyList(),
    val selectedCategoryIndex: Int = 0,
    val showOwnedOnly: Boolean = false,
    val items: List<ShopItemUiModel> = emptyList(),
    /**
     * 지금 캐릭터에 올려둔 착장. 자리 → 아이템.
     *
     * 저장 전에도 화면에 바로 비치게 여기서 들고 있다가, 저장할 때 통째로 보냄.
     * 한 자리엔 하나만 올라가므로 안 산 것을 미리 입어보면 그 자리 미리보기가 바뀜
     */
    val equipped: Map<String, EquippedUiModel> = emptyMap(),
    /**
     * 서버에 저장된 착장. 목록의 `착용중` 표시는 이걸 따름.
     *
     * [equipped]를 쓰면 고르자마자 착용중이 돼서 저장한 것과 구분이 안 됨
     */
    val savedEquippedItemIds: Set<Long> = emptySet(),
    /** 착장 저장 중 */
    val isEquipping: Boolean = false,
    /**
     * 그리드에서 고른 아이템. 아이템 id → 아이템.
     *
     * 탭을 옮겨도 유지돼야 여러 탭에서 고른 걸 한 번에 살 수 있음.
     * [items]는 지금 탭 것만 들고 있어서 id만 갖고는 다른 탭 선택을 되짚을 수 없어 아이템째로 담음
     */
    val selectedItems: Map<String, ShopItemUiModel> = emptyMap(),
    val isLoading: Boolean = false,
    /** 구매 요청 중. 따닥으로 두 번 사는 것 방지 */
    val isPurchasing: Boolean = false,
    val message: String? = null,
) {
    /** 고른 것 중 아직 안 산 아이템. 하단 버튼이 구매냐 저장이냐를 이걸로 가름 */
    val purchaseTargets: List<ShopItemUiModel>
        get() = selectedItems.values.filterNot { it.owned }
}

/**
 * 아이템 상점 화면의 일회성 UI 이벤트.
 *
 * Navigation은 [com.li_routi.feature.home.shop.navigation.ShoppingRoute] / AppNavHost에서 처리한다.
 */
sealed interface ShopUiEvent {
    data object NavigateBack : ShopUiEvent
    /**
     * 잔액 chip 탭 → 재화 구매 화면.
     * @param tabIndex 0 = 주황보석, 1 = 파란보석
     */
    data class NavigateToCurrencyShop(val tabIndex: Int = 0) : ShopUiEvent
    /** 하단 저장 버튼 탭. API 연동 전: ShoppingRoute에서 no-op. */
    data object SaveSelectedItems : ShopUiEvent
}
