package com.li_routi.feature.shopping.vm

import com.li_routi.feature.shopping.component.CurrencyProductUiModel
import com.li_routi.feature.shopping.component.SampleBlueGemProducts
import com.li_routi.feature.shopping.component.SampleOrangeGemProducts

/**
 * 재화 구매 화면 UI 상태.
 *
 * - [selectedProductId]: 첫 탭으로 선택된 상품 (파란 테두리)
 * - [chargeDialogProductId]: 같은 상품을 한 번 더 탭했을 때 충전 팝업에 표시할 상품
 *
 * 주황/파란 탭 UI 선택은 Screen 로컬. 목록은 탭별 샘플(또는 이후 API)로 분리한다.
 */
data class CurrencyShopUiState(
    val coinBalance: Int = 450,
    val gemBalance: Int = 30,
    val orangeProducts: List<CurrencyProductUiModel> = SampleOrangeGemProducts,
    val blueProducts: List<CurrencyProductUiModel> = SampleBlueGemProducts,
    val selectedProductId: String? = null,
    val chargeDialogProductId: String? = null,
) {
    val allProducts: List<CurrencyProductUiModel>
        get() = orangeProducts + blueProducts

    val chargeDialogProduct: CurrencyProductUiModel?
        get() = chargeDialogProductId?.let { id -> allProducts.find { it.id == id } }
}

/**
 * 재화 구매 화면의 일회성 UI 이벤트.
 */
sealed interface CurrencyShopUiEvent {
    data object NavigateBack : CurrencyShopUiEvent

    /** 충전하기 확인. API 연동 전: ShoppingRoute에서 no-op. */
    data class ConfirmCharge(val productId: String) : CurrencyShopUiEvent
}
