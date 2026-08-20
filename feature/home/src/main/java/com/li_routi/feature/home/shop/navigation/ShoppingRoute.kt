package com.li_routi.feature.home.shop.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.li_routi.feature.home.shop.vm.CurrencyShopUiEvent
import com.li_routi.feature.home.shop.vm.ShopUiEvent

/**
 * 쇼핑 feature 진입점. 아이템 상점 ↔ 재화 구매를 feature 내부에서 연결한다.
 *
 * - 주황/파란 잔액 chip → [CurrencyShopRoute]의 해당 탭
 * - 재화구매 뒤로가기(상단/시스템 Back) → [ShopRoute]
 * - 상점 뒤로가기 → [onNavigateBack] (홈 등 feature 밖은 AppNavHost에서 연결)
 *
 * 아이템 저장/결제는 각 Route와 ViewModel에서 처리하고, 여기서는 화면 전환만 중계한다.
 */
@Composable
fun ShoppingRoute(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    // 구성 변경(회전 등) 후에도 재화구매 화면이 유지되도록 saveable 사용.
    var showCurrencyShop by rememberSaveable { mutableStateOf(false) }
    var currencyShopTabIndex by rememberSaveable { mutableIntStateOf(0) }
    // ShopViewModel이 이미 받아온 잔액을 그대로 넘겨받는다 — 재화 구매 화면이 처음부터
    // 다시 서버에 물어보면 응답 오는 동안 기본값이 잠깐 보였다가 바뀌는 깜빡임이 생긴다.
    // null이면 상점도 아직 조회 전이라는 뜻이라 그대로 null로 넘긴다(0으로 바꾸면 안 됨).
    var currencyShopCoinBalance by rememberSaveable { mutableStateOf<Int?>(null) }
    var currencyShopGemBalance by rememberSaveable { mutableStateOf<Int?>(null) }

    // Nav 백스택이 아니라 로컬 전환이므로, 시스템 Back이 Shop을 건너뛰지 않게 가로챈다.
    BackHandler(enabled = showCurrencyShop) {
        showCurrencyShop = false
    }

    if (showCurrencyShop) {
        CurrencyShopRoute(
            initialTabIndex = currencyShopTabIndex,
            initialCoinBalance = currencyShopCoinBalance,
            initialGemBalance = currencyShopGemBalance,
            onEvent = { event ->
                when (event) {
                    CurrencyShopUiEvent.NavigateBack -> showCurrencyShop = false
                    // 결제창 띄우기는 CurrencyShopRoute가 직접 처리함
                    is CurrencyShopUiEvent.OpenPaymentSheet -> Unit
                }
            },
            modifier = modifier,
        )
    } else {
        ShopRoute(
            onEvent = { event ->
                when (event) {
                    ShopUiEvent.NavigateBack -> onNavigateBack()
                    is ShopUiEvent.NavigateToCurrencyShop -> {
                        currencyShopTabIndex = event.tabIndex
                        currencyShopCoinBalance = event.coinBalance
                        currencyShopGemBalance = event.gemBalance
                        showCurrencyShop = true
                    }
                    // API 연동 전: 장착 저장 미구현
                    ShopUiEvent.SaveSelectedItems -> Unit
                }
            },
            modifier = modifier,
        )
    }
}
