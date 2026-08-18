package com.li_routi.core.domain.shop

/**
 * 상점 상단 탭 한 개. 받은 순서대로 그리면 됨 — 서버가 정렬해서 내려줌.
 *
 * 탭 이름으로 분기하면 안 됨. 탭이 늘거나 줄어도 앱을 안 고치려고 서버가 목록으로 내려주는 것임.
 * [slot]이 비어 있는 탭이 `전체`와 `캐릭터` 둘이라 [source]로 갈라야 함
 */
data class ShopCategory(
    val key: String,
    val name: String,
    /** `ITEM`이면 아이템 목록, `CHARACTER`면 캐릭터 목록 */
    val source: String,
    /** 아이템 목록을 요청할 때 같이 보낼 자리. 비어 있으면 전체 */
    val slot: String?,
)

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

/**
 * 여러 아이템을 한 번에 산 결과. 이 자체로는 착용을 바꾸지 않음 —
 * 착용은 [ShopRepository.equipAvatar]가 따로 맡음
 */
data class ShopPurchaseResult(
    /** 이번에 산 아이템 id. 요청한 순서 그대로 옴 */
    val purchasedItemIds: List<Long>,
    /** 재화별 결제 내역. 한 재화로만 샀으면 한 줄 */
    val payments: List<ShopPurchasePayment>,
)

/** 재화 한 종류의 결제 내역 */
data class ShopPurchasePayment(
    val currency: String,
    val paidAmount: Int,
    /** 결제 후 잔액. 화면 상단 잔액을 이 값으로 갱신하면 됨 */
    val balanceAfter: Int,
)

data class AvatarEquippedItem(
    val slot: String,
    val itemId: Long,
    val name: String,
    val imageUrl: String?,
)

/**
 * 결제 시작 응답. 이 값을 그대로 포트원 SDK에 넘기면 됨 —
 * 상점 아이디·채널 키를 프론트가 따로 관리하지 않게 서버가 내려줌
 */
data class ChargeStarted(
    val storeId: String,
    val channelKey: String,
    val paymentId: String,
    val amount: Long,
    val currency: String,
    val orderName: String,
)

/**
 * 결제 검증·지급 결과. 지급 후 잔액까지 실려 와서 잔액 조회를 따로 부를 필요가 없음.
 * 화면에 하나로 보여줄 때는 유상([paidBalance])과 무상([freeBalance])을 더하면 됨
 */
data class ChargeSettled(
    val paymentId: String,
    val currency: String,
    val rewardAmount: Int,
    val bonusAmount: Int,
    val paidBalance: Int,
    val freeBalance: Int,
) {
    val totalBalance: Int get() = paidBalance + freeBalance
}

/** 상점 헤더에 표시할 재화 잔액. 한 번도 받은 적 없는 재화도 0으로 실려서 옴 */
data class CurrencyBalance(
    val currency: String,
    val balance: Int,
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
