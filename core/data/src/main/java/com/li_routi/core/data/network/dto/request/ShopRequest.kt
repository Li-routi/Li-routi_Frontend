package com.li_routi.core.data.network.dto.request

/** POST /api/shop/charges 요청 body. */
data class StartChargeRequest(
    val productId: Long,
)

/** POST /api/shop/exchanges 요청 body. */
data class ExchangeRequest(
    val productId: Long,
    /** 재시도 시 중복 차감을 막는 키. 클라이언트가 만들어야 함 */
    val idempotencyKey: String,
)

/** POST /api/shop/items/purchase 요청 body. 최대 30개까지 한 번에 담을 수 있음. */
data class PurchaseItemsRequest(
    val itemIds: List<Long>,
    /** 재시도 시 중복 결제를 막는 키. 클라이언트가 만들어야 함 */
    val idempotencyKey: String,
)
