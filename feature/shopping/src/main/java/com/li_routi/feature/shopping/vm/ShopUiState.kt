package com.li_routi.feature.shopping.vm

import com.li_routi.feature.shopping.component.SampleShopItems
import com.li_routi.feature.shopping.component.ShopItemUiModel

/**
 * 아이템 상점 화면 UI 상태.
 *
 * 카테고리 탭/보유 아이템 토글은 Screen 로컬 state로 두고,
 * 잔액·아이템 목록처럼 외부 데이터가 필요한 값만 여기서 관리한다.
 */
data class ShopUiState(
    val nickname: String = "닉네임",
    val coinBalance: Int = 450,
    val gemBalance: Int = 30,
    val items: List<ShopItemUiModel> = SampleShopItems,
)

/**
 * 아이템 상점 화면의 일회성 UI 이벤트.
 *
 * Navigation 연결은 [com.li_routi.feature.shopping.navigation.ShopRoute]의 onEvent에서
 * 다른 담당자가 구현한다.
 */
sealed interface ShopUiEvent {
    data object NavigateBack : ShopUiEvent
    /** 코인/보석 잔액 chip 탭 → 재화 구매 화면으로 이동 */
    data object NavigateToCurrencyShop : ShopUiEvent
    /** 하단 저장 버튼 탭 */
    data object SaveSelectedItems : ShopUiEvent
}
