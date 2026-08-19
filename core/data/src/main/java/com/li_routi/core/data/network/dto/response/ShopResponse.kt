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

/** GET /api/members/me/avatar, PUT /api/members/me/avatar 응답 result. 현재 착용 상태. */
data class MemberAvatarResponse(
    val equipped: List<AvatarEquippedItemResponse>?,
    val layers: List<AvatarLayerResponse>?,
)

/** 겹쳐 그릴 레이어 한 장. 개인/그룹 구성원 아바타가 같은 스키마를 씀 */
data class AvatarLayerResponse(
    val layer: String?,
    val imageUrl: String?,
)

data class AvatarEquippedItemResponse(
    val slot: String?,
    val itemId: Long,
    val name: String?,
    val imageUrl: String?,
)

/** POST /api/shop/items/purchase 응답 result. 이 자체로는 착용을 바꾸지 않음. */
data class ShopPurchaseResultResponse(
    val purchasedItemIds: List<Long>?,
    val payments: List<ShopPurchasePaymentResponse>?,
)

data class ShopPurchasePaymentResponse(
    val currency: String?,
    val paidAmount: Int,
    val balanceAfter: Int,
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
