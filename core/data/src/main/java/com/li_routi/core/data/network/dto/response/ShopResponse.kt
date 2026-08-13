package com.li_routi.core.data.network.dto.response

/** POST /api/shop/charges 응답 result. 그대로 포트원 SDK에 넘김 */
data class ChargeStartedResponse(
    val storeId: String?,
    val channelKey: String?,
    val paymentId: String?,
    val amount: Long,
    val currency: String?,
    val orderName: String?,
)

/** POST /api/shop/charges/{paymentId}/complete 응답 result. */
data class ChargeSettledResponse(
    val paymentId: String?,
    val currency: String?,
    val rewardAmount: Int,
    val bonusAmount: Int,
    val paidBalance: Int,
    val freeBalance: Int,
)

/** GET /api/members/me/wallet 응답 result. */
data class WalletBalancesResponse(
    val balances: List<WalletBalanceResponse>?,
)

data class WalletBalanceResponse(
    val currency: String?,
    val balance: Int,
)

/** GET /api/shop/items 응답 result. */
/** GET /api/shop/categories 응답 result. 상점 상단 탭 목록. */
data class ShopCategoriesResponse(
    val categories: List<ShopCategoryResponse>?,
)

data class ShopCategoryResponse(
    val key: String?,
    val name: String?,
    val source: String?,
    val slot: String?,
)

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
