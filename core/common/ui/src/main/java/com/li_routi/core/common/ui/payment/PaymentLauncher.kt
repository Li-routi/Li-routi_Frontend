package com.li_routi.core.common.ui.payment

import androidx.compose.runtime.staticCompositionLocalOf
import io.portone.sdk.android.type.request.PaymentRequest
import io.portone.sdk.android.type.response.PaymentResponse

/**
 * 결제창을 여는 통로.
 *
 * 포트원 SDK는 결과 런처를 Activity가 STARTED 되기 전에 등록해야 해서 Composable 안에서는 만들 수 없음.
 * Activity가 만들어 여기에 내려주고, 화면은 [PaymentRequest]만 넘김
 */
fun interface PaymentLauncher {
    fun launch(request: PaymentRequest)
}

/** 결제를 붙이지 않은 화면(프리뷰 등)에서는 null이라 아무 일도 일어나지 않음 */
val LocalPaymentLauncher = staticCompositionLocalOf<PaymentLauncher?> { null }

/**
 * 결제 결과를 받을 콜백을 등록하는 통로.
 *
 * 두 번째 파라미터가 true면 성공. 화면이 사라질 때는 null을 넣어 해제해야 함
 */
val LocalPaymentResultHandlerSetter =
    staticCompositionLocalOf<((((PaymentResponse, Boolean) -> Unit)?) -> Unit)?> { null }
