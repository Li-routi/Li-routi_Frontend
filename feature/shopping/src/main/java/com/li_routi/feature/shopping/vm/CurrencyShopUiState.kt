package com.li_routi.feature.shopping.vm

import com.li_routi.feature.shopping.component.CurrencyProductUiModel
import com.li_routi.feature.shopping.component.SampleCurrencyProducts

/**
 * 재화 구매 화면 UI 상태.
 *
 * - [selectedProductId]: 첫 탭으로 선택된 상품 (파란 테두리)
 * - [chargeDialogProductId]: 같은 상품을 한 번 더 탭했을 때 충전 팝업에 표시할 상품
 *
 * 주황보석/파란보석 탭 선택은 Screen 로컬 state로 둔다.
 */
data class CurrencyShopUiState(
    val coinBalance: Int = 450,
    val gemBalance: Int = 30,
    val products: List<CurrencyProductUiModel> = SampleCurrencyProducts,
    val selectedProductId: String? = null,
    val chargeDialogProductId: String? = null,
) {
    val chargeDialogProduct: CurrencyProductUiModel?
        get() = chargeDialogProductId?.let { id -> products.find { it.id == id } }
}

/**
 * 재화 구매 화면의 일회성 UI 이벤트.
 */
sealed interface CurrencyShopUiEvent {
    data object NavigateBack : CurrencyShopUiEvent

    /** 충전하기 확인. 결제/충전 실제 처리는 이 이벤트를 받는 쪽에서 연결한다. */
    data class ConfirmCharge(val productId: String) : CurrencyShopUiEvent
}
