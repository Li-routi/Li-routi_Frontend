package com.li_routi.feature.home.shop.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.di.ShopContainer
import com.li_routi.core.domain.shop.ChargeProduct
import com.li_routi.core.domain.shop.ChargeStarted
import com.li_routi.core.domain.shop.CompleteChargeUseCase
import com.li_routi.core.domain.shop.StartChargeUseCase
import com.li_routi.core.domain.shop.ExchangeCurrencyUseCase
import com.li_routi.core.domain.shop.ExchangeProduct
import com.li_routi.core.domain.shop.GetChargeProductsUseCase
import com.li_routi.core.domain.shop.CurrencyBalance
import com.li_routi.core.domain.shop.GetExchangeProductsUseCase
import com.li_routi.core.domain.shop.GetWalletBalancesUseCase
import com.li_routi.feature.home.shop.component.CurrencyProductUiModel
import com.li_routi.feature.home.shop.navigation.CurrencyShopScreenActions
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 재화 구매 화면 ViewModel.
 *
 * 상품 선택(파란 테두리) → 재탭 시 충전 팝업 → 취소/충전하기 이벤트를 처리함.
 *
 * 주황보석 탭은 재화 교환(`POST /api/shop/exchanges`)이라 여기서 바로 처리하고,
 * 파란보석 탭은 현금 결제라 포트원 SDK가 필요해서 아직 연결하지 않음
 */
