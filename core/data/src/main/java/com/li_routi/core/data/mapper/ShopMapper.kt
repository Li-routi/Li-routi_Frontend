package com.li_routi.core.data.mapper

import com.li_routi.core.common.kotlin.util.ApiException
import com.li_routi.core.data.network.dto.response.AvatarEquippedItemResponse
import com.li_routi.core.data.network.dto.response.ChargeProductItemResponse
import com.li_routi.core.data.network.dto.response.ChargeProductsResponse
import com.li_routi.core.data.network.dto.response.ChargeSettledResponse
import com.li_routi.core.data.network.dto.response.ChargeStartedResponse
import com.li_routi.core.data.network.dto.response.ExchangeProductItemResponse
import com.li_routi.core.data.network.dto.response.ExchangeProductsResponse
import com.li_routi.core.data.network.dto.response.ExchangeResultResponse
import com.li_routi.core.data.network.dto.response.MemberAvatarResponse
import com.li_routi.core.data.network.dto.response.ShopAvatarItemResponse
import com.li_routi.core.data.network.dto.response.ShopAvatarItemsResponse
import com.li_routi.core.data.network.dto.response.WalletBalancesResponse
import com.li_routi.core.domain.shop.AvatarEquippedItem
import com.li_routi.core.domain.shop.ChargeProduct
import com.li_routi.core.domain.shop.ChargeSettled
import com.li_routi.core.domain.shop.ChargeStarted
import com.li_routi.core.domain.shop.CurrencyBalance
import com.li_routi.core.domain.shop.ExchangeProduct
import com.li_routi.core.domain.shop.ExchangeResult
import com.li_routi.core.domain.shop.MemberAvatar
import com.li_routi.core.domain.shop.ShopAvatarItem

/**
 * 결제 응답에서 비면 안 되는 값을 걸러냄.
 *
 * 빈 값을 그냥 흘려보내면 결제창이 이상하게 뜨거나, 지급됐다고 안내해놓고 잔액은 그대로인 상황이 생김
 */
private fun String?.requirePaymentField(name: String): String {
    if (isNullOrBlank()) throw ApiException("결제 응답에 $name 가 없습니다.")
    return this
}

fun ChargeSettledResponse.toDomain(): ChargeSettled = ChargeSettled(
    paymentId = paymentId.requirePaymentField("paymentId"),
    // 비면 TOPAZ/GEM 어느 쪽 잔액도 갱신되지 않음
    currency = currency.requirePaymentField("currency"),
    rewardAmount = rewardAmount,
    bonusAmount = bonusAmount,
    paidBalance = paidBalance,
    freeBalance = freeBalance,
)

fun ChargeStartedResponse.toDomain(): ChargeStarted = ChargeStarted(
    // 아래 넷은 그대로 결제창(PaymentRequest)으로 넘어감
    storeId = storeId.requirePaymentField("storeId"),
    channelKey = channelKey.requirePaymentField("channelKey"),
    paymentId = paymentId.requirePaymentField("paymentId"),
    amount = amount,
    currency = currency.orEmpty(),
    orderName = orderName.requirePaymentField("orderName"),
)

fun WalletBalancesResponse.toDomain(): List<CurrencyBalance> =
    balances.orEmpty().map { CurrencyBalance(currency = it.currency.orEmpty(), balance = it.balance) }

fun ShopAvatarItemsResponse.toDomain(): List<ShopAvatarItem> = items.orEmpty().map { it.toDomain() }

fun ShopAvatarItemResponse.toDomain(): ShopAvatarItem = ShopAvatarItem(
    id = id,
    slot = slot.orEmpty(),
    name = name.orEmpty(),
    imageUrl = imageUrl,
    currency = currency.orEmpty(),
    price = price,
    owned = owned,
    onSale = onSale,
)

fun MemberAvatarResponse.toDomain(): MemberAvatar = MemberAvatar(
    equipped = equipped.orEmpty().map { it.toDomain() },
)

fun AvatarEquippedItemResponse.toDomain(): AvatarEquippedItem = AvatarEquippedItem(
    slot = slot.orEmpty(),
    itemId = itemId,
    name = name.orEmpty(),
    imageUrl = imageUrl,
)

fun ChargeProductsResponse.toDomain(): List<ChargeProduct> = items.orEmpty().map { it.toDomain() }

fun ChargeProductItemResponse.toDomain(): ChargeProduct = ChargeProduct(
    id = id,
    rewardCurrency = rewardCurrency.orEmpty(),
    rewardAmount = rewardAmount,
    bonusAmount = bonusAmount,
    priceKrw = priceKrw,
    popular = popular,
)

fun ExchangeProductsResponse.toDomain(): List<ExchangeProduct> = items.orEmpty().map { it.toDomain() }

fun ExchangeProductItemResponse.toDomain(): ExchangeProduct = ExchangeProduct(
    id = id,
    fromCurrency = fromCurrency.orEmpty(),
    fromAmount = fromAmount,
    toCurrency = toCurrency.orEmpty(),
    toAmount = toAmount,
)

fun ExchangeResultResponse.toDomain(): ExchangeResult = ExchangeResult(
    fromCurrency = fromCurrency.orEmpty(),
    fromAmount = fromAmount,
    toCurrency = toCurrency.orEmpty(),
    toAmount = toAmount,
    fromBalance = fromBalance,
    toBalance = toBalance,
)
