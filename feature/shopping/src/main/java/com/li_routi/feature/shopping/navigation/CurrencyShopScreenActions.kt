package com.li_routi.feature.shopping.navigation

/**
 * `CurrencyShopScreen`(재화 구매 리스트)에서 발생하는 사용자 이벤트에 대한 콜백 계약(contract).
 *
 * Navigation(NavController/NavHost, Activity Intent 등) 연결은 이번 범위에서 제외되며,
 * 실제 화면 전환 구현은 다른 담당자가 이 인터페이스를 구현해 연결한다.
 */
interface CurrencyShopScreenActions {
    /** 상단 뒤로가기 아이콘 탭 */
    fun onBackClick()

    /**
     * 재화 상품 행 탭.
     * - 미선택 → 선택(파란 테두리)
     * - 이미 선택됨 → 충전 팝업 표시
     */
    fun onProductClick(productId: String)

    /** 충전 팝업 취소 / dim 탭 / 뒤로 */
    fun onDismissChargeDialog()

    /** 충전 팝업 "충전하기" 탭 */
    fun onConfirmChargeClick()
}
