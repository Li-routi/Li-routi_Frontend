package com.li_routi.core.data.network.dto.request

/** POST /api/shop/exchanges 요청 body. */
data class ExchangeRequest(
    val productId: Long,
    /** 재시도 시 중복 차감을 막는 키. 클라이언트가 만들어야 함 */
    val idempotencyKey: String,
)
