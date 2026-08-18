package com.li_routi.core.domain.shop

import com.li_routi.core.common.kotlin.util.ResultState

interface ShopRepository {

    /** 결제를 시작함. 아직 돈은 오가지 않고 결제 식별자와 금액만 기록됨 */
    suspend fun startCharge(productId: Long): ResultState<ChargeStarted>

    /**
     * 결제창을 마친 뒤 서버에 검증·지급을 요청함.
     *
     * 서버가 포트원에 다시 물어보기 때문에 클라이언트가 무엇을 보내도 결과가 바뀌지 않음.
     * 이미 지급된 결제면 조용히 성공으로 답함(이 요청과 웹훅이 둘 다 오는 게 정상)
     */
    suspend fun completeCharge(paymentId: String): ResultState<ChargeSettled>

    /** 내 재화 잔액을 조회함. 재화 종류마다 한 건씩 항상 전부 내려옴 */
    suspend fun getWalletBalances(): ResultState<List<CurrencyBalance>>

    /** 지금 입고 있는 아이템을 자리별로 조회함. 안 입은 자리는 실리지 않음 */
    suspend fun getMyAvatar(): ResultState<MemberAvatar>

    /** 착장을 저장함. [itemIds]에 없는 자리는 벗겨지므로 항상 전체를 보내야 함 */
    suspend fun equipAvatar(itemIds: List<Long>): ResultState<MemberAvatar>

    /** 상점 상단 탭 목록을 조회함. 회원과 무관하게 같은 목록이 내려옴 */
    suspend fun getShopCategories(): ResultState<List<ShopCategory>>

    /** 아바타 아이템 목록을 조회함. [slot]을 안 주면 전체 탭 */
    suspend fun getAvatarItems(slot: String?, ownedOnly: Boolean?): ResultState<List<ShopAvatarItem>>

    /**
     * 고른 아이템을 한 번에 삼. 재화가 섞여도 됨(재화별로 합산해 각각 차감). 하나라도 막히면
     * 아무것도 사지 않음. 가격/결제 재화는 서버가 정함. 이 자체로는 착용을 바꾸지 않으므로,
     * 산 뒤 원하는 착장을 [equipAvatar]로 저장해야 함
     *
     * [idempotencyKey]는 클라이언트가 만들어야 함 — 응답을 못 받아 재시도할 때는 같은 장바구니에
     * 같은 키를, 장바구니가 바뀌면(아이템 구성이 달라지면) 반드시 새 키를 써야 함. 같은 키로 다른
     * 장바구니를 보내면 거절됨(서버가 조작 방지로 막음)
     */
    suspend fun purchaseItems(itemIds: List<Long>, idempotencyKey: String): ResultState<ShopPurchaseResult>

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
