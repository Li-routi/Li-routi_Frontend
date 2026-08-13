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
import androidx.activity.compose.LocalActivity
import com.li_routi.core.designsystem.component.LiroutiToast
import io.portone.sdk.android.PortOne
import io.portone.sdk.android.payment.PaymentCallback
import io.portone.sdk.android.type.entity.Currency
import io.portone.sdk.android.type.entity.PaymentPayMethod
import io.portone.sdk.android.type.request.PaymentRequest
import io.portone.sdk.android.type.response.PaymentResponse
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
    val activity = LocalActivity.current as? androidx.activity.ComponentActivity

    val paymentLauncher = activity?.let {
        PortOne.registerForPaymentActivity(
            it,
            callback = object : PaymentCallback {
                // code가 null이면 결제 성공
                override fun onSuccess(response: PaymentResponse) {
                    viewModel.onPaymentSucceeded(response.paymentId)
                }

                override fun onFail(response: PaymentResponse) {
                    viewModel.onPaymentFailed(response.message)
                }
            },
        )
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is CurrencyShopUiEvent.OpenPaymentSheet -> {
                    val charge = event.charge
                    if (activity == null || paymentLauncher == null) {
                        viewModel.onPaymentFailed("결제창을 열 수 없어요.")
                    } else {
                        PortOne.requestPayment(
                            activity,
                            request = PaymentRequest(
                                storeId = charge.storeId,
                                paymentId = charge.paymentId,
                                orderName = charge.orderName,
                                channelKey = charge.channelKey,
                                totalAmount = charge.amount,
                                currency = Currency.KRW,
                                payMethod = PaymentPayMethod.CARD,
                            ),
                            resultLauncher = paymentLauncher,
                        )
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
