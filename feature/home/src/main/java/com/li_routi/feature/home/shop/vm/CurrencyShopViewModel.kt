package com.li_routi.feature.home.shop.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.di.ShopContainer
import com.li_routi.core.domain.shop.ChargeProduct
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
                    message = (exchange as? ResultState.Error)?.message
                        ?: (charge as? ResultState.Error)?.message,
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
            // 파란보석 탭(현금 결제)은 포트원 결제창이 있어야 해서 아직 못 붙임
            _uiState.update { it.copy(message = "결제 준비 중이에요.") }
            emitEvent(CurrencyShopUiEvent.ConfirmCharge(productId))
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
                    is ResultState.Error -> _uiState.update { it.copy(message = result.message) }
                    ResultState.Loading -> Unit
                }
            } finally {
                _uiState.update { it.copy(isExchanging = false) }
            }
        }
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

private fun List<CurrencyBalance>.balanceOf(currency: String): Int? =
    firstOrNull { it.currency == currency }?.balance

private fun com.li_routi.core.domain.shop.ExchangeResult.balanceOf(currency: String): Long? = when (currency) {
    fromCurrency -> fromBalance
    toCurrency -> toBalance
    else -> null
}

private fun String.toDisplayCurrency(): String = when (this) {
    "GEM" -> "다이아"
    "TOPAZ" -> "토파즈"
    else -> this
}

private fun Long.formatted(): String = NumberFormat.getNumberInstance(Locale.KOREA).format(this)

private fun ExchangeProduct.toUiModel(): CurrencyProductUiModel {
    val title = "${toAmount.formatted()}${toCurrency.toDisplayCurrency()}"
    val payment = "${fromAmount.formatted()}${fromCurrency.toDisplayCurrency()}"
    return CurrencyProductUiModel(
        id = "$ExchangeIdPrefix$id",
        title = title,
        subtitle = payment,
        price = payment,
        chargeTitle = title,
        paymentAmount = payment,
    )
}

private fun ChargeProduct.toUiModel(): CurrencyProductUiModel {
    val title = "${rewardAmount.formatted()}${rewardCurrency.toDisplayCurrency()}"
    return CurrencyProductUiModel(
        id = "$ChargeIdPrefix$id",
        title = title,
        subtitle = "${priceKrw.formatted()}원",
        price = priceKrw.formatted(),
        priceSuffix = "원",
        isPopular = popular,
        chargeTitle = title,
        // 유상/무상을 나눠서 내려주는 건 팝업에서 "+50 보너스"를 따로 보여주기 위함
        bonusLabel = if (bonusAmount > 0) "+${bonusAmount.formatted()} 보너스" else null,
        paymentAmount = "${priceKrw.formatted()}원",
    )
}