class CurrencyShopViewModel(
    initialState: CurrencyShopUiState = CurrencyShopUiState(),
    private val getExchangeProductsUseCase: GetExchangeProductsUseCase = ShopContainer.getExchangeProductsUseCase,
    private val getChargeProductsUseCase: GetChargeProductsUseCase = ShopContainer.getChargeProductsUseCase,
    private val exchangeCurrencyUseCase: ExchangeCurrencyUseCase = ShopContainer.exchangeCurrencyUseCase,
    private val getWalletBalancesUseCase: GetWalletBalancesUseCase = ShopContainer.getWalletBalancesUseCase,
    private val startChargeUseCase: StartChargeUseCase = ShopContainer.startChargeUseCase,
    private val completeChargeUseCase: CompleteChargeUseCase = ShopContainer.completeChargeUseCase,
) : BaseViewModel(), CurrencyShopScreenActions {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<CurrencyShopUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<CurrencyShopUiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<CurrencyShopUiEvent> = _uiEvent.asSharedFlow()

    // 교환 재시도 시 같은 키를 보내야 두 번 차감되지 않음. 상품별로 진행 중인 키를 들고 있다가 성공하면 버림
    private val exchangeKeys = mutableMapOf<Long, String>()

    init {
        loadProducts()
        loadBalances()
    }

    /**
     * 재진입 시(같은 화면 안에서 [CurrencyShopViewModel]이 재사용될 때) 호출부(상점 화면)가
     * 이미 알고 있는 최신 잔액을 즉시 반영하고, 서버에도 다시 확인한다.
     *
     * `viewModel { }` 팩토리는 이 ViewModel이 재사용되면 다시 실행되지 않아 생성자 초기값이
     * 무시된다 — 그래서 재진입마다 [com.li_routi.feature.home.shop.navigation.CurrencyShopRoute]가
     * 이 메서드를 직접 호출해 값을 맞춘다.
     */
    fun syncKnownBalances(coinBalance: Int?, gemBalance: Int?) {
        _uiState.update {
            it.copy(
                coinBalance = coinBalance ?: it.coinBalance,
                gemBalance = gemBalance ?: it.gemBalance,
            )
        }
        loadBalances()
    }

    /** 상점 헤더 잔액 */
    private fun loadBalances() {
        viewModelScope.launch {
            val result = getWalletBalancesUseCase()
            if (result is ResultState.Success) {
                _uiState.update { state ->
                    state.copy(
                        coinBalance = result.data.balanceOf("TOPAZ") ?: state.coinBalance,
                        gemBalance = result.data.balanceOf("GEM") ?: state.gemBalance,
                    )
                }
            }
        }
    }

    fun onDismissMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun loadProducts() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            // 두 조회가 서로 독립이라 순차로 기다릴 이유가 없음
            val exchangeDeferred = async { getExchangeProductsUseCase() }
            val chargeDeferred = async { getChargeProductsUseCase() }
            val exchange = exchangeDeferred.await()
            val charge = chargeDeferred.await()
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    // 실패하면 이전 목록을 남기지 않음 — 없는 상품을 고를 수 있게 되면 안 됨
                    orangeProducts = (exchange as? ResultState.Success)?.data?.map { it.toUiModel() }
                        .orEmpty(),
                    blueProducts = (charge as? ResultState.Success)?.data?.map { it.toUiModel() }
                        .orEmpty(),
                    message = (exchange as? ResultState.Error)?.message?.toUserFacingCurrencyMessage()
                        ?: (charge as? ResultState.Error)?.message?.toUserFacingCurrencyMessage(),
                )
            }
        }
    }

    override fun onBackClick() {
        emitEvent(CurrencyShopUiEvent.NavigateBack)
    }

    override fun onProductClick(productId: String) {
        _uiState.update { state ->
            if (state.selectedProductId == productId) {
                state.copy(chargeDialogProductId = productId)
            } else {
                state.copy(
                    selectedProductId = productId,
                    chargeDialogProductId = null,
                )
            }
        }
    }

    override fun onDismissChargeDialog() {
        _uiState.update { it.copy(chargeDialogProductId = null) }
    }

    override fun onConfirmChargeClick() {
        val state = _uiState.value
        val productId = state.chargeDialogProductId ?: return
        _uiState.update { it.copy(chargeDialogProductId = null) }

        val exchangeId = productId.exchangeProductIdOrNull()
        if (exchangeId == null) {
            startCharge(productId)
            return
        }
        if (state.isExchanging) return

        _uiState.update { it.copy(isExchanging = true) }
        viewModelScope.launch {
            try {
                val key = exchangeKeys.getOrPut(exchangeId) { UUID.randomUUID().toString() }
                when (val result = exchangeCurrencyUseCase(productId = exchangeId, idempotencyKey = key)) {
                    is ResultState.Success -> {
                        // 성공한 키를 다시 쓰면 서버가 새 교환 대신 이전 결과를 돌려줘서 버려야 함
                        exchangeKeys.remove(exchangeId)
                        val data = result.data
                        _uiState.update {
                            it.copy(
                                coinBalance = data.balanceOf("TOPAZ")?.toInt() ?: it.coinBalance,
                                gemBalance = data.balanceOf("GEM")?.toInt() ?: it.gemBalance,
                                message = "교환이 완료됐어요.",
                            )
                        }
                    }

                    // 실패한 키는 남겨둬야 재시도할 때 중복 차감되지 않음
                    is ResultState.Error -> _uiState.update {
                        it.copy(message = result.message.toUserFacingCurrencyMessage())
                    }
                    ResultState.Loading -> Unit
                }
            } finally {
                _uiState.update { it.copy(isExchanging = false) }
            }
        }
    }

    /**
     * 현금 결제 시작. 서버가 준 값을 그대로 결제창에 넘겨야 해서 화면으로 올려보냄.
     *
     * 이 시점엔 돈이 오가지 않고 결제 식별자만 발급됨
     */
    private fun startCharge(productId: String) {
        val chargeId = productId.chargeProductIdOrNull() ?: run {
            _uiState.update { it.copy(message = "상품 정보를 불러오지 못했어요.") }
            return
        }
        if (_uiState.value.isCharging) return

        _uiState.update { it.copy(isCharging = true) }
        viewModelScope.launch {
            when (val result = startChargeUseCase(chargeId)) {
                is ResultState.Success -> emitEvent(CurrencyShopUiEvent.OpenPaymentSheet(result.data))
                is ResultState.Error -> _uiState.update {
                    it.copy(isCharging = false, message = result.message.toUserFacingCurrencyMessage())
                }

                ResultState.Loading -> Unit
            }
        }
    }

    /** 결제창을 마친 뒤 서버에 검증·지급을 요청함 */
    fun onPaymentSucceeded(paymentId: String) {
        viewModelScope.launch {
            try {
                when (val result = completeChargeUseCase(paymentId)) {
                    is ResultState.Success -> {
                        // 응답에 지급 후 잔액이 실려 와서 잔액 조회를 따로 부를 필요가 없음
                        val settled = result.data
                        val charged = settled.rewardAmount + settled.bonusAmount
                        _uiState.update { state ->
                            state.copy(
                                coinBalance = if (settled.currency == "TOPAZ") settled.totalBalance else state.coinBalance,
                                gemBalance = if (settled.currency == "GEM") settled.totalBalance else state.gemBalance,
                                message = "${charged}개 충전이 완료됐어요.",
                            )
                        }
                    }

                    is ResultState.Error -> _uiState.update {
                        it.copy(message = result.message.toUserFacingCurrencyMessage())
                    }
                    ResultState.Loading -> Unit
                }
            } finally {
                _uiState.update { it.copy(isCharging = false) }
            }
        }
    }

    fun onPaymentFailed(message: String?) {
        _uiState.update { it.copy(isCharging = false, message = message ?: "결제가 취소됐어요.") }
    }

    private fun emitEvent(event: CurrencyShopUiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}

