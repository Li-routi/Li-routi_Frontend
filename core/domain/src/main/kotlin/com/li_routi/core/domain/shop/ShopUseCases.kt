package com.li_routi.core.domain.shop

import com.li_routi.core.common.kotlin.util.ResultState

class StartChargeUseCase(
    private val repository: ShopRepository,
) {
    suspend operator fun invoke(productId: Long): ResultState<ChargeStarted> =
        repository.startCharge(productId)
}

class CompleteChargeUseCase(
    private val repository: ShopRepository,
) {
    suspend operator fun invoke(paymentId: String): ResultState<ChargeSettled> =
        repository.completeCharge(paymentId)
}

class GetWalletBalancesUseCase(
    private val repository: ShopRepository,
) {
    suspend operator fun invoke(): ResultState<List<CurrencyBalance>> =
        repository.getWalletBalances()
}

class GetMyAvatarUseCase(
    private val repository: ShopRepository,
) {
    suspend operator fun invoke(): ResultState<MemberAvatar> = repository.getMyAvatar()
}

class EquipAvatarUseCase(
    private val repository: ShopRepository,
) {
    /** [itemIds]가 곧 전체 착장임. 빈 목록이면 전부 벗음 */
    suspend operator fun invoke(itemIds: List<Long>): ResultState<MemberAvatar> =
        repository.equipAvatar(itemIds)
}

class GetShopCategoriesUseCase(
    private val repository: ShopRepository,
) {
    suspend operator fun invoke(): ResultState<List<ShopCategory>> = repository.getShopCategories()
}

class GetShopAvatarItemsUseCase(
    private val repository: ShopRepository,
) {
    suspend operator fun invoke(slot: String? = null, ownedOnly: Boolean? = null): ResultState<List<ShopAvatarItem>> =
        repository.getAvatarItems(slot = slot, ownedOnly = ownedOnly)
}

class PurchaseShopAvatarItemUseCase(
    private val repository: ShopRepository,
) {
    suspend operator fun invoke(itemId: Long): ResultState<MemberAvatar> =
        repository.purchaseAvatarItem(itemId)
}

class GetChargeProductsUseCase(
    private val repository: ShopRepository,
) {
    suspend operator fun invoke(): ResultState<List<ChargeProduct>> =
        repository.getChargeProducts()
}

class GetExchangeProductsUseCase(
    private val repository: ShopRepository,
) {
    suspend operator fun invoke(): ResultState<List<ExchangeProduct>> =
        repository.getExchangeProducts()
}

class ExchangeCurrencyUseCase(
    private val repository: ShopRepository,
) {
    /** [idempotencyKey]를 안 주면 새 교환으로 보고 매번 새로 만듦 */
    suspend operator fun invoke(
        productId: Long,
        idempotencyKey: String = java.util.UUID.randomUUID().toString(),
    ): ResultState<ExchangeResult> = repository.exchangeCurrency(productId, idempotencyKey)
}
