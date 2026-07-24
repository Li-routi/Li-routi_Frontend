package com.li_routi.feature.shopping.navigation

/**
 * `ShopScreen`(아이템 상점)에서 발생하는 사용자 이벤트에 대한 콜백 계약(contract).
 *
 * Navigation(NavController/NavHost, Activity Intent 등) 연결은 이번 범위에서 제외되며,
 * 실제 화면 전환 구현은 다른 담당자가 이 인터페이스를 구현해 연결한다.
 */
interface ShopScreenActions {
    /** 상단 뒤로가기 아이콘 탭 */
    fun onBackClick()

    /** 상단 코인/보석 잔액 chip 탭 */
    fun onCurrencyChipClick()

    /** 하단 저장 버튼 탭 */
    fun onSaveClick()
}
