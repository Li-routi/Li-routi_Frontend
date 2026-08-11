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
 * API 연동 전: [ShopUiEvent.SaveSelectedItems], [CurrencyShopUiEvent.ConfirmCharge]는 no-op.
 */
@Composable
fun ShoppingRoute(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    // 구성 변경(회전 등) 후에도 재화구매 화면이 유지되도록 saveable 사용.
    var showCurrencyShop by rememberSaveable { mutableStateOf(false) }
    var currencyShopTabIndex by rememberSaveable { mutableIntStateOf(0) }

    // Nav 백스택이 아니라 로컬 전환이므로, 시스템 Back이 Shop을 건너뛰지 않게 가로챈다.
    BackHandler(enabled = showCurrencyShop) {
        showCurrencyShop = false
    }

    if (showCurrencyShop) {
        CurrencyShopRoute(
            initialTabIndex = currencyShopTabIndex,
            onEvent = { event ->
                when (event) {
                    CurrencyShopUiEvent.NavigateBack -> showCurrencyShop = false
                    // API 연동 전: 결제/잔액 갱신 미구현
                    is CurrencyShopUiEvent.ConfirmCharge -> Unit
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
