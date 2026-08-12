package com.li_routi.core.domain.shop

import com.li_routi.core.common.kotlin.util.ResultState

interface ShopRepository {

    /** 아바타 아이템 목록을 조회함. [slot]을 안 주면 전체 탭 */
    suspend fun getAvatarItems(slot: String?, ownedOnly: Boolean?): ResultState<List<ShopAvatarItem>>

    /** 아이템을 사고 그 자리에 바로 입힘. 가격/결제 재화는 서버가 정함 */
    suspend fun purchaseAvatarItem(itemId: Long): ResultState<MemberAvatar>

    /** 충전 상품(현금 결제) 목록을 조회함 */
    suspend fun getChargeProducts(): ResultState<List<ChargeProduct>>

    /** 교환 상품(재화 → 재화) 목록을 조회함 */
    suspend fun getExchangeProducts(): ResultState<List<ExchangeProduct>>

    /**
     * 보유 재화를 다른 재화로 교환함.
     *
     * [idempotencyKey]는 클라이언트가 만들어야 함 — 응답을 못 받아 재시도할 때는 같은 키를,
     * 한 번 더 교환할 때는 새 키를 써야 함. 상품 id로 키를 만들면 두 번째 교환이 조용히 건너뛰어짐
     */
    suspend fun exchangeCurrency(productId: Long, idempotencyKey: String): ResultState<ExchangeResult>
}