// 교환/충전 상품 id가 서로 겹칠 수 있어서 접두사로 구분함
private const val ExchangeIdPrefix = "exchange_"
private const val ChargeIdPrefix = "charge_"

private fun String.exchangeProductIdOrNull(): Long? =
    if (startsWith(ExchangeIdPrefix)) removePrefix(ExchangeIdPrefix).toLongOrNull() else null

private fun String.chargeProductIdOrNull(): Long? =
    if (startsWith(ChargeIdPrefix)) removePrefix(ChargeIdPrefix).toLongOrNull() else null

private fun List<CurrencyBalance>.balanceOf(currency: String): Int? =
    firstOrNull { it.currency == currency }?.balance

private fun com.li_routi.core.domain.shop.ExchangeResult.balanceOf(currency: String): Long? = when (currency) {
    fromCurrency -> fromBalance
    toCurrency -> toBalance
    else -> null
}

// Figma(node 6389:17225/17281/18526) 표기 기준 — "다이아"/"토파즈"가 아니라 "오렌지젬"/"블루젬".
private fun String.toDisplayCurrency(): String = when (uppercase()) {
    "GEM" -> "블루젬"
    "TOPAZ" -> "오렌지젬"
    "", "UNKNOWN" -> "젬"
    else -> this
}

/** 서버가 내부 enum 실패를 그대로 내려주는 경우를 사용자 문구로 바꿈 */
private fun String.toUserFacingCurrencyMessage(): String =
    if (contains("알수없는 재화") || contains("알 수 없는 재화")) {
        "재화 정보를 확인하지 못했어요. 잠시 후 다시 시도해 주세요."
    } else {
        this
    }

private fun Long.formatted(): String = NumberFormat.getNumberInstance(Locale.KOREA).format(this)

private fun ExchangeProduct.toUiModel(): CurrencyProductUiModel {
    val title = "${toAmount.formatted()} ${toCurrency.toDisplayCurrency()}"
    val payment = "${fromAmount.formatted()} ${fromCurrency.toDisplayCurrency()}"
    return CurrencyProductUiModel(
        id = "$ExchangeIdPrefix$id",
        title = title,
        subtitle = payment,
        // 가격 칸 아이콘이 이미 파란 다이아몬드로 재화를 나타내므로, Figma처럼 이름 없이 개수만 적는다.
        price = "${fromAmount.formatted()}개",
        chargeTitle = title,
        paymentAmount = payment,
    )
}

private fun ChargeProduct.toUiModel(): CurrencyProductUiModel {
    // Figma 충전 팝업(node 6389:18526)의 큰 숫자는 유상+무상 합계(예: "550 블루젬" = 500 유상 + 50
    // 보너스)이고, "+50 보너스" 뱃지가 그중 무상분만 별도로 강조해 보여준다.
    val totalAmount = rewardAmount + bonusAmount
    val title = "${totalAmount.formatted()} ${rewardCurrency.toDisplayCurrency()}"
    return CurrencyProductUiModel(
        id = "$ChargeIdPrefix$id",
        title = title,
        subtitle = "${priceKrw.formatted()}원",
        price = priceKrw.formatted(),
        priceSuffix = "원",
        isPopular = popular,
        chargeTitle = title,
        bonusLabel = if (bonusAmount > 0) "+${bonusAmount.formatted()} 보너스" else null,
        paymentAmount = "${priceKrw.formatted()}원",
    )
}
