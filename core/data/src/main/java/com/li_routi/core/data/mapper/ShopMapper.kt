package com.li_routi.core.data.mapper

import com.li_routi.core.data.network.dto.response.AvatarEquippedItemResponse
import com.li_routi.core.data.network.dto.response.ChargeProductItemResponse
import com.li_routi.core.data.network.dto.response.ChargeProductsResponse
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
import com.li_routi.core.domain.shop.ChargeStarted
import com.li_routi.core.domain.shop.CurrencyBalance
import com.li_routi.core.domain.shop.ExchangeProduct
import com.li_routi.core.domain.shop.ExchangeResult
import com.li_routi.core.domain.shop.MemberAvatar
import com.li_routi.core.domain.shop.ShopAvatarItem

fun ChargeStartedResponse.toDomain(): ChargeStarted = ChargeStarted(
    storeId = storeId.orEmpty(),
    channelKey = channelKey.orEmpty(),
    paymentId = paymentId.orEmpty(),
    amount = amount,
    currency = currency.orEmpty(),
    orderName = orderName.orEmpty(),
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
