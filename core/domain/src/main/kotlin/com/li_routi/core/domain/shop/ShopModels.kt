package com.li_routi.core.domain.shop

/** 상점 격자에 뿌릴 아바타 아이템. 보유한 것도 같이 내려와서 [owned]로 가름 */
data class ShopAvatarItem(
    val id: Long,
    val slot: String,
    val name: String,
    val imageUrl: String?,
    val currency: String,
    val price: Long,
    val owned: Boolean,
    /** 판매 종료된 아이템도 보유했으면 목록에 실려서 false로 옴 */
    val onSale: Boolean,
)

/** 구매 직후의 착용 상태. 산 아이템의 자리만 바뀜 */
data class MemberAvatar(
    val equipped: List<AvatarEquippedItem>,
)

data class AvatarEquippedItem(
    val slot: String,
    val itemId: Long,
    val name: String,
    val imageUrl: String?,
)

/** 현금으로 사는 재화 묶음(파란보석 탭). 유상 [rewardAmount]와 무상 [bonusAmount]를 나눠서 보여줌 */
data class ChargeProduct(
    val id: Long,
    val rewardCurrency: String,
    val rewardAmount: Long,
    val bonusAmount: Long,
    val priceKrw: Long,
    val popular: Boolean,
)

/** 재화로 사는 재화 묶음(주황보석 탭). 비율은 묶음마다 다르고 서버가 갖고 있음 */
data class ExchangeProduct(
    val id: Long,
    val fromCurrency: String,
    val fromAmount: Long,
    val toCurrency: String,
    val toAmount: Long,
)

/** 교환 결과. 교환 후 잔액까지 같이 옴 */
data class ExchangeResult(
    val fromCurrency: String,
    val fromAmount: Long,
    val toCurrency: String,
    val toAmount: Long,
    val fromBalance: Long,
    val toBalance: Long,
)
