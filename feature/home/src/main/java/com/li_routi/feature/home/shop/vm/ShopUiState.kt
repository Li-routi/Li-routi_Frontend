package com.li_routi.feature.home.shop.vm

import com.li_routi.feature.home.shop.component.ShopItemUiModel

/**
 * 상점 상단 탭 하나.
 *
 * 이름으로 분기하지 않고 [slot]을 그대로 조회에 실어 보냄 — 탭이 늘어도 앱을 안 고치려는 것임
 */
/** 캐릭터 위에 겹쳐 그릴 착용 아이템 한 건 */
data class EquippedUiModel(
    val itemId: Long,
    val imageUrl: String?,
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
     * 저장 전에도 화면에 바로 비치게 여기서 들고 있다가, 저장할 때 통째로 보냄
     */
    val equipped: Map<String, EquippedUiModel> = emptyMap(),
    /** 착장 저장 중 */
    val isEquipping: Boolean = false,
    /** 그리드에서 선택된 아이템. null이면 미선택. */
    val selectedItemId: String? = null,
    val isLoading: Boolean = false,
    /** 구매 요청 중. 따닥으로 두 번 사는 것 방지 */
    val isPurchasing: Boolean = false,
    val message: String? = null,
)

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
