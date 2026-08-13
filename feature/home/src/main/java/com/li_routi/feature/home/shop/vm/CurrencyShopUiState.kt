package com.li_routi.feature.home.shop.vm

import com.li_routi.core.domain.shop.ChargeStarted
import com.li_routi.feature.home.shop.component.CurrencyProductUiModel

/**
 * 재화 구매 화면 UI 상태.
 *
 * - [selectedProductId]: 첫 탭으로 선택된 상품 (파란 테두리)
 * - [chargeDialogProductId]: 같은 상품을 한 번 더 탭했을 때 충전 팝업에 표시할 상품
 *
 * 주황/파란 탭 UI 선택은 Screen 로컬. 목록은 각각 교환/충전 상품 API로 채운다.
 * 실패하면 빈 목록이다 — 샘플 id에는 exchange_/charge_ 접두사가 없어서 남아 있으면
 * 교환을 현금 결제로 오인한다.
 */
data class CurrencyShopUiState(
    val coinBalance: Int = 450,
    val gemBalance: Int = 30,
    val orangeProducts: List<CurrencyProductUiModel> = emptyList(),
    val blueProducts: List<CurrencyProductUiModel> = emptyList(),
    val selectedProductId: String? = null,
    val chargeDialogProductId: String? = null,
    val isLoading: Boolean = false,
    /** 교환 요청 중. 따닥으로 두 번 교환되는 것 방지 */
    val isExchanging: Boolean = false,
    /** 결제 시작~결제창 종료까지. 중복 결제 방지 */
    val isCharging: Boolean = false,
    val message: String? = null,
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

    /** 결제창을 띄워야 함. 서버가 준 값을 그대로 포트원 SDK에 넘김 */
    data class OpenPaymentSheet(val charge: ChargeStarted) : CurrencyShopUiEvent
}
