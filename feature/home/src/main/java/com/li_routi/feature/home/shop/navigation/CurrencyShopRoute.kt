package com.li_routi.feature.home.shop.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.DisposableEffect
import com.li_routi.core.common.ui.payment.LocalPaymentLauncher
import com.li_routi.core.common.ui.payment.LocalPaymentResultHandlerSetter
import com.li_routi.core.designsystem.component.LiroutiToast
import io.portone.sdk.android.type.entity.Currency
import io.portone.sdk.android.type.entity.PaymentPayMethod
import io.portone.sdk.android.type.request.PaymentRequest
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.li_routi.feature.home.shop.screen.CurrencyShopScreen
import com.li_routi.feature.home.shop.vm.CurrencyShopUiEvent
import com.li_routi.feature.home.shop.vm.CurrencyShopUiState
import com.li_routi.feature.home.shop.vm.CurrencyShopViewModel

/**
 * 재화 구매 화면 진입점. [CurrencyShopViewModel]과 [CurrencyShopScreen]을 연결한다.
 *
 * 현금 결제는 포트원 SDK가 Activity를 띄우는 구조라 여기서 처리한다.
 * 결제창을 마치면 서버에 검증·지급을 요청한다 — 지급 판단은 서버가 포트원에 다시 물어서 한다.
 *
 * @param onEvent 일회성 UI 이벤트 수신 콜백.
 */
@Composable
fun CurrencyShopRoute(
    onEvent: (CurrencyShopUiEvent) -> Unit = {},
    modifier: Modifier = Modifier,
    initialTabIndex: Int = 0,
    viewModel: CurrencyShopViewModel = viewModel {
        CurrencyShopViewModel(initialState = CurrencyShopUiState())
    },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val paymentLauncher = LocalPaymentLauncher.current
    val setPaymentResultHandler = LocalPaymentResultHandlerSetter.current

    // 결제 결과는 Activity가 받아서 넘겨줌 — 화면을 벗어나면 해제해야 다른 화면으로 새지 않음
    DisposableEffect(viewModel, setPaymentResultHandler) {
        setPaymentResultHandler?.invoke { response, isSuccess ->
            if (isSuccess) {
                viewModel.onPaymentSucceeded(response.paymentId)
            } else {
                viewModel.onPaymentFailed(response.message)
            }
        }
        onDispose { setPaymentResultHandler?.invoke(null) }
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is CurrencyShopUiEvent.OpenPaymentSheet -> {
                    val charge = event.charge
                    if (paymentLauncher == null) {
                        viewModel.onPaymentFailed("결제창을 열 수 없어요.")
                    } else {
                        // 여는 데 실패하면 결과 콜백도 안 와서, 여기서 안 풀어주면 다시 충전을 못 함
                        runCatching {
                            paymentLauncher.launch(
                                PaymentRequest(
                                    storeId = charge.storeId,
                                    paymentId = charge.paymentId,
                                    orderName = charge.orderName,
                                    channelKey = charge.channelKey,
                                    totalAmount = charge.amount,
                                    currency = Currency.KRW,
                                    payMethod = PaymentPayMethod.CARD,
                                ),
                            )
                        }.onFailure { viewModel.onPaymentFailed("결제창을 열 수 없어요.") }
                    }
                }

                else -> Unit
            }
            onEvent(event)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        CurrencyShopScreen(
            actions = viewModel,
            coinBalance = uiState.coinBalance,
            gemBalance = uiState.gemBalance,
            orangeProducts = uiState.orangeProducts,
            blueProducts = uiState.blueProducts,
            selectedProductId = uiState.selectedProductId,
            chargeDialogProduct = uiState.chargeDialogProduct,
            initialTabIndex = initialTabIndex,
        )

        // 교환 실패(잔액 부족 등) 사유를 서버 메시지 그대로 보여줌
        uiState.message?.let { message ->
            LiroutiToast(
                message = message,
                onCloseClick = viewModel::onDismissMessage,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 100.dp),
            )
        }
    }
}
