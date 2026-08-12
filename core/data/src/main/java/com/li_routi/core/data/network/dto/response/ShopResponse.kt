package com.li_routi.core.data.network.dto.response

/** GET /api/members/me/wallet 응답 result. */
data class WalletBalancesResponse(
    val balances: List<WalletBalanceResponse>?,
)

data class WalletBalanceResponse(
    val currency: String?,
    val balance: Int,
)

/** GET /api/shop/items 응답 result. */
data class ShopAvatarItemsResponse(
    val items: List<ShopAvatarItemResponse>?,
)

data class ShopAvatarItemResponse(
    val id: Long,
    val slot: String?,
    val name: String?,
    val imageUrl: String?,
    val currency: String?,
    val price: Long,
    val owned: Boolean,
    val onSale: Boolean,
)

/** POST /api/shop/items/{itemId}/purchase 응답 result. 구매 후의 착용 상태. */
data class MemberAvatarResponse(
    val equipped: List<AvatarEquippedItemResponse>?,
)

data class AvatarEquippedItemResponse(
    val slot: String?,
    val itemId: Long,
    val name: String?,
    val imageUrl: String?,
)

/** GET /api/shop/charge-products 응답 result. */
data class ChargeProductsResponse(
    val items: List<ChargeProductItemResponse>?,
)

data class ChargeProductItemResponse(
    val id: Long,
    val rewardCurrency: String?,
    val rewardAmount: Long,
    val bonusAmount: Long,
    val priceKrw: Long,
    val popular: Boolean,
)

/** GET /api/shop/exchange-products 응답 result. */
data class ExchangeProductsResponse(
    val items: List<ExchangeProductItemResponse>?,
)

data class ExchangeProductItemResponse(
    val id: Long,
    val fromCurrency: String?,
    val fromAmount: Long,
    val toCurrency: String?,
    val toAmount: Long,
)

/** POST /api/shop/exchanges 응답 result. */
data class ExchangeResultResponse(
    val fromCurrency: String?,
    val fromAmount: Long,
    val toCurrency: String?,
    val toAmount: Long,
    val fromBalance: Long,
    val toBalance: Long,
)
